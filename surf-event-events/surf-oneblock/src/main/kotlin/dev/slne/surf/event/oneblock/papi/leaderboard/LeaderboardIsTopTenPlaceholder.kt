package dev.slne.surf.event.oneblock.papi.leaderboard

import dev.slne.surf.event.oneblock.papi.ValueHolder
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import org.bukkit.OfflinePlayer

object LeaderboardIsTopTenPlaceholder : PapiPlaceholder("isTopTen") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String {
        val uuid = player.uniqueId
        val ownPlace = ValueHolder.getPlace(uuid) ?: return "scoreboard02"

        if (ownPlace <= 10) {
            return "scoreboard01"
        }
        return "scoreboard02"
    }
}