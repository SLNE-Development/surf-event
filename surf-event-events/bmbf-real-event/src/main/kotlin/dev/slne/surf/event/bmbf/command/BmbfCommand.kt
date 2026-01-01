package dev.slne.surf.event.bmbf.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.event.bmbf.command.sub.cancelCommand
import dev.slne.surf.event.bmbf.command.sub.nextCommand
import dev.slne.surf.event.bmbf.command.sub.selectCommand
import dev.slne.surf.event.bmbf.command.sub.startCommand
import dev.slne.surf.event.bmbf.permission.BmbfPermissionRegistry

fun bmbfCommand() = commandAPICommand("bmbf-stream") {
    withPermission(BmbfPermissionRegistry.BMBF_COMMAND)
    startCommand()
    nextCommand()
    cancelCommand()
    selectCommand()
}