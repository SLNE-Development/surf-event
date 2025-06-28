package dev.slne.surf.event.randomdrops.listener.random

import dev.slne.surf.event.randomdrops.random.RandomDropSelector
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.LootGenerateEvent

object RandomLootTable : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onLootGenerate(event: LootGenerateEvent) {
        val replacedLoot = event.loot.map {
            RandomDropSelector.selectRandomBlockDrop().createItemStack(it.amount)
        }
        event.setLoot(replacedLoot)
    }
}