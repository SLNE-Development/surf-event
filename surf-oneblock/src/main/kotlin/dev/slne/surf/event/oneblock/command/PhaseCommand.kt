package dev.slne.surf.event.oneblock.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.event.oneblock.permission.OneBlockPermissions
import dev.slne.surf.event.oneblock.progress.PhaseConfig
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

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
}