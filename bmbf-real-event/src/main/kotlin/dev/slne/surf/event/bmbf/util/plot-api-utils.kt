package dev.slne.surf.event.bmbf.util

import com.plotsquared.core.PlotSquared
import com.plotsquared.core.player.PlotPlayer
import com.plotsquared.core.plot.Plot
import org.bukkit.entity.Player
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Player.toPlotPlayer(): PlotPlayer<*> =
    PlotSquared.platform().playerManager().getPlayer(uniqueId)

suspend fun Plot.teleportPlayerSuspend(player: PlotPlayer<*>): Boolean = suspendCoroutine {  continuation ->
    teleportPlayer(player) {
        continuation.resume(it)
    }
}