package dev.slne.surf.event.oneblock.papi

import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import org.bukkit.OfflinePlayer
import java.util.UUID

class PlayerNamePlaceholder : PapiPlaceholder("player-name") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String? {
        if (args.isEmpty()) return null

        val uuidString = args[0]
        val uuid = runCatching { UUID.fromString(uuidString) }.getOrNull() ?: return null
        val offlinePlayer = server.getOfflinePlayer(uuid)

        return offlinePlayer.name?.toSmallCaps()
    }
}