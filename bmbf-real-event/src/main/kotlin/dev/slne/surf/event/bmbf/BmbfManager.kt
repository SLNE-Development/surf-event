package dev.slne.surf.event.bmbf

import com.plotsquared.core.plot.Plot
import org.bukkit.entity.Player

object BmbfManager {
    var running = false
        private set
    var currentCategory: BmbfCategory = BmbfCategory.entries.first()
        private set
    var currentChallenge: BmbfChallenge = BmbfChallenge.MINUTE_1
        private set

    fun getOrCreatePlotForPlayer(player: Player, teleportPlayer: Boolean = true): Plot {
        val plotArea = currentCategory.getPlotArea(currentChallenge)
        val uuid = player.uniqueId
        val plots = plotArea.getPlots(uuid)
        val plotPlayer = plotAPI.wrapPlayer(uuid) ?: error("Player not found in PlotSquared")

        if (plots.isNotEmpty()) {
            return plots.first().also { plot ->
                if (teleportPlayer) {
                    plot.teleportPlayer(plotPlayer) {}
                }
            }
        }

        val plot = plotArea.getNextFreePlot(plotPlayer, null)
        plot.claim(plotPlayer, teleportPlayer, null, true, true)

        return plot
    }
}