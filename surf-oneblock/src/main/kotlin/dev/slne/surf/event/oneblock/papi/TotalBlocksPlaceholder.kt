package dev.slne.surf.event.oneblock.papi

import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import org.bukkit.OfflinePlayer
import java.text.NumberFormat
import java.util.*

class TotalBlocksPlaceholder : PapiPlaceholder("total-blocks") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String? {
        val uuidString = args[0]
        val uuid = runCatching { UUID.fromString(uuidString) }.getOrNull() ?: return null

        val island = IslandService.getIsland(uuid) ?: return null
        return NumberFormat.getNumberInstance(Locale.GERMAN).format(island.totalMined)
    }
}