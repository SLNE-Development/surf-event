package dev.slne.surf.event.anarchy.equipment

import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.event.anarchy.permission.PermissionList
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import org.bukkit.entity.Player

fun equipmentCommand() = commandTree("equipment") {
    withPermission(PermissionList.EQUIPMENT_COMMAND)
    literalArgument("reset") {
        withPermission(PermissionList.EQUIPMENT_COMMAND_RESET)
        argument(EntitySelectorArgument.ManyPlayers("targets")) {
            anyExecutor { sender, arguments ->
                val targets: List<Player> by arguments

                targets.forEach { target ->
                    EquipmentManager.resetEquipment(target)
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
                    success("Das Equipment von ")
                    variableValue(targets.size)
                    success(" Spielern wurde zurückgesetzt.")
                }
            }
        }
    }

    literalArgument("give") {
        withPermission(PermissionList.EQUIPMENT_COMMAND_GIVE)
        argument(EntitySelectorArgument.ManyPlayers("targets")) {
            anyExecutor { sender, arguments ->
                val targets: List<Player> by arguments

                targets.forEach { target ->
                    EquipmentManager.giveEquipment(target)
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
                    success("Das Equipment von ")
                    variableValue(targets.size)
                    success(" wurde gegeben.")
                }
            }
        }
    }
}