package dev.slne.surf.event.bmbf.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object BmbfPermissionRegistry: PermissionRegistry() {
    private const val COMMAND_PREFIX = "bmbf-stream.command"

    val BMBF_COMMAND = create("$COMMAND_PREFIX.bmbf")
    val BMBF_COMMAND_START = create("$COMMAND_PREFIX.start")
    val BMBF_COMMAND_NEXT = create("$COMMAND_PREFIX.next")
    val BMBF_COMMAND_CANCEL = create("$COMMAND_PREFIX.cancel")
    val BMBF_SELECT_COMMAND = create("$COMMAND_PREFIX.select")
}