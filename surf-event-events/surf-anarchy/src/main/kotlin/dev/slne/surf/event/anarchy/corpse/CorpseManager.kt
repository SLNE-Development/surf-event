package dev.slne.surf.event.anarchy.corpse

import com.jeff_media.morepersistentdatatypes.DataType
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.mutableObject2ObjectMapOf
import dev.slne.surf.api.paper.builder.buildItem
import dev.slne.surf.api.paper.util.namespacedKey
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.util.EulerAngle
import java.util.*

@Suppress("UnstableApiUsage")
object CorpseManager {
    val corpseOwners = mutableObject2ObjectMapOf<UUID, String>()
    val corpseGroups = mutableObject2ObjectMapOf<UUID, List<UUID>>()

    val KEY_INVENTORY = namespacedKey("inventory")
    val KEY_IS_CORPSE = namespacedKey("is_corpse")
    val KEY_OWNER = namespacedKey("owner_name")

    fun spawnCorpse(player: Player, deathLocation: Location, inventoryContents: Array<ItemStack?>) {
        val spawnLoc = deathLocation.clone().apply {
            add(0.0, -1.2, 0.0)
        }

        val stand = spawnLoc.world.spawn(spawnLoc, ArmorStand::class.java) {
            it.isVisible = false
            it.setGravity(false)
            it.setCanMove(false)
            it.setArms(true)
            it.setBasePlate(false)
            it.isSmall = false
            it.isInvulnerable = true
            it.customName(buildText { error("☠ ${player.name}") })
            it.isCustomNameVisible = true

            it.headPose = EulerAngle(Math.toRadians(-75.0), 0.0, Math.toRadians(10.0))
            it.bodyPose = EulerAngle(Math.toRadians(-75.0), 0.0, 0.0)
            it.leftArmPose = EulerAngle(Math.toRadians(-75.0), 0.0, Math.toRadians(-18.0))
            it.rightArmPose = EulerAngle(Math.toRadians(-75.0), 0.0, Math.toRadians(18.0))
            it.leftLegPose = EulerAngle(Math.toRadians(75.0), 0.0, 0.0)
            it.rightLegPose = EulerAngle(Math.toRadians(75.0), 0.0, 0.0)

            it.equipment.apply {
                setHelmet(buildItem(Material.PLAYER_HEAD) {
                    editMeta(SkullMeta::class.java) { meta ->
                        meta.owningPlayer = player
                    }
                })
                setChestplate(ItemType.LEATHER_CHESTPLATE.createItemStack())
            }

            it.persistentDataContainer.apply {
                set(KEY_IS_CORPSE, DataType.BOOLEAN, true)
                set(KEY_OWNER, DataType.STRING, player.name)
                set(
                    KEY_INVENTORY,
                    DataType.ITEM_STACK_ARRAY,
                    inventoryContents.filterNotNull().toTypedArray()
                )
            }
        }

        corpseOwners[stand.uniqueId] = player.name
        corpseGroups[stand.uniqueId] = listOf(stand.uniqueId)
    }

    fun isCorpse(stand: ArmorStand): Boolean =
        stand.persistentDataContainer.has(KEY_IS_CORPSE, DataType.BOOLEAN)
}