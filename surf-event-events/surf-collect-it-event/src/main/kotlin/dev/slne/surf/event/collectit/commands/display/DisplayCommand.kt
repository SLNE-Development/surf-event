package dev.slne.surf.event.collectit.commands.display

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.event.collectit.commands.display.subcommands.displayInfoCommand
import dev.slne.surf.event.collectit.commands.display.subcommands.spawnDisplayCommand
import dev.slne.surf.event.collectit.utils.CollectItPermissionRegistry

fun displayCommand() = commandAPICommand("display") {
    withPermission(CollectItPermissionRegistry.COMMAND_DISPLAY)

    displayInfoCommand()
    spawnDisplayCommand()
}