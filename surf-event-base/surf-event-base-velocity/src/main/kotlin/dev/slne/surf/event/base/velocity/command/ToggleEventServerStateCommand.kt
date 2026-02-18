package dev.slne.surf.event.base.velocity.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.event.base.api.common.state.EventServerState
import dev.slne.surf.event.base.core.access.eventServerAccess
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun eventServerStateCommand() = commandTree("eventserverstate") {
    withPermission("surf.event.base.command.eventserverstate")

    literalArgument("info") {
        anyExecutor { executor, _ ->
            val state = eventServerAccess.getEventServerState()

            executor.sendText {
                appendInfoPrefix()
                info("Der Event-Server ist aktuell ")
                variableValue(state.displayName)
            }
        }
    }

    literalArgument("set") {
        booleanArgument("state") {
            anyExecutor { executor, args ->
                val state: Boolean by args

                val eventServerState = when (state) {
                    true -> EventServerState.OPEN
                    false -> EventServerState.CLOSED
                }

                eventServerAccess.setEventServerState(eventServerState)

                executor.sendText {
                    appendSuccessPrefix()
                    success("Der Event-Server wurde auf ")
                    variableValue(eventServerState.displayName)
                    success(" gesetzt.")
                }
            }
        }
    }
}