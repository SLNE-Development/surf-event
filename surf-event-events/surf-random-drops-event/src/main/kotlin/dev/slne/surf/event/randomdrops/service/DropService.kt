package dev.slne.surf.event.randomdrops.service

import dev.slne.surf.event.randomdrops.random.RandomDropSelector
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.key.Key
import org.bukkit.Registry
import org.bukkit.inventory.ItemStack

object DropService {
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

    fun replaceDrops(iterator: MutableListIterator<ItemStack?>) {
        val replacementKeys = mutableObject2ObjectMapOf<Key, Key>()

        while (iterator.hasNext()) {
            val stack = iterator.next() ?: continue
            if (stack.isEmpty) continue

            val originalType = stack.type.asItemType() ?: continue
            val replacementKey = replacementKeys.computeIfAbsent(originalType.key) {
                RandomDropSelector.selectRandomBlockDrop(originalType.itemRarity).key
            }
            val replacementType = Registry.ITEM.getOrThrow(replacementKey)
            iterator.set(replacementType.createItemStack(stack.amount))
        }
    }
}
