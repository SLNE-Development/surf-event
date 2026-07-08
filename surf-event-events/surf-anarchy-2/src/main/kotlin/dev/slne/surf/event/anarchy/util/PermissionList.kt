package dev.slne.surf.event.anarchy.util

import dev.slne.surf.api.paper.permission.PermissionRegistry

object PermissionList : PermissionRegistry() {
    private const val BASE = "surf.event.anarchy"

    val FINALE_COMMAND = create("$BASE.finale.command")
    val FINALE_JOIN = create("$BASE.finale.join")
}