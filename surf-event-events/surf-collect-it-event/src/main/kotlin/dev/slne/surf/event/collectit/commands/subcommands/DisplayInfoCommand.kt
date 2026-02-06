package dev.slne.surf.event.collectit.commands.subcommands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.event.collectit.display.DisplayManager
import dev.slne.surf.event.collectit.display.displays.ItemDisplay
import dev.slne.surf.event.collectit.utils.CollectItPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.displayInfoCommand() = subcommand("info") {
    withPermission(CollectItPermissionRegistry.COMMAND_DISPLAY_INFO)

    playerExecutor { player, _ ->
        val displays by DisplayManager::displays
        val count = displays.size
        val itemDisplayCount = displays.count { it is ItemDisplay }

        player.sendText {
            variableKey("Anzahl Displays: ")
            variableValue(count)
            appendNewline(2)

            variableKey("Anzahl Item Displays: ")
            variableValue(itemDisplayCount)
        }
    }
}