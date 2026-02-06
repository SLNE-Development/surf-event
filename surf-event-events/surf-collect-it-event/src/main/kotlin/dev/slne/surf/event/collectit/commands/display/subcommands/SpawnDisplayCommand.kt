package dev.slne.surf.event.collectit.commands.display.subcommands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.event.collectit.utils.CollectItPermissionRegistry

fun CommandAPICommand.spawnDisplayCommand() = subcommand("spawn") {
    withPermission(CollectItPermissionRegistry.COMMAND_DISPLAY_SPAWN)
}