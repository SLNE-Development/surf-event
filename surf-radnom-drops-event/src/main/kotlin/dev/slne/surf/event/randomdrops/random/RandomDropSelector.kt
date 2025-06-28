package dev.slne.surf.event.randomdrops.random

import dev.slne.surf.event.randomdrops.util.lootTable
import dev.slne.surf.surfapi.core.api.util.random
import dev.slne.surf.surfapi.core.api.util.toObjectList
import org.bukkit.Registry
import org.bukkit.entity.Damageable
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemType
import kotlin.random.asKotlinRandom

object RandomDropSelector {

    private val blockDrops = Registry.ITEM.toObjectList()
    private val entityTypes = EntityType.entries
        .filter { type -> type.entityClass?.let { Damageable::class.java.isAssignableFrom(it) } == true }
        .filter { it.lootTable() != null }
        .filter { it.isSpawnable }
        .toObjectList()


    fun selectRandomBlockDrop(): ItemType = blockDrops.random(random.asKotlinRandom())
    fun selectRandomEntityType(): EntityType = entityTypes.random(random.asKotlinRandom())
}