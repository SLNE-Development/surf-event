package dev.slne.surf.event.anarchy.vertborder

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.messages.adventure.showTitle
import dev.slne.surf.api.paper.util.BukkitSound
import dev.slne.surf.event.anarchy.plugin
import dev.slne.surf.event.anarchy.util.geilesRot
import dev.slne.surf.event.anarchy.vertborder.util.VerticalBorderAlignment
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.GameMode
import org.bukkit.Particle
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object VertBorderService {
    private const val INTERVAL_TICKS = 5L
    private const val INTERVAL_SECONDS = INTERVAL_TICKS / 20.0

    private val ENFORCED_GAME_MODES = setOf(GameMode.SURVIVAL, GameMode.ADVENTURE)
    private val dustOptions = Particle.DustOptions(Color.fromRGB(0xFF3333), 1.6f)
    private val tasks = ConcurrentHashMap<UUID, ScheduledTask>()
    private val warned = ConcurrentHashMap.newKeySet<UUID>()

    fun start() {
        Bukkit.getOnlinePlayers().forEach(::startFor)
    }

    fun stop() {
        tasks.values.forEach(ScheduledTask::cancel)
        tasks.clear()
        warned.clear()
    }

    fun startFor(player: Player) {
        if (tasks.containsKey(player.uniqueId)) {
            return
        }

        val task = player.scheduler.runAtFixedRate(
            plugin,
            { tick(player) },
            { cleanup(player.uniqueId) },
            1L,
            INTERVAL_TICKS
        ) ?: return

        tasks[player.uniqueId] = task
    }

    private fun cleanup(uuid: UUID) {
        tasks.remove(uuid)
        warned.remove(uuid)
    }

    private fun tick(player: Player) {
        if (player.gameMode !in ENFORCED_GAME_MODES || player.isDead) {
            leaveArea(player)
            return
        }

        val world = player.world
        val top = VertBorderManager.borderHeight(world, VerticalBorderAlignment.TOP)
        val bottom = VertBorderManager.borderHeight(world, VerticalBorderAlignment.BOTTOM)
        val y = player.location.y

        when {
            top != null && y > top ->
                render(
                    player,
                    VerticalBorderAlignment.TOP,
                    top,
                    outside = y - top,
                    violating = true
                )

            bottom != null && y < bottom ->
                render(
                    player,
                    VerticalBorderAlignment.BOTTOM,
                    bottom,
                    outside = bottom - y,
                    violating = true
                )

            top != null && y >= top - VertBorderManager.WARNING_DISTANCE ->
                render(
                    player,
                    VerticalBorderAlignment.TOP,
                    top,
                    outside = top - y,
                    violating = false
                )

            bottom != null && y <= bottom + VertBorderManager.WARNING_DISTANCE ->
                render(
                    player,
                    VerticalBorderAlignment.BOTTOM,
                    bottom,
                    outside = y - bottom,
                    violating = false
                )

            else -> leaveArea(player)
        }
    }

    private fun render(
        player: Player,
        alignment: VerticalBorderAlignment,
        borderY: Double,
        outside: Double,
        violating: Boolean
    ) {
        if (violating) {
            val perRun = VertBorderManager.damagePerSecond(outside) * INTERVAL_SECONDS
            if (perRun > 0.0) {
                player.damage(perRun)
            }
        }

        enterArea(player)
        showBorderPlane(player, borderY)

        player.sendActionBar(buildText {
            error("Du bist zu ${alignment.offsetName}!")
            appendSpace()
            darkSpacer("|")
            appendSpace()
            error(alignment.offsetArrow)
            appendSpace()
            variableValue("(${outside.roundToInt()} Blöcke)")
        })
    }

    private fun enterArea(player: Player) {
        if (!warned.add(player.uniqueId)) {
            return
        }

        player.playSound(true) {
            type(BukkitSound.BLOCK_NOTE_BLOCK_DIDGERIDOO)
            pitch(0f)
        }

        player.showTitle {
            title {
                geilesRot("Weltengrenze")
            }

            subtitle {
                spacer("Kehre um, bevor du Schaden nimmst!")
            }

            times {
                fadeIn(200.milliseconds)
                stay(3.seconds)
                fadeOut(400.milliseconds)
            }
        }
    }

    private fun leaveArea(player: Player) {
        warned.remove(player.uniqueId)
    }

    private fun showBorderPlane(player: Player, border: Double) = player.spawnParticle(
        Particle.DUST,
        player.location.x, border, player.location.z,
        30,
        2.5, 0.0, 2.5,
        0.0,
        dustOptions
    )
}
