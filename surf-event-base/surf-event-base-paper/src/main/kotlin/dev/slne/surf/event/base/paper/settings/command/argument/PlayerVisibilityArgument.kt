package dev.slne.surf.event.base.paper.settings.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.event.base.paper.settings.PlayerVisibilityState
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class PlayerVisibilityStateArgument(nodeName: String) :
    CustomArgument<PlayerVisibilityState, String>(StringArgument(nodeName), { info ->
        PlayerVisibilityState.entries.toTypedArray()
            .firstOrNull { it.name.equals(info.input, true) }
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendErrorPrefix()
                    error("Die angegebene Event Server State ist ungültig.")
                }
            }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.strings(
                PlayerVisibilityState.entries.map { it.name }
            )
        )
    }
}

inline fun CommandTree.playerVisibilityState(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    PlayerVisibilityStateArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.playerVisibilityState(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    PlayerVisibilityStateArgument(nodeName).setOptional(optional).apply(block)
)