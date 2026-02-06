package dev.slne.surf.event.collectit.utils

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object CollectItPermissionRegistry : PermissionRegistry() {
    private const val PREFIX = "surf.event.collectit"
    private const val COMMAND_PREFIX = "$PREFIX.command"

    val COMMAND_DISPLAY = create("$COMMAND_PREFIX.display")
    val COMMAND_DISPLAY_INFO = create("$COMMAND_DISPLAY.info")
    val COMMAND_DISPLAY_SPAWN = create("$COMMAND_DISPLAY.spawn")
    val COMMAND_DISPLAY_SPAWN_ITEM = create("$COMMAND_DISPLAY_SPAWN.item")
    val COMMAND_DISPLAY_SPAWN_ENTITY = create("$COMMAND_DISPLAY_SPAWN.entity")
    val COMMAND_DISPLAY_SPAWN_ADVANCEMENT = create("$COMMAND_DISPLAY_SPAWN.advancement")

    val COMMAND_REGISTRY = create("$COMMAND_PREFIX.registry")
}