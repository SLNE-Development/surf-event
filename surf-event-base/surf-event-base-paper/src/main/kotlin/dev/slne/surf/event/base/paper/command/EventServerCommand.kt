package dev.slne.surf.event.base.paper.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.event.base.paper.eventServerConfigHolder
import dev.slne.surf.event.base.paper.permission.PermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun eventServerCommand() = commandTree("eventserver") {
    withPermission(PermissionRegistry.COMMAND_EVENT_SERVER)
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