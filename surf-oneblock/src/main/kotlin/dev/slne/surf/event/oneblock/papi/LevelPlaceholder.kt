package dev.slne.surf.event.oneblock.papi

import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.progress.phaseConfig
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import org.bukkit.OfflinePlayer
import java.util.UUID

class LevelPlaceholder : PapiPlaceholder("level") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String? {
        if (args.isEmpty()) return null

        val uuidString = args[0]
        val uuid = runCatching { UUID.fromString(uuidString) }.getOrNull() ?: return null

        val island = IslandService.getIsland(uuid) ?: return null
        val phase = phaseConfig.currentPhase(island.totalMined)
        return phase.displayName.toSmallCaps()
    }
}