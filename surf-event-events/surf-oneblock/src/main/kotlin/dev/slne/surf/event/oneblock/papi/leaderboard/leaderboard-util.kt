package dev.slne.surf.event.oneblock.papi.leaderboard

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.minimessage.miniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.luckperms.api.LuckPermsProvider
import java.util.*

fun prefixPlayer(uuid: UUID): String {
    val luckPerms = LuckPermsProvider.get()

    val player = server.getOfflinePlayer(uuid)
    val user = luckPerms.userManager.getUser(uuid) ?: run {
        plugin.launch {
            luckPerms.userManager.loadUser(uuid)
        }

        return LegacyComponentSerializer.legacySection()
            .serialize(miniMessage.deserialize(player.name ?: "Fehler"))
    }

    val primaryGroup = user.primaryGroup

    val group = luckPerms.groupManager.getGroup(primaryGroup) ?: return player.name ?: "Fehler"
    val prefix = group.cachedData.metaData.prefix

    return LegacyComponentSerializer.legacySection()
        .serialize(miniMessage.deserialize("$prefix ${player.name}"))
}