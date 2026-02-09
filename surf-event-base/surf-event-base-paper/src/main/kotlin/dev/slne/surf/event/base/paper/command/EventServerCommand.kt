package dev.slne.surf.event.base.paper.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.event.base.api.common.state.EventServerState
import dev.slne.surf.event.base.paper.command.argument.eventServerStateArgument
import dev.slne.surf.event.base.paper.eventServerConfigHolder
import dev.slne.surf.event.base.paper.manager.eventServerManager
import dev.slne.surf.event.base.paper.permission.PermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun eventServerCommand() = commandTree("eventserver") {
    withPermission(PermissionRegistry.COMMAND_EVENT_SERVER)
    literalArgument("changestate") {
        withPermission(PermissionRegistry.COMMAND_EVENT_SERVER_CHANGE_STATE)
        eventServerStateArgument("state") {
            anyExecutor { executor, args ->
                val current = eventServerManager.state.get()
                val state: EventServerState by args

                if (current == state) {
                    executor.sendText {
                        appendErrorPrefix()
                        error("Der Event Server ist bereits ${current.displayName}.")
                    }
                    return@anyExecutor
                }

                eventServerManager.state.set(state)

                executor.sendText {
                    appendSuccessPrefix()
                    success("Der Event Server ist nun ")
                    variableValue(state.displayName)
                    success(".")
                }
            }
        }
    }

    literalArgument("reload") {
        withPermission(PermissionRegistry.COMMAND_EVENT_SERVER_RELOAD)
        anyExecutor { executor, _ ->
            eventServerConfigHolder.reload()

            executor.sendText {
                appendSuccessPrefix()
                success("Die Event Server Konfiguration wurde neu geladen.")
            }
        }
    }
}