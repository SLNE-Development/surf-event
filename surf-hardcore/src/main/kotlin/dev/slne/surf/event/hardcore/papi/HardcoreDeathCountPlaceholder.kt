package dev.slne.surf.event.hardcore.papi

import com.destroystokyo.paper.profile.PlayerProfile
import dev.slne.surf.event.hardcore.PaperMain
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import io.papermc.paper.ban.BanListType
import org.bukkit.BanEntry
import org.bukkit.OfflinePlayer

class HardcoreDeathCountPlaceholder: PapiPlaceholder("death-count") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String? {
        return server.getBanList(BanListType.PROFILE)
            .getEntries<BanEntry<PlayerProfile>>()
            .count { it.source == PaperMain.Companion.HARDCORE_BAN_SOURCE }
            .toString()
    }
}