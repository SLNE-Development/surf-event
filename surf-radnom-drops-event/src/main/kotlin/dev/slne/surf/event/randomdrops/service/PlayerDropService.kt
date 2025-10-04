package dev.slne.surf.event.randomdrops.service

import dev.slne.surf.event.randomdrops.config.effectiveUuid
import dev.slne.surf.event.randomdrops.data.PlayerDataStorage
import dev.slne.surf.surfapi.bukkit.api.nms.bridges.nmsLootTableBridge
import org.bukkit.Registry
import org.bukkit.World
import org.bukkit.damage.DamageSource
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import java.util.*

object PlayerDropService {

    fun getReplacedBlockDrop(
        player: Player,
        original: ItemType,
        world: World = player.world
    ): ItemType {
        val scopeId = effectiveUuid(player.uniqueId, world.uid)
        val key = PlayerDataStorage.getOrCreateReplacedBlockDrop(scopeId, original.key)
        return Registry.ITEM.getOrThrow(key)
    }

    fun getReplacedBlockDrop(
        playerUuid: UUID,
        worldUid: UUID,
        original: ItemType
    ): ItemType {
        val scopeId = effectiveUuid(playerUuid, worldUid)
        val key = PlayerDataStorage.getOrCreateReplacedBlockDrop(scopeId, original.key)
        return Registry.ITEM.getOrThrow(key)
    }

    fun getReplacedMobDrops(
        player: Player,
        entity: LivingEntity,
        damageSource: DamageSource
    ): Collection<ItemStack> {
        val scopeId = effectiveUuid(player.uniqueId, entity.world.uid)
        val replacedType = PlayerDataStorage.getOrCreateReplacedMobType(scopeId, entity.type)
        return nmsLootTableBridge.getDifferentLootTable(entity, damageSource, replacedType, true)
    }

    fun replaceBlockDrops(
        player: Player,
        iterator: MutableListIterator<out ItemStack?>
    ) {
        val iterator = iterator as MutableListIterator<ItemStack?>
        while (iterator.hasNext()) {
            val stack = iterator.next() ?: continue
            if (stack.isEmpty) continue

            val originalType = stack.type.asItemType() ?: continue
            val replacementType = getReplacedBlockDrop(player, originalType)

            if (replacementType.key != originalType.key) {
                iterator.set(replacementType.createItemStack(stack.amount))
            }
        }
    }
}