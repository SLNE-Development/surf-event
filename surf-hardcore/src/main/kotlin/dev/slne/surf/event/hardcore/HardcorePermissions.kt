package dev.slne.surf.event.hardcore

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object HardcorePermissions: PermissionRegistry() {
    val HARDCORE_BYPASS = create("surf.event.hardcore.bypass")
    val HARDCORE_TP = create("surf.event.hardcore.tp")
}