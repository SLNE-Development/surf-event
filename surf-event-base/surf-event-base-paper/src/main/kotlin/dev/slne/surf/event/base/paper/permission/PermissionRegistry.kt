package dev.slne.surf.event.base.paper.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object PermissionRegistry : PermissionRegistry() {
    const val PREFIX = "surf.event"

    val COMMAND_EVENT_SERVER_CHANGE_STATE = create("$PREFIX.command.changeeventserverstate")
}