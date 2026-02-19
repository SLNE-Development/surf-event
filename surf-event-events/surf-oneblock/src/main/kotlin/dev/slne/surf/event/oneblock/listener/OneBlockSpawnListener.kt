package dev.slne.surf.event.oneblock.listener

import dev.slne.surf.event.oneblock.db.IslandService
import io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

@Suppress("UnstableApiUsage")
object OneBlockSpawnListener : Listener {

    @EventHandler
    fun onPlayerSpawnLocation(event: AsyncPlayerSpawnLocationEvent) {
        val player = event.connection.profile.id ?: return

        if (!OneBlockConnectionListener.shouldTeleportToIsland(player)) {
            return
        }

        val island = IslandService.getIsland(player) ?: return
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