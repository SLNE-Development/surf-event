package dev.slne.surf.event.oneblock.papi.leaderboard

import dev.slne.surf.event.oneblock.papi.ValueHolder
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.OfflinePlayer

object LeaderboardPlacePlaceholder : PapiPlaceholder("place") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String? {
        require(args.size == 1) { "Invalid number of arguments. A place must be provided." }
        val place = args[0].toIntOrNull() ?: return null
        val playerUniqueId = ValueHolder.getUuid(place) ?: return "$place. ???"
        val player = server.getOfflinePlayer(playerUniqueId)

        return LegacyComponentSerializer.legacySection().serialize(buildText {
            text(prefixPlayer(player.uniqueId))
            spacer(": ")
            variableValue(ValueHolder.getMined(playerUniqueId)?.toString() ?: "???")
        })
    }
}