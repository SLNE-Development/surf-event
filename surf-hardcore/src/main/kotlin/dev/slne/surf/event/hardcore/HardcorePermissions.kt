package dev.slne.surf.event.hardcore

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object HardcorePermissions: PermissionRegistry() {
    val HARDCORE_BYPASS = create("surf.event.hardcore.bypass")
    val HARDCORE_TP = create("surf.event.hardcore.tp")
    val HARDCORE_COMMAND = create("surf.event.hardcore.command")
    val HARDCORE_DEATH_SOUND = create("surf.event.hardcore.deathsound.command")
}