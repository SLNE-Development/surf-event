package dev.slne.surf.event.oneblock.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.event.oneblock.config.OneBlockConfigHolder
import dev.slne.surf.event.oneblock.permission.OneBlockPermissions
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.command.CommandSender

fun oneBlockCommand() = commandTree("oneblock") {
    withPermission(OneBlockPermissions.ONE_BLOCK_COMMAND)

    literalArgument("reload") {
        anyExecutor { sender, _ ->
            reload(sender)
        }
    }
}

private fun reload(sender: CommandSender) {
    OneBlockConfigHolder.reloadFromFile()

    sender.sendText {
        appendPrefix()
        success("OneBlock configuration reloaded!")
    }
}