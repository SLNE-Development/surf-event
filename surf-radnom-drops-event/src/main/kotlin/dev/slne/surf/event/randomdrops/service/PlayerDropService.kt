package dev.slne.surf.event.randomdrops.service

import dev.slne.surf.event.randomdrops.data.PlayerDataStorage
import dev.slne.surf.event.randomdrops.util.lootTableOrThrow
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
}