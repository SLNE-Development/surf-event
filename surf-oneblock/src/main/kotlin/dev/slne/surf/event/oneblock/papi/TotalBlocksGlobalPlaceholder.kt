package dev.slne.surf.event.oneblock.papi

import dev.slne.surf.event.oneblock.global.GlobalGoals
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import org.bukkit.OfflinePlayer
import java.text.NumberFormat
import java.util.*

class TotalBlocksGlobalPlaceholder : PapiPlaceholder("total-blocks-global") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String? {
        return NumberFormat.getNumberInstance(Locale.GERMAN).format(GlobalGoals.totalBlocks())
    }
}