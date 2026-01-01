package dev.slne.surf.event.oneblock.listener

import dev.slne.surf.event.oneblock.db.IslandService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent

object OneBlockSpawnListener : Listener {

    @EventHandler
    fun onPlayerSpawnLocation(event: PlayerSpawnLocationEvent) {
        val player = event.player
        if (!OneBlockConnectionListener.shouldTeleportToIsland(player.uniqueId) && player.hasPlayedBefore()) return

        val island = IslandService.getIsland(player.uniqueId) ?: return
        event.spawnLocation = island.oneBlock.clone().add(0.5, 1.0, 0.5)
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        if (event.isMissingRespawnBlock || !event.isBedSpawn && !event.isAnchorSpawn) {
            val island = IslandService.getIsland(event.player.uniqueId) ?: return
            event.respawnLocation = island.oneBlock.clone().add(0.5, 1.0, 0.5)
        }
    }
}