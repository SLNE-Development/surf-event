package dev.slne.surf.event.randomdrops.service

import dev.slne.surf.event.randomdrops.data.PlayerDataStorage
import dev.slne.surf.surfapi.bukkit.api.nms.bridges.nmsLootTableBridge
import org.bukkit.Registry
import org.bukkit.damage.DamageSource
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import java.util.*

object PlayerDropService {
    fun getReplacedBlockDrop(
        uuid: UUID,
        original: ItemType
    ): ItemType =
        Registry.ITEM.getOrThrow(PlayerDataStorage.getOrCreateReplacedBlockDrop(uuid, original.key))

    fun replaceBlockDrops(
        uuid: UUID,
        iterator: MutableListIterator<out ItemStack?>
    ) {
        val iterator = iterator as MutableListIterator<ItemStack?>
        while (iterator.hasNext()) {
            val stack = iterator.next() ?: continue
            if (stack.isEmpty) continue

            val originalType = stack.type.asItemType() ?: continue
            val replacementType = getReplacedBlockDrop(uuid, originalType)

            if (replacementType.key != originalType.key) {
                iterator.set(replacementType.createItemStack(stack.amount))
            }
        }
    }

    fun getReplacedMobDrops(
        player: Player,
        entity: LivingEntity,
        damageSource: DamageSource
    ): Collection<ItemStack> {
        val replacedType =
            PlayerDataStorage.getOrCreateReplacedMobType(player.uniqueId, entity.type)

        return nmsLootTableBridge.getDifferentLootTable(entity, damageSource, replacedType, true)
    }
}