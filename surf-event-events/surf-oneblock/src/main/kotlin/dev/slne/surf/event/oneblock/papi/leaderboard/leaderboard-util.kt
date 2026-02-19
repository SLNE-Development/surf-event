package dev.slne.surf.event.oneblock.papi.leaderboard

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import net.luckperms.api.LuckPermsProvider
import java.util.*

fun prefixPlayer(uuid: UUID): String {
    val luckPerms = LuckPermsProvider.get()

    val player = server.getOfflinePlayer(uuid)
    val user = luckPerms.userManager.getUser(uuid) ?: run {
        plugin.launch {
            luckPerms.userManager.loadUser(uuid)
        }

        return player.name ?: "Fehler"
    }

    val primaryGroup = user.primaryGroup

    val group = luckPerms.groupManager.getGroup(primaryGroup) ?: return player.name ?: "Fehler"
    val prefix = group.cachedData.metaData.prefix

    return "$prefix ${player.name}"
}