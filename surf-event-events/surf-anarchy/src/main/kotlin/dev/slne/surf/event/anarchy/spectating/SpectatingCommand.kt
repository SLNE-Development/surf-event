package dev.slne.surf.event.anarchy.spectating

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.event.anarchy.permission.PermissionList
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import org.bukkit.entity.Player

fun spectatingCommand() = commandTree("spectating") {
    withPermission(PermissionList.SPECTATING_COMMAND)

    literalArgument("reset") {
        withPermission(PermissionList.SPECTATING_COMMAND_RESET)

        entitySelectorArgumentManyPlayers("targets") {
            anyExecutor { sender, arguments ->
                val targets: List<Player> by arguments

                targets.forEach { target ->
                    SpectatingManager.resetSpectator(target)
                }

                if (targets.isEmpty()) {
                    sender.sendText {
                        appendAnarchyPrefix()
                        error("Es wurden keine Spieler gefunden.")
                    }
                    return@anyExecutor
                }

                sender.sendText {
                    appendAnarchyPrefix()
                    success("Der Spectator-Status von ")
                    variableValue(targets.size)
                    success(" Spielern wurde zurückgesetzt.")
                }
            }
        }
    }
}