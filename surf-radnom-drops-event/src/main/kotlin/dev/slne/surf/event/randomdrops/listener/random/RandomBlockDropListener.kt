package dev.slne.surf.event.randomdrops.listener.random

import dev.slne.surf.event.randomdrops.service.PlayerDropService
import org.bukkit.block.Container
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.InventoryHolder

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

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block.state
        if (block is InventoryHolder) {
            val inventory = block.inventory
            val content = inventory.contents
            inventory.clear()

            for (content in content) {
                if (content == null) continue
                block.world.dropItemNaturally(block.location, content)
            }
        }
    }
}