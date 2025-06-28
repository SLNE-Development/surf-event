package dev.slne.surf.event.randomdrops.service

import dev.slne.surf.event.randomdrops.data.PlayerDataStorage
import dev.slne.surf.event.randomdrops.random.RandomDropSelector
import dev.slne.surf.event.randomdrops.util.lootTableOrThrow
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import net.kyori.adventure.key.Key
import org.bukkit.Location
import org.bukkit.Registry
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent
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
        val replacedType = PlayerDataStorage.getOrCreateReplacedMobType(player.uniqueId, entity.type)

        println(
            "Replaced entity type: ${
                replacedType.key().value()
            } for original type: ${entity.type.key().value()}"
        )

        val lootTable = replacedType.lootTableOrThrow()
        val tempEntity = entity.world.spawnEntity(
            Location.BLOCK_ZERO.toLocation(entity.world),
            replacedType,
            CreatureSpawnEvent.SpawnReason.CUSTOM
        ) { temp ->
            with(temp) {
                isInvulnerable = true
                isSilent = true
                isPersistent = false
            }
        }

        val context = LootContext.Builder(entity.location)
            .luck(player.getAttribute(Attribute.LUCK)?.value?.toFloat() ?: 0.0f)
            .killer(player)
            .lootedEntity(tempEntity)
            .build()

        val drops = lootTable.lootTable.populateLoot(null, context)
        tempEntity.remove()

        return drops
    }
}