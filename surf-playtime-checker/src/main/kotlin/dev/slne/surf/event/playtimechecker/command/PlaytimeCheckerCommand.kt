package dev.slne.surf.event.playtimechecker.command

import com.velocitypowered.api.command.CommandSource
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.longArgument
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.event.playtimechecker.config.PlaytimeCheckerConfigManager
import dev.slne.surf.event.playtimechecker.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun playtimeCheckerCommand() = commandAPICommand("playtimechecker") {
    withPermission("surf.event.playtimechecker.command")

    subcommand("reload") {
        anyExecutor { sender, _ -> handleReload(sender) }
    }
    subcommand("add") {
        stringArgument("server") {
            includeSuggestions(ArgumentSuggestions.stringCollection {
                plugin.proxy.allServers.map { it.serverInfo.name }
            })
        }
        stringArgument("category") {
            includeSuggestions(ArgumentSuggestions.strings("survival", "creative", "skyblock"))
        }
        longArgument("maxPlaytimeSeconds", 1L)
        anyExecutor { sender, args ->
            val server: String by args
            val category: String by args
            val maxPlaytimeSeconds: Long by args
            handleAdd(sender, server, category, maxPlaytimeSeconds)
        }
    }
    subcommand("remove") {
        stringArgument("server") {
            includeSuggestions(ArgumentSuggestions.stringCollection {
                PlaytimeCheckerConfigManager.getPlaytimeChecksServers()
            })
        }
        anyExecutor { sender, args ->
            val server: String by args
            handleRemove(sender, server)
        }
    }
}

private fun handleReload(sender: CommandSource) {
    try {
        PlaytimeCheckerConfigManager.reloadFromFile()
        sender.sendText {
            appendPrefix()
            success("Die Konfiguration wurde erfolgreich neu geladen.")
        }
    } catch (e: Exception) {
        sender.sendText {
            appendPrefix()
            error("Failed to reload config: ${e.message}")
        }
    }
}

private fun handleAdd(sender: CommandSource, server: String, category: String, maxPlaytimeSeconds: Long) {
    try {
        PlaytimeCheckerConfigManager.addPlaytimeCheck(server, category, maxPlaytimeSeconds)
        sender.sendText {
            appendPrefix()
            success("Die Spielzeitüberprüfung für den Server $server wurde hinzugefügt.")
        }
    } catch (e: Exception) {
        sender.sendText {
            appendPrefix()
            error("Failed to add playtime check: ${e.message}")
        }
    }
}

private fun handleRemove(sender: CommandSource, server: String) {
    try {
        if (PlaytimeCheckerConfigManager.removePlaytimeCheck(server)) {
            sender.sendText {
                appendPrefix()
                success("Die Spielzeitüberprüfung für den Server $server wurde entfernt.")
            }
        } else {
            sender.sendText {
                appendPrefix()
                error("Die Spielzeitüberprüfung für den Server $server existiert nicht.")
            }
        }
    } catch (e: Exception) {
        sender.sendText {
            appendPrefix()
            error("Failed to remove playtime check: ${e.message}")
        }
    }
}