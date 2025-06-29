package dev.slne.surf.event.randomdrops.service

import dev.slne.surf.event.randomdrops.data.PlayerDataStorage
import dev.slne.surf.event.randomdrops.random.RandomDropSelector
import dev.slne.surf.event.randomdrops.util.lootTableOrThrow
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.key.Key
import org.bukkit.Registry
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import org.bukkit.loot.LootContext
import java.util.*

object PlayerDropService {
    fun getReplacedBlockDrop(
        uuid: UUID,
        original: ItemType
    ): ItemType =
        Registry.ITEM.getOrThrow(PlayerDataStorage.getOrCreateReplacedBlockDrop(uuid, original.key))

    fun getReplacedMobDrops(player: Player, entity: Entity): Collection<ItemStack> {
        val replacedType =
            PlayerDataStorage.getOrCreateReplacedMobType(player.uniqueId, entity.type)

        val lootTable = replacedType.lootTableOrThrow()
        val context = LootContext.Builder(entity.location)
            .luck(player.getAttribute(Attribute.LUCK)?.value?.toFloat() ?: 0.0f)
            .killer(player)
            .lootedEntity(entity)
            .build()

        return lootTable.lootTable.populateLoot(null, context)
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