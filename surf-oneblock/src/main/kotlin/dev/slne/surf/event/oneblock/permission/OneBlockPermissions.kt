package dev.slne.surf.event.oneblock.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object OneBlockPermissions : PermissionRegistry() {
    private const val PREFIX = "surf.event.oneblock"
    private const val COMMAND_PREFIX = "$PREFIX.command"

    val RELOCATE_COMMAND = create("$COMMAND_PREFIX.relocate")
    val LOCATE_ONE_BLOCK_COMMAND = create("$COMMAND_PREFIX.locateOneBlock")
    val LOCATE_OTHERS_ONE_BLOCK_COMMAND = create("$COMMAND_PREFIX.locateOneBlock.others")
    val LOCATE_OTHERS_ONE_BLOCK_TP = create("$COMMAND_PREFIX.locateOneBlock.others.tp")
    val PHASE_CHEST_COMMAND = create("$COMMAND_PREFIX.phaseChest")
}