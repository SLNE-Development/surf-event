package dev.slne.surf.event.anarchy.finale.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.slne.surf.api.core.messages.adventure.buildText
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ZonedDateTimeArgument(nodeName: String) :
    CustomArgument<ZonedDateTime, String>(GreedyStringArgument(nodeName), { info ->
        parse(info.input) ?: throw CustomArgumentException.fromAdventureComponent(
            buildText {
                appendErrorPrefix()
                error("Das Datum muss im Format dd.MM.yyyy HH:mm sein.")
            })
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.strings("18.07.2026 18:00")
        )
    }
}

private val zone = ZoneId.of("Europe/Berlin")
private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
private fun parse(input: String): ZonedDateTime? {
    return try {
        LocalDateTime.parse(input, formatter)
            .atZone(zone)
    } catch (_: DateTimeParseException) {
        null
    }
}

inline fun CommandTree.zonedDateTimeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    ZonedDateTimeArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.zonedDateTimeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    ZonedDateTimeArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandAPICommand.zonedDateTimeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(ZonedDateTimeArgument(nodeName).setOptional(optional).apply(block))