package dev.slne.surf.event.bmbf

import com.github.shynixn.mccoroutine.folia.ticks
import com.plotsquared.core.plot.Plot
import com.plotsquared.core.plot.flag.implementations.DoneFlag
import dev.slne.surf.event.bmbf.papi.countdown.BmbfCountdownPlaceholder
import dev.slne.surf.event.bmbf.util.teleportPlayerSuspend
import dev.slne.surf.event.bmbf.util.toPlotPlayer
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.bukkit.api.util.dispatcher
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayerInRegion
import dev.slne.surf.surfapi.core.api.messages.adventure.Sound
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.longSetOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.sound.Sound.Emitter
import net.kyori.adventure.sound.Sound.Source
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object BmbfManager {
    var running = false
        private set

    var firstRun = true
        private set

    var currentCategory: BmbfCategory = BmbfCategory.entries.first()
        private set
    var currentChallenge: BmbfChallenge = BmbfChallenge.MINUTE_1
        private set

    fun selectNextCategory(category: BmbfCategory, challenge: BmbfChallenge) {
        firstRun = true
        currentCategory = category
        currentChallenge = challenge
    }

    fun getPlotForPlayer(player: Player): Plot? {
        val plotArea = currentCategory.getPlotArea(currentChallenge)
        val uuid = player.uniqueId
        val plots = plotArea.getPlots(uuid)

        if (plots.isNotEmpty()) {
            return plots.first()
        }

        return null
    }

    fun getOrCreatePlotForPlayer(player: Player): Plot {
        val createdPlot = getPlotForPlayer(player)
        if (createdPlot != null) {
            return createdPlot
        }

        val plotArea = currentCategory.getPlotArea(currentChallenge)
        val plotPlayer = player.toPlotPlayer()

        val plot = plotArea.getNextFreePlot(plotPlayer, null)
        plot.claim(plotPlayer, false, null, true, true)
        val doneFlag = plot.flagContainer.getFlag(DoneFlag::class.java)
            .createFlagInstance((System.currentTimeMillis() / 1000).toString())
        plot.setFlag(doneFlag)

        return plot
    }

    suspend fun startNext() {
        if (running) return
        if (!firstRun) {
            val nextChallenge = currentChallenge.getNext()
            if (nextChallenge != null) {
                currentChallenge = nextChallenge
            } else {
                currentCategory = currentCategory.nextCategory() ?: return
                currentChallenge = BmbfChallenge.MINUTE_1
            }

        }
        val previousFirstRun = firstRun
        firstRun = false
        running = true

        forEachPlayerInRegion({ player ->
            player.updateCommands()
            val plot = getOrCreatePlotForPlayer(player)
            plot.teleportPlayerSuspend(player.toPlotPlayer())
        }, concurrent = true)

        startCountdown(previousFirstRun)
    }

    private suspend fun startCountdown(firstRun: Boolean) {
        for (currentSecond in 10 downTo 0) {
            if (!running) return
            BmbfCountdownPlaceholder.current = currentSecond.seconds
            server.sendText {
                appendPrefix()
                info("In ")
                variableValue(currentSecond.toString())
                info(" Sekunden geht es mit der Kategorie ")
                variableValue(currentCategory.displayName)
                append {
                    variableValue(" (")
                    val minutes = currentChallenge.eventDuration.inWholeMinutes
                    if (minutes == 1L) {
                        variableValue("1 Minute")
                    } else {
                        variableValue("$minutes Minuten")
                    }
                    variableValue(")")
                }

                if (firstRun) {
                    info(" los!")
                } else {
                    info(" weiter!")
                }

            }

            playCountdownSound()
            delay(1.seconds)
        }

        startBuilding()
    }

    private suspend fun startBuilding() {
        if (!running) return

        forEachPlayerInRegion({ player ->
            player.gameMode = GameMode.CREATIVE
            val plot = getOrCreatePlotForPlayer(player)
            plot.removeFlag(DoneFlag::class.java)
        }, concurrent = true)

        for (currentSecond in currentChallenge.eventDuration.inWholeSeconds downTo 0) {
            if (!running) return
            val currentDuration = currentSecond.seconds
            BmbfCountdownPlaceholder.current = currentDuration

            if (announceTimesSeconds.contains(currentSecond)) {
                server.sendText {
                    appendPrefix()
                    info("Ihr habt noch ")
                    variableValue(currentDuration.toString())
                    info(" Zeit!")
                }

                playCountdownSound()
            }

            delay(1.seconds)
        }

        endBuilding()
    }

    private suspend fun endBuilding() {
        running = false
        BmbfCountdownPlaceholder.current = Duration.ZERO

        forEachPlayerInRegion({ player ->
            player.updateCommands()
            val plot = getPlotForPlayer(player) ?: return@forEachPlayerInRegion
            val doneFlag = plot.flagContainer.getFlag(DoneFlag::class.java)
                .createFlagInstance((System.currentTimeMillis() / 1000).toString())
            plot.setFlag(doneFlag)
        }, concurrent = true)

        server.sendText {
            appendPrefix()
            info("Die Zeit ist um!")
        }
        playFinishSound()

        if (!hasNextRound()) {
            reset()
        }
    }

    private fun reset() {
        running = false
        BmbfCountdownPlaceholder.current = Duration.ZERO
        currentCategory = BmbfCategory.entries.first()
        currentChallenge = BmbfChallenge.MINUTE_1
        firstRun = true
    }

    suspend fun cancel() {
        reset()
        forEachPlayerInRegion({ player ->
            player.updateCommands()
            val plot = getPlotForPlayer(player) ?: return@forEachPlayerInRegion
            val doneFlag = plot.flagContainer.getFlag(DoneFlag::class.java)
                .createFlagInstance((System.currentTimeMillis() / 1000).toString())
            plot.setFlag(doneFlag)
        }, concurrent = true)

        server.sendText {
            appendPrefix()
            error("Das Event wurde abgebrochen.")
        }
    }

    private suspend fun playCountdownSound() {
        forEachPlayerInRegion({
            it.playSound(Sound {
                type(Sound.BLOCK_NOTE_BLOCK_PLING)
                pitch(2f)
                volume(0.5f)
                source(Source.BLOCK)
            }, Emitter.self())
        }, concurrent = true)
    }

    private suspend fun playFinishSound() {
        forEachPlayerInRegion({
            it.playSound(Sound {
                type(Sound.ENTITY_ENDER_DRAGON_DEATH)
                pitch(0.75f)
                volume(0.75f)
                source(Source.HOSTILE)
            }, Emitter.self())
        }, concurrent = true)
    }

    fun hasNextRound(): Boolean {
        return currentChallenge.getNext() != null || currentCategory.nextCategory() != null
    }

    suspend fun configurePlayerOnJoin(player: Player) {
        delay(3.ticks)
        withContext(player.dispatcher()) {
            player.gameMode = GameMode.CREATIVE
        }

        if (running) {
            player.sendText {
                appendPrefix()
                info("Das Event läuft bereits!")
                appendNewPrefixedLine()
                info("Aktuelle Kategorie: ")
                variableValue(currentCategory.displayName)
                append {
                    variableValue(" (")
                    val minutes = currentChallenge.eventDuration.inWholeMinutes
                    if (minutes == 1L) {
                        variableValue("1 Minute")
                    } else {
                        variableValue("$minutes Minuten")
                    }
                    variableValue(")")
                }
            }
        }

        val plot = getOrCreatePlotForPlayer(player)
        if (running && DoneFlag.isDone(plot)) {
            plot.removeFlag(DoneFlag::class.java)
        }

        plot.teleportPlayerSuspend(player.toPlotPlayer())
    }

    private val announceTimesSeconds =
        longSetOf(900, 600, 300, 60, 30, 15, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1)
}