package dev.slne.surf.event.base.paper.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.event.base.api.common.state.EventServerState
import dev.slne.surf.event.base.paper.command.argument.eventServerStateArgument
import dev.slne.surf.event.base.paper.manager.eventServerManager
import dev.slne.surf.event.base.paper.permission.PermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun eventServerStateChangeCommand() = commandTree("changeeventserverstate") {
    withPermission(PermissionRegistry.COMMAND_EVENT_SERVER_CHANGE_STATE)
    eventServerStateArgument("state") {
        anyExecutor { executor, args ->
            val current = eventServerManager.state.get()
            val state: EventServerState by args

            if (current == state) {
                executor.sendText {
                    appendPrefix()
                    error("Der Event Server ist bereits ${current.displayName}.")
                }
                return@anyExecutor
            }

            eventServerManager.state.set(state)

            executor.sendText {
                appendPrefix()
                success("Der Event Server ist nun ")
                variableValue(state.displayName)
                success(".")
            }
        }
    }
}