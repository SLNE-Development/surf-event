package dev.slne.surf.event.anarchy.finale

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.scope
import dev.slne.surf.api.core.messages.adventure.*
import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.api.paper.util.forEachPlayer
import dev.slne.surf.event.anarchy.plugin
import dev.slne.surf.event.anarchy.util.appendAnarchyBar
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import dev.slne.surf.event.anarchy.util.formatCountdownTime
import dev.slne.surf.event.anarchy.util.geilesRot
import dev.slne.surf.event.anarchy.vertborder.VertBorderManager
import dev.slne.surf.event.anarchy.vertborder.border.VerticalBorderAlignment
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.World
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object FinaleLifecycle {
    private val WARN_BEFORE_START = 1.hours
    val DEFAULT_FINALE_DURATION = 5.minutes

    var finaleDuration: kotlin.time.Duration = DEFAULT_FINALE_DURATION
        private set

    private val USER_GAME_MODES = setOf(GameMode.SURVIVAL, GameMode.ADVENTURE)
    private val countdownMarks = listOf(
        7.days, 6.days, 5.days, 4.days, 3.days, 2.days, 1.days,
        12.hours, 6.hours, 3.hours, 2.hours, 1.hours,
        30.minutes, 15.minutes, 10.minutes, 5.minutes, 4.minutes, 3.minutes, 2.minutes, 1.minutes,
        30.seconds, 20.seconds, 10.seconds, 5.seconds, 4.seconds, 3.seconds, 2.seconds, 1.seconds
    ).map { it.inWholeSeconds }

    private var startAt: ZonedDateTime? = null
    private var startedAt: ZonedDateTime? = null
    private var started = false
    private val announcedMarks = ConcurrentHashMap.newKeySet<Long>()

    private lateinit var tickJob: Job

    fun create() {
        tickJob = plugin.scope.runAtFixedRate(1.seconds) { tick() }
    }

    fun cancel() {
        if (::tickJob.isInitialized && tickJob.isActive) {
            tickJob.cancel()
        }
    }

    fun isScheduled() = startAt != null && !started
    fun isRunning() = started

    fun secondsUntilStart(): Long? {
        val start = startAt ?: return null
        if (started) return null
        return ceilSeconds(Duration.between(ZonedDateTime.now(), start)).coerceAtLeast(0)
    }

    fun secondsUntilFinalBorder(): Long? {
        val since = startedAt ?: return null
        val elapsed = Duration.between(since, ZonedDateTime.now())
        val remaining = finaleDuration.inWholeSeconds - elapsed.seconds
        return remaining.coerceAtLeast(0)
    }

    fun scheduleFinale(
        date: ZonedDateTime,
        duration: kotlin.time.Duration = DEFAULT_FINALE_DURATION
    ) {
        startAt = date
        startedAt = null
        started = false
        finaleDuration = duration
        announcedMarks.clear()

        val initialSeconds = ceilSeconds(Duration.between(ZonedDateTime.now(), date))
        countdownMarks.forEach { if (it > initialSeconds) announcedMarks.add(it) }
    }

    fun cancelFinale() {
        startAt = null
        startedAt = null
        started = false
        announcedMarks.clear()
    }

    fun resetAll() {
        val world = Bukkit.getWorlds().first()

        cancelFinale()
        with(world.worldBorder) {
            size = 5000.0
            setCenter(0.0, 0.0)
        }

        VertBorderManager.clearBorders(world)
    }

    private suspend fun tick() {
        val start = startAt ?: return

        if (started) {
            damageDimensionPlayers()
            return
        }

        val now = ZonedDateTime.now()
        if (now.isBefore(start)) {
            broadcastCountdown(Duration.between(now, start))
        } else {
            started = true
            startedAt = now
            beginFinale()
        }
    }

    private fun broadcastCountdown(remaining: Duration) {
        val remainingSeconds = ceilSeconds(remaining)
        val crossed = countdownMarks.filter { it >= remainingSeconds && announcedMarks.add(it) }
        val mark = crossed.minOrNull() ?: return

        val label = formatCountdownTime(mark)
        val warn = mark <= WARN_BEFORE_START.inWholeSeconds

        forEachPlayer { player ->
            player.sendText {
                appendAnarchyBar()
                appendNewline()

                appendAnarchyPrefix()
                appendNewline()


                appendAnarchyPrefix()
                info("Das Finale startet in ")
                variableValue(label)
                info("!")
                appendNewline()

                if (warn) {
                    appendAnarchyPrefix()
                    appendNewline()

                    appendAnarchyPrefix()
                    error("Der Nether und das End werden")
                    appendNewline()

                    appendAnarchyPrefix()
                    error("geschlossen — verlasse sie rechtzeitig,")
                    appendNewline()

                    appendAnarchyPrefix()
                    error("sonst nimmst du tödlichen Schaden!")
                    appendNewline()
                }

                appendAnarchyPrefix()
                appendNewline()

                appendAnarchyBar()
            }

            if (mark <= 10) {
                player.showTitle {
                    title { geilesRot(mark.toString()) }
                    subtitle { spacer("Das Finale startet in $label...") }
                }
            }

            player.playSound(true) {
                type(key("nexo", "countdown"))
            }
        }
    }

    private suspend fun beginFinale() {
        forEachPlayer { player ->
            player.showTitle {
                title { geilesRot("Finale") }
                subtitle { spacer("Das Finale hat begonnen!") }
            }

            player.sendText {
                appendAnarchyBar()
                appendNewline()

                appendAnarchyPrefix()
                appendNewline()


                appendAnarchyPrefix()
                info("Das Finale hat begonnen.")
                appendNewline()

                appendAnarchyPrefix()
                appendNewline()

                appendAnarchyPrefix()
                error("Der Nether und das End sind")
                appendNewline()

                appendAnarchyPrefix()
                error("nun geschlossen — verlasse sie")
                appendNewline()

                appendAnarchyPrefix()
                error("sofort, sonst stirbst du!")
                appendNewline()

                appendAnarchyPrefix()
                appendNewline()

                appendAnarchyBar()
            }

            player.playSound(true) {
                type(key("nexo", "start"))
            }
        }

        shrinkBorders()
    }

    private fun damageDimensionPlayers() {
        val since = startedAt ?: return
        val damage = dimensionDamagePerSecond(Duration.between(since, ZonedDateTime.now()))
        if (damage <= 0.0) {
            return
        }

        forEachPlayer { player ->
            player.scheduler.run(plugin, {
                if (player.isDead || player.gameMode !in USER_GAME_MODES) {
                    return@run
                }

                val environment = player.world.environment
                if (environment != World.Environment.NETHER && environment != World.Environment.THE_END) {
                    return@run
                }

                player.damage(damage)
                player.sendActionBar(buildText {
                    error("⚠")
                    appendSpace()
                    darkSpacer("|")
                    appendSpace()
                    geilesRot("Verlasse den Nether und das End, sonst stirbst du!")
                })
            }, null)
        }
    }

    private fun dimensionDamagePerSecond(elapsed: Duration): Double {
        val progress =
            (elapsed.toMillis().toDouble() / 5.minutes.inWholeMilliseconds).coerceIn(0.0, 1.0)
        return 50.0 * sqrt(progress)
    }

    private suspend fun shrinkBorders() {
        val overworld = server.worlds.first { it.environment == World.Environment.NORMAL }

        withContext(plugin.globalRegionDispatcher) {
            with(overworld.worldBorder) {
                setCenter(0.0, 0.0)
                changeSize(50.0, finaleDuration.inWholeSeconds * 20)
            }
        }

        VertBorderManager.moveBorderTo(
            overworld,
            VerticalBorderAlignment.BOTTOM,
            50.0,
            finaleDuration
        )

        VertBorderManager.moveBorderTo(
            overworld,
            VerticalBorderAlignment.TOP,
            100.0,
            finaleDuration
        )
    }

    private fun ceilSeconds(duration: Duration) = (duration.toMillis() + 999) / 1000
}
