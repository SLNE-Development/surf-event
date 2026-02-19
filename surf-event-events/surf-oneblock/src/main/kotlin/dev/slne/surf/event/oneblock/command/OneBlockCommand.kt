package dev.slne.surf.event.oneblock.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.arguments.AsyncPlayerProfileArgument
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.event.oneblock.config.OneBlockConfigHolder
import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.permission.OneBlockPermissions
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.surfapi.bukkit.api.command.util.awaitAsyncPlayerProfile
import dev.slne.surf.surfapi.bukkit.api.command.util.idOrThrow
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.command.CommandSender

fun oneBlockCommand() = commandTree("oneblock") {
    withPermission(OneBlockPermissions.ONE_BLOCK_COMMAND)

    literalArgument("reload") {
        withPermission(OneBlockPermissions.ONE_BLOCK_COMMAND_RELOAD)
        anyExecutor { sender, _ ->
            reload(sender)
        }
    }

    literalArgument("resetOneBlock") {
        withPermission(OneBlockPermissions.ONE_BLOCK_COMMAND_RESET)

        argument(AsyncPlayerProfileArgument("target")) {
            anyExecutor { sender, args ->
                plugin.launch {
                    val target = args.awaitAsyncPlayerProfile("target")
                    val success = IslandService.resetIsland(target.idOrThrow())

                    if (success) {
                        sender.sendText {
                            appendSuccessPrefix()
                            success("Die Oneblock Insel von ")
                            variableValue(target.name ?: target.idOrThrow().toString())
                            success(" wurde zurückgesetzt.")
                        }
                    } else {
                        sender.sendText {
                            appendErrorPrefix()
                            error("Die Insel konnte nicht zurückgesetzt werden. Möglicherweise hat der Spieler keine Insel oder es ist ein Fehler aufgetreten.")
                        }
                    }
                }
            }
        }
    }
}

private fun reload(sender: CommandSender) {
    OneBlockConfigHolder.reloadFromFile()

    sender.sendText {
        appendSuccessPrefix()
        success("OneBlock configuration reloaded!")
    }
}