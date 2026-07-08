package dev.slne.surf.event.anarchy.finale.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.event.anarchy.finale.FinaleLifecycle
import dev.slne.surf.event.anarchy.finale.command.argument.zonedDateTimeArgument
import dev.slne.surf.event.anarchy.util.PermissionList
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import dev.slne.surf.event.anarchy.util.formatDurationUntil
import java.time.ZonedDateTime

fun finaleCommand() = commandTree("finale") {
    withPermission(PermissionList.FINALE_COMMAND)
    literalArgument("schedule") {
        zonedDateTimeArgument("date") {
            anyExecutor { sender, arguments ->
                val date: ZonedDateTime by arguments

                if (date.isBefore(ZonedDateTime.now())) {
                    sender.sendText {
                        appendAnarchyPrefix()
                        error("Du kannst das Finale nicht in der Vergangenheit planen!")
                    }
                    return@anyExecutor
                }

                FinaleLifecycle.scheduleFinale(date)

                sender.sendText {
                    appendAnarchyPrefix()
                    success("Das Finale startet in ")
                    variableValue(formatDurationUntil(date))
                    success(" am ")
                    variableValue(date.toLocalDate().toString())
                    success(" um ")
                    variableValue(date.toLocalTime().toString())
                }
            }
        }
    }

    literalArgument("cancel") {
        anyExecutor { sender, _ ->
            if (!FinaleLifecycle.isScheduled()) {
                sender.sendText {
                    appendAnarchyPrefix()
                    error("Es ist kein Finale geplant!")
                }
                return@anyExecutor
            }

            FinaleLifecycle.cancelFinale()

            sender.sendText {
                appendAnarchyPrefix()
                success("Das Finale wurde erfolgreich abgebrochen!")
            }
        }
    }
}