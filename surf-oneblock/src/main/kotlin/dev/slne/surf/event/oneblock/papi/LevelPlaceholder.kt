package dev.slne.surf.event.oneblock.papi

import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.progress.phaseConfig
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import org.bukkit.OfflinePlayer

class LevelPlaceholder : PapiPlaceholder("level") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String? {
        val island = IslandService.getIsland(player.uniqueId) ?: return null
        val phase = phaseConfig.currentPhase(island.totalMined)
        return phase.displayName
    }
}