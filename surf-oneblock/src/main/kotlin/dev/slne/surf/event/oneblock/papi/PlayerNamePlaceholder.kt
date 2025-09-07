package dev.slne.surf.event.oneblock.papi

import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import org.bukkit.OfflinePlayer

class PlayerNamePlaceholder : PapiPlaceholder("player-name") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String? {
        if (args.isEmpty()) return null

        val uuidString = args[0]
        val uuid = runCatching { java.util.UUID.fromString(uuidString) }.getOrNull() ?: return null
        val offlinePlayer = server.getOfflinePlayer(uuid)

        return offlinePlayer.name
    }
}