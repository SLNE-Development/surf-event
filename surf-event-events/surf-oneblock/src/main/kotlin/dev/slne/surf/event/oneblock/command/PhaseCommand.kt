package dev.slne.surf.event.oneblock.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.event.oneblock.permission.OneBlockPermissions
import dev.slne.surf.event.oneblock.progress.PhaseConfig
import dev.slne.surf.event.oneblock.progress.phaseConfig
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf

fun phaseCommand() = commandTree("phase") {
    withPermission(OneBlockPermissions.PHASE_COMMAND)

    literalArgument("reload") {
        anyExecutor { sender, _ ->
            PhaseConfig.reloadFromFile()
            sender.sendText {
                appendPrefix()
                success("Phase configuration reloaded.")
            }
        }
    }

    literalArgument("validate") {
        anyExecutor { sender, _ ->
            val errors = mutableObjectListOf<Throwable>()

            for (phase in phaseConfig.phases) {
                try {
                    phase.entitySelector?.pick()
                } catch (e: Throwable) {
                    errors.add(e)
                }
            }

            if (errors.isEmpty()) {
                sender.sendText {
                    appendPrefix()
                    success("Phase configuration is valid.")
                }
            } else {
                sender.sendText {
                    appendPrefix()
                    error("Phase configuration has ${errors.size} errors:")
                    appendCollectionNewLine(errors) { error ->
                        buildText {
                            error(error.message ?: error.toString())
                        }
                    }
                }
            }
        }
    }
}