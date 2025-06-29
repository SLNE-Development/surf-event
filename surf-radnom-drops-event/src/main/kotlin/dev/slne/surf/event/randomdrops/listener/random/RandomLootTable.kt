package dev.slne.surf.event.randomdrops.listener.random

import dev.slne.surf.event.randomdrops.service.PlayerDropService
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.LootGenerateEvent

object RandomLootTable : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onLootGenerate(event: LootGenerateEvent) {
        event.setLoot(PlayerDropService.generateReplacedLootDrop(event.loot))
    }
}