package dev.slne.surf.event.oneblock.papi.leaderboard

import dev.slne.surf.event.oneblock.papi.ValueHolder
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import org.bukkit.OfflinePlayer

object LeaderboardIsTopTenPlaceholder : PapiPlaceholder("place") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String {
        val uuid = player.uniqueId
        val ownPlace = ValueHolder.getPlace(uuid) ?: return "false"

        if (ownPlace <= 10) {
            return "true"
        }
        return "false"
    }
}