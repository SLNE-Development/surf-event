package dev.slne.surf.event.anarchy.finale

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.scope
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.key
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.messages.adventure.showTitle
import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.api.paper.util.forEachPlayer
import dev.slne.surf.event.anarchy.plugin
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import dev.slne.surf.event.anarchy.util.formatCountdownTime
import dev.slne.surf.event.anarchy.util.geilesRot
import dev.slne.surf.event.anarchy.vertborder.VertBorderManager
import dev.slne.surf.event.anarchy.vertborder.util.VerticalBorderAlignment
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import org.bukkit.GameMode
import org.bukkit.World
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * The whole finale is driven by a single one-second tick task.
 *
 * - While a finale is [scheduled][scheduleFinale] but not yet started, the tick broadcasts a
 *   countdown at sensible marks (see [countdownMarks]) and, from [WARN_BEFORE_START] onwards, warns
 *   that the nether/end will close and non spectator/creative players will die.
 * - The moment the scheduled time passes, the tick flips [started] and runs [beginFinale] once:
 *   nether + end access is closed and both the horizontal world border and the vertical border
 *   start shrinking over [BORDER_SHRINK].
 * - While the finale runs, every tick applies escalating damage to players still in the nether/end
 *   (see [dimensionDamagePerSecond]): gentle at first so you can flee within ~[FLEE_WINDOW], then
 *   ramping past what natural regeneration can outpace so lingering is lethal.
 */
object FinaleLifecycle {
    private val WARN_BEFORE_START = 1.hours
    private val BORDER_SHRINK = 1.hours

    private const val HORIZONTAL_BORDER_SIZE = 25.0
    private const val VERTICAL_BORDER_BOTTOM = 50.0
    private const val VERTICAL_BORDER_TOP = 100.0

    /** How long a full-health player should roughly have to escape the nether/end before it turns lethal. */
    private val FLEE_WINDOW = 5.minutes

    /** Peak damage per second reached at the end of [FLEE_WINDOW] — far above any regeneration. */
    private const val MAX_DIMENSION_DPS = 10.0

    private val DAMAGED_GAME_MODES = setOf(GameMode.SURVIVAL, GameMode.ADVENTURE)

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

    fun scheduleFinale(date: ZonedDateTime) {
        startAt = date
        startedAt = null
        started = false
        announcedMarks.clear()

        // Suppress marks that already lie in the past relative to when the finale was scheduled, so
        // scheduling with 3 days left does not immediately spam the 4/5/6/7 day marks.
        val initialSeconds = ceilSeconds(Duration.between(ZonedDateTime.now(), date))
        countdownMarks.forEach { if (it > initialSeconds) announcedMarks.add(it) }
    }

    fun cancelFinale() {
        startAt = null
        startedAt = null
        started = false
        announcedMarks.clear()
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
                appendAnarchyPrefix()
                info("Das Finale startet in ")
                variableValue(label)
                info("!")

                if (warn) {
                    appendNewline()
                    error("Der Nether und das End werden geschlossen — ")
                    error("verlasse sie rechtzeitig, sonst nimmst du tödlichen Schaden!")
                }
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
                appendAnarchyPrefix()
                info("Das Finale hat ")
                variableValue("begonnen")
                info("!")
                appendNewline()
                error("Der Nether und das End sind jetzt geschlossen — verlasse sie sofort, sonst stirbst du!")
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
        if (damage <= 0.0) return

        forEachPlayer { player ->
            player.scheduler.run(plugin, {
                if (player.isDead || player.gameMode !in DAMAGED_GAME_MODES) return@run

                val environment = player.world.environment
                if (environment != World.Environment.NETHER && environment != World.Environment.THE_END) {
                    return@run
                }

                player.damage(damage)
                player.sendActionBar(buildText {
                    error("Verlasse den Nether und das End, sonst stirbst du!")
                })
            }, null)
        }
    }

    /**
     * Escalating damage-per-second for players still in the nether/end. Ramps quadratically from 0
     * to [MAX_DIMENSION_DPS] over [FLEE_WINDOW]: negligible right after the start (time to flee),
     * then quickly outpacing natural regeneration so staying is fatal.
     */
    private fun dimensionDamagePerSecond(elapsed: Duration): Double {
        val progress = (elapsed.toMillis().toDouble() / FLEE_WINDOW.inWholeMilliseconds).coerceIn(0.0, 1.0)
        return MAX_DIMENSION_DPS * progress * progress
    }

    private suspend fun shrinkBorders() {
        val overworld = server.worlds.first { it.environment == World.Environment.NORMAL }

        withContext(plugin.globalRegionDispatcher) {
            with(overworld.worldBorder) {
                setCenter(0.0, 0.0)
                setSize(HORIZONTAL_BORDER_SIZE, BORDER_SHRINK.inWholeSeconds)
            }
        }

        VertBorderManager.moveBorderTo(
            overworld,
            VerticalBorderAlignment.BOTTOM,
            VERTICAL_BORDER_BOTTOM,
            BORDER_SHRINK
        )
        VertBorderManager.moveBorderTo(
            overworld,
            VerticalBorderAlignment.TOP,
            VERTICAL_BORDER_TOP,
            BORDER_SHRINK
        )
    }

    private fun ceilSeconds(duration: Duration) = (duration.toMillis() + 999) / 1000
}
