package dev.slne.surf.event.randomdrops.listener.random

import com.destroystokyo.paper.event.block.BlockDestroyEvent
import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.event.randomdrops.service.PlayerDropService
import io.papermc.paper.event.block.PlayerShearBlockEvent
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
import org.bukkit.event.player.PlayerHarvestBlockEvent
import org.bukkit.event.player.PlayerShearEntityEvent
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
        val player = event.player
        breakSource.put(event.block.location, event.player)

        for (drop in items) {
            val dropItem = drop.itemStack
            val originalType = dropItem.type.asItemType() ?: continue
            val replacedType = PlayerDropService.getReplacedBlockDrop(player, originalType, event.block.world)
            drop.itemStack = replacedType.createItemStack(dropItem.amount)
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block
        val holder = block.state as? InventoryHolder ?: return

        if (!isTrustedByChestProtect(block, event.player)) return

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
                PlayerDropService.getReplacedBlockDrop(sourcePlayer, originalType, event.block.world)

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
            sourcePlayer,
            originalStack.type.asItemType() ?: return,
            event.entity.world
        ).createItemStack(originalStack.amount)
    }

    @EventHandler
    fun onPlayerShearBlock(event: PlayerShearBlockEvent) {
        PlayerDropService.replaceBlockDrops(
            event.player,
            event.drops.listIterator()
        )
    }

    @EventHandler
    fun onPlayerShearEntity(event: PlayerShearEntityEvent) {
        val drops = event.drops.toMutableList()
        PlayerDropService.replaceBlockDrops(
            event.player,
            drops.listIterator()
        )
        event.drops = drops
    }

    @EventHandler
    fun onPlayerHarvestBlock(event: PlayerHarvestBlockEvent) {
        PlayerDropService.replaceBlockDrops(
            event.player,
            event.itemsHarvested.listIterator()
        )
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

    private fun isTrustedByChestProtect(block: Block, player: Player): Boolean {
        return try {
            // Reflection to avoid hard dependency on ChestProtect
            val apiClazz = Class.forName("me.angeschossen.chestprotect.api.ChestProtectAPI")
            val getInstance = apiClazz.getMethod("getInstance")
            val api = getInstance.invoke(null)

            val getProt = apiClazz.getMethod("getBlockProtectionByBlock", Block::class.java)
            val prot = getProt.invoke(api, block) ?: return true

            val isTrusted = prot.javaClass.getMethod("isTrusted", UUID::class.java)
            isTrusted.invoke(prot, player.uniqueId) as Boolean
        } catch (e: Throwable) {
            // ChestProtect not present
            true
        }
    }
}