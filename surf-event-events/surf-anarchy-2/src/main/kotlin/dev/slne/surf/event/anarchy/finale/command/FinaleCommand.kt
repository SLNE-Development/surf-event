package dev.slne.surf.event.anarchy.finale.command

import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.event.anarchy.finale.FinaleLifecycle
import dev.slne.surf.event.anarchy.finale.command.argument.zonedDateTimeArgument
import dev.slne.surf.event.anarchy.util.PermissionList
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import dev.slne.surf.event.anarchy.util.formatCountdownTime
import dev.slne.surf.event.anarchy.util.formatDurationUntil
import org.bukkit.command.CommandSender
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes

fun finaleCommand() = commandTree("finale") {
    withPermission(PermissionList.FINALE_COMMAND)
    literalArgument("schedule") {
        anyExecutor { sender, _ ->
            sender.sendText {
                appendErrorPrefix()
                error("Bitte nutze: /finale schedule <Dauer in Minuten> <Start-Datum>: z.b. /finale schedule 60 18.07.2026 18:00")
            }
        }

        integerArgument("durationMinutes", 1) {
            zonedDateTimeArgument("date") {
                anyExecutor { sender, arguments ->
                    val durationMinutes: Int by arguments
                    scheduleFinale(sender, arguments, durationMinutes)
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

    literalArgument("lockOverworld") {
        anyExecutor { sender, _ ->
            lockOverworld(sender)
        }
    }

    literalArgument("reset") {
        anyExecutor { sender, _ ->
            FinaleLifecycle.resetAll()

            sender.sendText {
                appendAnarchyPrefix()
                success("Das Finale wurde erfolgreich zurückgesetzt!")
            }
        }
    }

    literalArgument("debugInfo") {
        anyExecutor { sender, _ ->
            sender.sendText {
                appendAnarchyPrefix()
                info("Finale geplant: ")
                variableValue(FinaleLifecycle.isScheduled().toString())

                appendNewline()
                appendAnarchyPrefix()
                info("Finale gestartet: ")
                variableValue(FinaleLifecycle.isRunning().toString())

                appendNewline()
                appendAnarchyPrefix()
                info("Finale-Dauer (bis finale Border): ")
                variableValue(formatCountdownTime(FinaleLifecycle.finaleDuration.inWholeSeconds))

                appendNewline()
                appendAnarchyPrefix()
                info("Overworld gesperrt: ")
                variableValue(FinaleLifecycle.isOverworldLocked().toString())

                FinaleLifecycle.secondsUntilStart()?.let { seconds ->
                    appendNewline()
                    appendAnarchyPrefix()
                    info("Zeit bis Finale-Start: ")
                    variableValue(formatCountdownTime(seconds))
                }

                FinaleLifecycle.secondsUntilFinalBorder()?.let { seconds ->
                    appendNewline()
                    appendAnarchyPrefix()
                    info("Zeit bis finale Border: ")
                    variableValue(formatCountdownTime(seconds))
                }
            }
        }
    }
}

private fun lockOverworld(sender: CommandSender) {
    when (FinaleLifecycle.lockOverworld()) {
        FinaleLifecycle.OverworldLockResult.NOT_RUNNING -> sender.sendText {
            appendAnarchyPrefix()
            error("Derzeit läuft kein Finale.")
        }

        FinaleLifecycle.OverworldLockResult.ALREADY_LOCKED -> sender.sendText {
            appendAnarchyPrefix()
            error("Die Overworld ist bereits gesperrt!")
        }

        FinaleLifecycle.OverworldLockResult.OK -> sender.sendText {
            appendAnarchyPrefix()
            success("Die Overworld ist nun gesperrt.")
        }
    }
}

private fun scheduleFinale(
    sender: CommandSender,
    arguments: CommandArguments,
    durationMinutes: Int?
) {
    val date: ZonedDateTime by arguments

    if (date.isBefore(ZonedDateTime.now())) {
        sender.sendText {
            appendAnarchyPrefix()
            error("Du kannst das Finale nicht in der Vergangenheit planen!")
        }
        return
    }

    val duration = durationMinutes?.minutes ?: FinaleLifecycle.DEFAULT_FINALE_DURATION
    FinaleLifecycle.scheduleFinale(date, duration)

    sender.sendText {
        appendAnarchyPrefix()
        success("Das Finale startet in ")
        variableValue(formatDurationUntil(date))
        success(" am ")
        variableValue(date.toLocalDate().toString())
        success(" um ")
        variableValue(date.toLocalTime().toString())

        appendNewline()
        appendAnarchyPrefix()
        success("Das Finale dauert bis zur finalen Border ")
        variableValue(formatCountdownTime(duration.inWholeSeconds))
        success(".")
    }
}