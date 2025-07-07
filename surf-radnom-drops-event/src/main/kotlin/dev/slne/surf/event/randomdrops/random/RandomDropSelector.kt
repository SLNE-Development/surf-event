package dev.slne.surf.event.randomdrops.random

import com.destroystokyo.paper.MaterialTags
import dev.slne.surf.event.randomdrops.config.config
import dev.slne.surf.event.randomdrops.util.lootTable
import dev.slne.surf.surfapi.core.api.util.*
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Registry
import org.bukkit.entity.Damageable
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemType
import kotlin.random.asKotlinRandom

object RandomDropSelector {

    @Suppress("DEPRECATION")
    private val blockDrops = Registry.ITEM.asSequence()
        .filterNot { it in config.randomBlockDropFilter }
        .filterNot { type ->
            type.asMaterial()?.let { MaterialTags.SPAWN_EGGS.isTagged(it) } == true
        }
        .toObjectList()

    private val blockDropsWithRarity = mutableObject2ObjectMapOf<ItemRarity, ObjectList<ItemType>>()

    private val entityTypes = EntityType.entries.asSequence()
        .filter { type -> type.entityClass?.let { Damageable::class.java.isAssignableFrom(it) } == true }
        .filter { it.lootTable() != null }
        .filter { it.isSpawnable }
        .filter { it !in objectSetOf(EntityType.PILLAGER, EntityType.SHEEP, EntityType.SLIME) }
        .toObjectList()


    init {
        for (type in blockDrops) {
            val rarity = type.itemRarity ?: continue
            blockDropsWithRarity.computeIfAbsent(rarity) { mutableObjectListOf() }.add(type)
        }
    }

    fun selectRandomBlockDrop(rarity: ItemRarity? = null): ItemType =
        (if (rarity != null) blockDropsWithRarity[rarity]!! else blockDrops).random(random.asKotlinRandom())

    fun selectRandomEntityType(): EntityType = entityTypes.random(random.asKotlinRandom())
}