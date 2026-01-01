package dev.slne.surf.event.base.paper.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.event.base.api.common.state.EventServerState
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class EventServerStateArgument(nodeName: String) :
    CustomArgument<EventServerState, String>(StringArgument(nodeName), { info ->
        when (info.input()) {
            "open" -> EventServerState.OPEN
            "closed" -> EventServerState.CLOSED
            else -> throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Die angegebene Event Server State ist ungültig.")
                }
            }
        }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.strings(
                "open", "closed"
            )
        )
    }
}

inline fun CommandTree.eventServerStateArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    EventServerStateArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.eventServerStateArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    EventServerStateArgument(nodeName).setOptional(optional).apply(block)
)