package dev.slne.surf.event.randomdrops.listener.random

import com.destroystokyo.paper.event.block.BlockDestroyEvent
import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.event.randomdrops.service.PlayerDropService
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.PointedDripstone
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.inventory.InventoryHolder
import java.util.*
import kotlin.time.Duration.Companion.minutes

object RandomBlockDropListener : Listener {
    private val breakSource = Caffeine.newBuilder()
        .expireAfterWrite(1.minutes)
        .weakValues()
        .build<Location, Player>()

    private val breakBlockChangeSource = Caffeine.newBuilder()
        .expireAfterWrite(1.minutes)
        .weakValues()
        .build<UUID, Player>()

    @EventHandler
    fun onBlockDropItem(event: BlockDropItemEvent) {
        val items = event.items
        val uuid = event.player.uniqueId
        breakSource.put(event.block.location, event.player)

        for (drop in items) {
            val dropItem = drop.itemStack
            val originalType = dropItem.type.asItemType() ?: continue
            val replacedType = PlayerDropService.getReplacedBlockDrop(uuid, originalType)
            drop.itemStack = replacedType.createItemStack(dropItem.amount)
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block
        val holder = block.state as? InventoryHolder ?: return
        holder.inventory.storageContents
            .filterNotNull()
            .forEach { block.world.dropItemNaturally(block.location, it) }
        holder.inventory.clear()
    }

    @EventHandler
    fun onBlockDestroy(event: BlockDestroyEvent) {
        val sourcePlayer = findSourcePlayer(event.block) ?: return

        event.setWillDrop(false)
        breakSource.put(event.block.location, sourcePlayer)

        val drops = event.block.getDrops(sourcePlayer.inventory.itemInMainHand, sourcePlayer)
        for (drop in drops) {
            val originalType = drop.type.asItemType() ?: continue
            val replacedType =
                PlayerDropService.getReplacedBlockDrop(sourcePlayer.uniqueId, originalType)

            event.block.world.dropItemNaturally(
                event.block.location,
                replacedType.createItemStack(drop.amount)
            )
        }
    }

    @EventHandler
    fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
        val block = event.block
        val sourcePlayer = findSourcePlayer(block) ?: return
        val entity = event.entity

        breakBlockChangeSource.put(entity.uniqueId, sourcePlayer)
        breakSource.put(block.location, sourcePlayer)
    }

    @EventHandler
    fun onEntityDropItem(event: EntityDropItemEvent) {
        val sourcePlayer = breakBlockChangeSource.asMap().remove(event.entity.uniqueId) ?: return
        val item = event.itemDrop
        val originalStack = item.itemStack

        item.itemStack = PlayerDropService.getReplacedBlockDrop(
            sourcePlayer.uniqueId,
            originalStack.type.asItemType() ?: return
        ).createItemStack(originalStack.amount)
    }

    private val DOWN_ONLY = arrayOf(BlockFace.DOWN)
    private val CHORUS_CHECK = arrayOf(
        BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST
    )

    private fun findSourcePlayer(block: Block): Player? {
        val sourceFaces = when (block.type) {
            Material.POINTED_DRIPSTONE -> {
                val pointedDripstone = block.blockData as PointedDripstone
                pointedDripstone.verticalDirections.toTypedArray()
            }

            Material.CHORUS_PLANT -> CHORUS_CHECK
            else -> DOWN_ONLY
        }

        return sourceFaces.asSequence()
            .map { block.getRelative(it).location }
            .mapNotNull { breakSource.getIfPresent(it) }
            .firstOrNull()
    }
}