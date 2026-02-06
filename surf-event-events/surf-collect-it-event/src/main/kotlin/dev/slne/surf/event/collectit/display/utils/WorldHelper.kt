package dev.slne.surf.event.collectit.display.utils

import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.util.toObjectList

object WorldHelper {
    const val HUB_WORLD_NAME = "hub"

    val HUB_WORLD = server.getWorld(HUB_WORLD_NAME) ?: error("Hub world not found")

    fun getHubPlayers() = HUB_WORLD.players.toObjectList()
}