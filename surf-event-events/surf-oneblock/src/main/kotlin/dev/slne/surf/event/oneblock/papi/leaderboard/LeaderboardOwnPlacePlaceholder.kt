package dev.slne.surf.event.oneblock.papi.leaderboard

import dev.slne.surf.event.oneblock.papi.ValueHolder
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.OfflinePlayer

object LeaderboardOwnPlacePlaceholder : PapiPlaceholder("ownplace") {

    override fun parse(player: OfflinePlayer, args: List<String>): String? {
        val operation = args.getOrNull(0)?.lowercase()?.trim()
        val number = args.getOrNull(1)?.toIntOrNull()

        val uuid = player.uniqueId
        val ownPlace = ValueHolder.getPlace(uuid) ?: return null

        val targetPlace = when {
            operation == null -> ownPlace
            number == null -> return null
            operation == "plus" -> ownPlace + number
            operation == "minus" -> ownPlace - number
            else -> return null
        }

        if (targetPlace <= 0) return null

        val targetUuid = ValueHolder.getUuid(targetPlace) ?: return "Du bist Letzter? haha"
        val mined = ValueHolder.getMined(targetUuid)?.toString() ?: "???"

        return LegacyComponentSerializer.legacySection().serialize(buildText {
            text("#$targetPlace ", Colors.GOLD)
            text(prefixPlayer(targetUuid))
            spacer(": ")
            variableValue(mined)
        })
    }
}
