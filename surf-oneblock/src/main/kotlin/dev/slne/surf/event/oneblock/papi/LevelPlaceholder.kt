package dev.slne.surf.event.oneblock.papi

import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import org.bukkit.OfflinePlayer
import java.text.NumberFormat
import java.util.*

class LevelPlaceholder : PapiPlaceholder("level") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String? {
        val island = IslandService.getIsland(player.uniqueId) ?: return null
        return NumberFormat.getNumberInstance(Locale.GERMAN).format(island.level)
    }
}