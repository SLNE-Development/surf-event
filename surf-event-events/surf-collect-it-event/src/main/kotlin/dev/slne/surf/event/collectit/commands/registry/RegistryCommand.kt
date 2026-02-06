package dev.slne.surf.event.collectit.commands.registry

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.event.collectit.dialogs.MainDialog
import dev.slne.surf.event.collectit.utils.CollectItPermissionRegistry

fun registryCommand() = commandAPICommand("registry") {
    withPermission(CollectItPermissionRegistry.COMMAND_REGISTRY)

    playerExecutor { player, arguments ->
        player.showDialog(MainDialog.createDialog())
    }
}