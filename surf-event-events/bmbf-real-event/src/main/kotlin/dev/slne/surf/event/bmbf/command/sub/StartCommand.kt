package dev.slne.surf.event.bmbf.command.sub

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.event.bmbf.BmbfManager
import dev.slne.surf.event.bmbf.permission.BmbfPermissionRegistry
import dev.slne.surf.event.bmbf.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.startCommand() = subcommand("start") {
    withPermission(BmbfPermissionRegistry.BMBF_COMMAND_START)
    withRequirement { !BmbfManager.running && BmbfManager.firstRun }

    anyExecutor { sender, _ ->
        sender.sendText {
            appendPrefix()
            success("Das Event wird gestartet.")
        }

        plugin.launch {
            BmbfManager.startNext()
        }
    }
}