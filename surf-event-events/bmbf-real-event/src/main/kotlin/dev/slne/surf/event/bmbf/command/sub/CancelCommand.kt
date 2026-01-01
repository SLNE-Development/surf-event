package dev.slne.surf.event.bmbf.command.sub

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.event.bmbf.BmbfManager
import dev.slne.surf.event.bmbf.permission.BmbfPermissionRegistry
import dev.slne.surf.event.bmbf.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.cancelCommand() = subcommand("cancel") {
    withPermission(BmbfPermissionRegistry.BMBF_COMMAND_CANCEL)
    withRequirement { BmbfManager.running }

    anyExecutor { sender, _ ->
        sender.sendText {
            appendPrefix()
            success("Das Event wird gestoppt...")
        }

        plugin.launch { BmbfManager.cancel() }
    }
}