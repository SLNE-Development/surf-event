package dev.slne.surf.event.oneblock.progress

import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.global.GlobalGoals
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.stats.api.surfStatsApi
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ProgressService {
    lateinit var statsSaveTask: ScheduledTask
    fun onBlockMined(player: Player) {
        val island = IslandService.incrementMined(player.uniqueId) ?: return
        val newPhase = phaseConfig.currentPhase(island.totalMined)
        val oldPhase = phaseConfig.currentPhase(island.totalMined - 1)

        if (oldPhase.id != newPhase.id) {
            player.sendText {
                appendSuccessPrefix()
                success("Du hast jetzt ")
                variableValue(newPhase.displayName)
                success(" erreicht!")
            }
        }

        GlobalGoals.onBlockMined()
    }

    suspend fun flushStats() {
        val saved = Bukkit.getOnlinePlayers().map {
            saveStats(it)
        }

        plugin.logger.info("Successfully flushed stats for ${saved.size} players.")
    }

    private suspend fun saveStats(player: Player) {
        val island = IslandService.getIsland(player.uniqueId) ?: return

        surfStatsApi.saveCustomStat(
            player.uniqueId,
            player.name,
            "minecraft:one_block_mined",
            island.totalMined
        )

        surfStatsApi.saveCustomStat(
            player.uniqueId,
            player.name,
            "minecraft:one_block_level",
            phaseConfig.currentPhase(island.totalMined).weight.toLong()
        )
    }
}