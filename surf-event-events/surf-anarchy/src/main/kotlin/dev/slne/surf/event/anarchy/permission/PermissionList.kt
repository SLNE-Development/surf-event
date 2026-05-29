package dev.slne.surf.event.anarchy.permission

import dev.slne.surf.api.paper.permission.PermissionRegistry

object PermissionList : PermissionRegistry() {
    private const val PREFIX = "surf.event.anarchy"


    val ANTI_DUPE_NOTIFY = create("$PREFIX.antidupe")

    val DEATH_SPECTATOR = create("$PREFIX.death.spectator")

    val EQUIPMENT_COMMAND = create("$PREFIX.equipment.command")
    val EQUIPMENT_COMMAND_RESET = create("$EQUIPMENT_COMMAND.reset")
    val EQUIPMENT_COMMAND_GIVE = create("$EQUIPMENT_COMMAND.give")

    val SPECTATING_COMMAND = create("$PREFIX.spectating.command")
    val SPECTATING_COMMAND_RESET = create("$SPECTATING_COMMAND.reset")
}