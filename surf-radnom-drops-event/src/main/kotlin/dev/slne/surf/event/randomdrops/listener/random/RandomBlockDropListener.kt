package dev.slne.surf.event.randomdrops.listener.random

import dev.slne.surf.event.randomdrops.service.PlayerDropService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent

object RandomBlockDropListener : Listener {
    @EventHandler
    fun onBlockDropItem(event: BlockDropItemEvent) {
        val items = event.items
        val uuid = event.player.uniqueId

        for (drop in items) {
            val dropItem = drop.itemStack
            val originalType = dropItem.type.asItemType() ?: return
            val replacedType = PlayerDropService.getReplacedBlockDrop(uuid, originalType)
            drop.itemStack = replacedType.createItemStack(dropItem.amount)
        }
    }
}