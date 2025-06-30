package dev.slne.surf.event.randomdrops.service

import dev.slne.surf.event.randomdrops.data.PlayerDataStorage
import dev.slne.surf.event.randomdrops.random.RandomDropSelector
import dev.slne.surf.surfapi.bukkit.api.nms.bridges.nmsLootTableBridge
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.key.Key
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

    fun getReplacedMobDrops(
        player: Player,
        entity: LivingEntity,
        damageSource: DamageSource
    ): Collection<ItemStack> {
        val replacedType =
            PlayerDataStorage.getOrCreateReplacedMobType(player.uniqueId, entity.type)

        return nmsLootTableBridge.getDifferentLootTable(entity, damageSource, replacedType, true)
    }

    fun generateReplacedLootDrop(original: Collection<ItemStack>): ObjectList<ItemStack> {
        val replacementKeys = mutableObject2ObjectMapOf<Key, Key>()
        val replacements = mutableObjectListOf<ItemStack>()

        for (stack in original) {
            val originalType = stack.type.asItemType()
            if (originalType == null) {
                replacements.add(stack)
                continue
            }
            val replacementKey = replacementKeys.computeIfAbsent(originalType.key) {
                RandomDropSelector.selectRandomBlockDrop(originalType.itemRarity).key
            }
            val replacementType = Registry.ITEM.getOrThrow(replacementKey)
            replacements.add(replacementType.createItemStack(stack.amount))
        }

        return replacements
    }
}