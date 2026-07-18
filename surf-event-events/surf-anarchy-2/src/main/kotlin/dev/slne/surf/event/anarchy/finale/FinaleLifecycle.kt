package dev.slne.surf.event.anarchy.finale

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.scope
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.messages.adventure.showTitle
import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.api.paper.util.BukkitSound
import dev.slne.surf.api.paper.util.forEachPlayer
import dev.slne.surf.event.anarchy.config.AnarchyConfig
import dev.slne.surf.event.anarchy.plugin
import dev.slne.surf.event.anarchy.util.appendAnarchyBar
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import dev.slne.surf.event.anarchy.util.formatCountdownTime
import dev.slne.surf.event.anarchy.util.geilesRot
import dev.slne.surf.event.anarchy.vertborder.VertBorderManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.GameMode
import org.bukkit.World
import org.bukkit.entity.Player
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

    private const val OVERWORLD_FINALE_BORDER_SIZE = 3000.0
    private const val OVERWORLD_RESET_SIZE = 7500.0

    private const val FINALE_DAMAGE_FLOOR = 4.0
    private const val FINALE_DAMAGE_MAX = 50.0
    private val FINALE_DAMAGE_RAMP = 60.seconds

    var finaleDuration: kotlin.time.Duration = DEFAULT_FINALE_DURATION
        private set

    private val USER_GAME_MODES = setOf(GameMode.SURVIVAL, GameMode.ADVENTURE)
    private val countdownMarks = listOf(
        7.days, 6.days, 5.days, 4.days, 3.days, 2.days, 1.days,
        12.hours, 6.hours, 3.hours, 2.hours, 1.hours,
        30.minutes, 15.minutes, 10.minutes, 5.minutes, 4.minutes, 3.minutes, 1.minutes,
        30.seconds, 20.seconds, 10.seconds, 3.seconds, 2.seconds, 1.seconds
    ).map { it.inWholeSeconds }

    private var startAt: ZonedDateTime? = null
    private var startedAt: ZonedDateTime? = null
    private var started = false
    private val announcedMarks = ConcurrentHashMap.newKeySet<Long>()

    private var overworldLockedAt: ZonedDateTime? = null

    enum class OverworldLockResult { OK, NOT_RUNNING, ALREADY_LOCKED }

    private lateinit var tickJob: Job

    suspend fun create() {
        restoreFromConfig()
        tickJob = plugin.scope.runAtFixedRate(1.seconds) { tick() }
    }

    fun cancel() {
        if (::tickJob.isInitialized && tickJob.isActive) {
            tickJob.cancel()
        }
    }

    fun isScheduled() = startAt != null && !started
    fun isRunning() = started
    fun isOverworldLocked() = overworldLockedAt != null

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
        applySchedule(date, duration)

        AnarchyConfig.edit {
            finaleConfig.start = date
            finaleConfig.finaleDurationMinutes = duration.inWholeMinutes.toInt()
            finaleConfig.startedAt = null
        }
    }

    private fun applySchedule(date: ZonedDateTime, duration: kotlin.time.Duration) {
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
        finaleDuration = DEFAULT_FINALE_DURATION
        announcedMarks.clear()
        overworldLockedAt = null

        AnarchyConfig.edit {
            finaleConfig.start = null
            finaleConfig.finaleDurationMinutes = null
            finaleConfig.startedAt = null
            finaleConfig.overworldLockedAt = null
        }
    }

    private suspend fun restoreFromConfig() {
        val config = AnarchyConfig.getConfig().finaleConfig
        val savedStart = config.start ?: return
        val savedStartedAt = config.startedAt
        val duration = config.finaleDurationMinutes?.minutes ?: DEFAULT_FINALE_DURATION

        if (savedStartedAt != null) {
            // Finale lief bereits vor dem Neustart -> laufenden Zustand fortsetzen
            startAt = savedStart
            startedAt = savedStartedAt
            started = true
            finaleDuration = duration
            announcedMarks.clear()

            setOverworldFinaleBorder()

            overworldLockedAt = config.overworldLockedAt
        } else {
            applySchedule(savedStart, duration)
        }
    }

    fun resetAll() {
        cancelFinale()

        server.worlds.firstOrNull { it.environment == World.Environment.NORMAL }?.let { overworld ->
            with(overworld.worldBorder) {
                size = OVERWORLD_RESET_SIZE
                setCenter(0.0, 0.0)
            }
            VertBorderManager.clearBorders(overworld)
        }

        server.worlds.firstOrNull { it.environment == World.Environment.THE_END }?.let { end ->
            VertBorderManager.clearBorders(end)
        }
    }

    private suspend fun tick() {
        val start = startAt ?: return

        if (started) {
            damageNetherPlayers()
            damageOverworldPlayers()
            return
        }

        val now = ZonedDateTime.now()
        if (now.isBefore(start)) {
            broadcastCountdown(Duration.between(now, start))
        } else {
            started = true
            startedAt = now
            AnarchyConfig.edit { finaleConfig.startedAt = now }
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
                variableValue(label, TextDecoration.BOLD)
                info("!")
                appendNewline()

                if (warn) {
                    appendAnarchyPrefix()
                    appendNewline()

                    appendAnarchyPrefix()
                    error("Der Nether wird geschlossen -".toSmallCaps())
                    appendNewline()

                    appendAnarchyPrefix()
                    error("verlasse ihn rechtzeitig!".toSmallCaps())
                    appendNewline()

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
                    type(BukkitSound.BLOCK_NOTE_BLOCK_PLING)
                    pitch(0f)
                }
            }
        }
    }

    private suspend fun beginFinale() {
        forEachPlayer { player ->
            player.showTitle {
                title { geilesRot("Last man standing.") }
                subtitle { spacer("Überlebe so lange du kannst...") }
            }

            player.sendText {
                appendAnarchyBar()
                appendNewline()

                appendAnarchyPrefix()
                appendNewline()


                appendAnarchyPrefix()
                info("Das Finale hat begonnen.", TextDecoration.BOLD)
                appendNewline()

                appendAnarchyPrefix()
                appendNewline()

                appendAnarchyPrefix()
                error("Der Nether ist nun geschlossen -".toSmallCaps())
                appendNewline()

                appendAnarchyPrefix()
                error("verlasse ihn sofort!".toSmallCaps())
                appendNewline()

                appendAnarchyPrefix()
                appendNewline()

                appendAnarchyBar()
            }

            player.playSound(true) {
                type(BukkitSound.BLOCK_NOTE_BLOCK_PLING)
                pitch(1f)
            }

            player.playSound(true) {
                type(BukkitSound.ENTITY_WITHER_SPAWN)
                pitch(0f)
            }
        }

        setOverworldFinaleBorder()
    }

    fun lockOverworld(): OverworldLockResult {
        if (!started) return OverworldLockResult.NOT_RUNNING
        if (overworldLockedAt != null) return OverworldLockResult.ALREADY_LOCKED

        val now = ZonedDateTime.now()
        overworldLockedAt = now

        AnarchyConfig.edit {
            finaleConfig.overworldLockedAt = now
        }

        broadcastOverworldLock()

        return OverworldLockResult.OK
    }

    private fun broadcastOverworldLock() {
        forEachPlayer { player ->
            player.sendText {
                appendAnarchyBar()
                appendNewline()

                appendAnarchyPrefix()
                appendNewline()

                appendAnarchyPrefix()
                error("Die Overworld ist nun gesperrt.".toSmallCaps())
                appendNewline()

                appendAnarchyPrefix()
                appendNewline()

                appendAnarchyBar()
            }

            player.playSound(true) {
                type(BukkitSound.ENTITY_WITHER_SPAWN)
                pitch(0f)
            }
        }
    }

    private fun damageNetherPlayers() =
        damageEnvironment(startedAt, World.Environment.NETHER)

    private fun damageOverworldPlayers() =
        damageEnvironment(overworldLockedAt, World.Environment.NORMAL)

    private fun damageEnvironment(since: ZonedDateTime?, environment: World.Environment) {
        val start = since ?: return
        val damage = finaleDamagePerSecond(Duration.between(start, ZonedDateTime.now()))
        if (damage <= 0.0) {
            return
        }

        forEachPlayer { player ->
            player.scheduler.run(plugin, {
                if (player.isDead || player.gameMode !in USER_GAME_MODES) {
                    return@run
                }

                if (player.world.environment != environment) {
                    return@run
                }

                applyTrueDamage(player, damage)
            }, null)
        }
    }

    private fun applyTrueDamage(player: Player, amount: Double) {
        val newHealth = player.health - amount
        if (newHealth <= 0.0) {
            player.health = 0.0
        } else {
            player.health = newHealth
            player.playHurtAnimation(0f)
        }
    }

    private fun finaleDamagePerSecond(elapsed: Duration): Double {
        val progress =
            (elapsed.toMillis().toDouble() / FINALE_DAMAGE_RAMP.inWholeMilliseconds)
                .coerceIn(0.0, 1.0)
        return FINALE_DAMAGE_FLOOR + (FINALE_DAMAGE_MAX - FINALE_DAMAGE_FLOOR) * sqrt(progress)
    }

    private fun overworldOrNull() =
        server.worlds.firstOrNull { it.environment == World.Environment.NORMAL }

    private suspend fun setOverworldFinaleBorder() {
        val overworld = overworldOrNull() ?: return

        withContext(plugin.globalRegionDispatcher) {
            with(overworld.worldBorder) {
                setCenter(0.0, 0.0)
                size = OVERWORLD_FINALE_BORDER_SIZE
            }
        }
    }

    private fun ceilSeconds(duration: Duration) = (duration.toMillis() + 999) / 1000
}
