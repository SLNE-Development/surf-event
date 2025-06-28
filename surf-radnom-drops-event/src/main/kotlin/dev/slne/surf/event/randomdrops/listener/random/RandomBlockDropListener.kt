package dev.slne.surf.event.randomdrops.listener.random

import com.destroystokyo.paper.event.block.BlockDestroyEvent
import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.event.randomdrops.service.PlayerDropService
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.InventoryHolder
import kotlin.time.Duration.Companion.minutes

object RandomBlockDropListener : Listener {
    private val breakSource = Caffeine.newBuilder()
        .expireAfterWrite(1.minutes)
        .weakValues()
        .build<Location, Player>()

    @EventHandler
    fun onBlockDropItem(event: BlockDropItemEvent) {
        val items = event.items
        val uuid = event.player.uniqueId
        breakSource.put(event.block.location, event.player)

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

    @EventHandler
    fun onBlockDestroy(event: BlockDestroyEvent) {
        val blockSourceCheck = when (event.block.type) {
            Material.CHORUS_PLANT -> listOf(
                BlockFace.DOWN,
                BlockFace.NORTH,
                BlockFace.SOUTH,
                BlockFace.EAST,
                BlockFace.WEST
            )

            else -> listOf(BlockFace.DOWN)
        }

        val sourcePlayer = blockSourceCheck
            .map { event.block.getRelative(it) }
            .map { breakSource.getIfPresent(it.location) }
            .firstOrNull { it != null } ?: return

        event.setWillDrop(false)
        val drops = event.block.getDrops(sourcePlayer.inventory.itemInMainHand, sourcePlayer)
        breakSource.put(event.block.location, sourcePlayer)

        for (drop in drops) {
            val originalType = drop.type.asItemType() ?: return
            val replacedType =
                PlayerDropService.getReplacedBlockDrop(sourcePlayer.uniqueId, originalType)

            event.block.world.dropItemNaturally(
                event.block.location,
                replacedType.createItemStack(drop.amount)
            )
        }
    }
}