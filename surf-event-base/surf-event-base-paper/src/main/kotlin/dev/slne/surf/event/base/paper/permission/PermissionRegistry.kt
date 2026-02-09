package dev.slne.surf.event.base.paper.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object PermissionRegistry : PermissionRegistry() {
    const val PREFIX = "surf.event"

    val COMMAND_EVENT_SERVER = create("$PREFIX.command.eventserver")
    val COMMAND_EVENT_SERVER_CHANGE_STATE = create("$PREFIX.command.eventserver.changestate")
    val COMMAND_EVENT_SERVER_RELOAD = create("$PREFIX.command.eventserver.reload")
    val COMMAND_PLAYER_VISIBILITY = create("$PREFIX.command.playervisibility")
}