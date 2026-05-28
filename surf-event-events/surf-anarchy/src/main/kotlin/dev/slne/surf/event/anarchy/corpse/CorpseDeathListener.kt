package dev.slne.surf.event.anarchy.corpse

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

object CorpseDeathListener : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity

        val contents = player.inventory.contents.clone()

        event.drops.clear()
        event.droppedExp = 0

        CorpseManager.spawnCorpse(
            player = player,
            deathLocation = player.location,
            inventoryContents = contents
        )
    }
}