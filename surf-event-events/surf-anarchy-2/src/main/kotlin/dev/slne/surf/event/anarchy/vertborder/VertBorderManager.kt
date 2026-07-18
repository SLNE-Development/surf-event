package dev.slne.surf.event.anarchy.vertborder

import dev.slne.surf.event.anarchy.plugin
import dev.slne.surf.event.anarchy.vertborder.border.Borders
import dev.slne.surf.event.anarchy.vertborder.border.VerticalBorderAlignment
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.World
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.pow
import kotlin.time.Duration


object VertBorderManager {
    private val growth = (DAMAGE_AT_TEN / DAMAGE_AT_ONE).pow(1.0 / 9.0)

    private const val DAMAGE_AT_ONE = 0.5
    private const val DAMAGE_AT_TEN = 6.0
    private const val ANIMATION_STEP_TICKS = 1L
    const val WARNING_DISTANCE = 5.0

    private val borderCache = ConcurrentHashMap<UUID, Borders>()
    private val animationTasks =
        ConcurrentHashMap<Pair<UUID, VerticalBorderAlignment>, ScheduledTask>()

    private fun borders(world: World) = borderCache.computeIfAbsent(world.uid) {
        Borders(null, null)
    }

    fun borderHeight(world: World, border: VerticalBorderAlignment): Double? = borders(world).let {
        if (border == VerticalBorderAlignment.TOP) {
            it.top
        } else {
            it.bottom
        }
    }

    fun defaultHeight(world: World, border: VerticalBorderAlignment) =
        if (border == VerticalBorderAlignment.TOP) world.maxHeight.toDouble() else world.minHeight.toDouble()

    fun currentHeight(world: World, border: VerticalBorderAlignment) =
        borderHeight(world, border) ?: defaultHeight(world, border)

    fun moveBorder(world: World, border: VerticalBorderAlignment, height: Double) {
        val borders = borders(world)

        if (border == VerticalBorderAlignment.TOP) {
            borders.top = height
        } else {
            borders.bottom = height
        }
    }

    fun moveBorderTo(
        world: World,
        border: VerticalBorderAlignment,
        targetHeight: Double,
        duration: Duration
    ) =
        moveBorderTo(
            world,
            border,
            targetHeight,
            (duration.inWholeMilliseconds / 50).coerceAtLeast(1)
        )

    private fun moveBorderTo(
        world: World,
        border: VerticalBorderAlignment,
        targetHeight: Double,
        durationTicks: Long
    ) {
        stopAnimation(world, border)

        if (durationTicks <= 0) {
            moveBorder(world, border, targetHeight)
            return
        }

        val start = currentHeight(world, border)
        val animationKey = world.uid to border
        var elapsed = 0L

        val task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, { scheduled ->
            elapsed += ANIMATION_STEP_TICKS
            val progress = (elapsed.toDouble() / durationTicks).coerceIn(0.0, 1.0)
            moveBorder(world, border, start + (targetHeight - start) * progress)

            if (progress >= 1.0) {
                scheduled.cancel()
                animationTasks.remove(animationKey)
            }
        }, ANIMATION_STEP_TICKS, ANIMATION_STEP_TICKS)

        animationTasks[animationKey] = task
    }

    fun stopAnimation(world: World, border: VerticalBorderAlignment) {
        animationTasks.remove(world.uid to border)?.cancel()
    }


    fun clearBorder(world: World, border: VerticalBorderAlignment) {
        stopAnimation(world, border)

        val borders = borders(world)
        if (border == VerticalBorderAlignment.TOP) {
            borders.top = null
        } else {
            borders.bottom = null
        }
    }

    fun clearBorders(world: World) {
        clearBorder(world, VerticalBorderAlignment.TOP)
        clearBorder(world, VerticalBorderAlignment.BOTTOM)
    }

    fun damagePerSecond(blocksOutside: Double): Double {
        if (blocksOutside <= 0.0) {
            return 0.0
        }

        return (DAMAGE_AT_ONE * growth.pow(blocksOutside - 1.0)).coerceAtMost(10.0)
    }
}
