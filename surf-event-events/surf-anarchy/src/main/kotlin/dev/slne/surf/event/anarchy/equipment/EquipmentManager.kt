package dev.slne.surf.event.anarchy.equipment

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.paper.util.BukkitSound
import dev.slne.surf.api.paper.util.namespacedKey
import dev.slne.surf.event.anarchy.plugin
import kotlinx.coroutines.delay
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import org.bukkit.persistence.PersistentDataType
import kotlin.time.Duration.Companion.milliseconds

object EquipmentManager {
    private val equipmentKey = namespacedKey("equipment")
    private val equipmentItemKey = namespacedKey("equipment_item")

    fun resetEquipment(player: Player) {
        player.persistentDataContainer.remove(equipmentKey)
    }

    fun isEquipment(item: ItemStack?) = item?.itemMeta?.persistentDataContainer?.has(
        equipmentItemKey,
        PersistentDataType.BOOLEAN
    ) == true

    fun hasEquipment(player: Player) = player.persistentDataContainer.has(
        equipmentKey,
        PersistentDataType.BOOLEAN
    )

    fun giveEquipment(player: Player) {
        player.persistentDataContainer.set(
            equipmentKey,
            PersistentDataType.BOOLEAN,
            true
        )

        plugin.launch {
            repeat(4) {
                player.playSound(true) {
                    type(BukkitSound.ITEM_ARMOR_EQUIP_COPPER)
                }
                delay(150.milliseconds)
            }
        }

        fillInventory(player)
    }


    @Suppress("UnstableApiUsage")
    private fun fillInventory(player: Player) {
        player.inventory.setHelmet(
            ItemType.COPPER_HELMET.createItemStack().apply { applyEffects() })
        player.inventory.setChestplate(
            ItemType.COPPER_CHESTPLATE.createItemStack().apply { applyEffects() })
        player.inventory.setLeggings(
            ItemType.COPPER_LEGGINGS.createItemStack().apply { applyEffects() })
        player.inventory.setBoots(ItemType.COPPER_BOOTS.createItemStack().apply { applyEffects() })

        player.inventory.setItem(
            0,
            ItemType.COPPER_SWORD.createItemStack().apply { applyEffects() })
        player.inventory.setItem(1, ItemType.COPPER_AXE.createItemStack().apply { applyEffects() })
        player.inventory.setItem(
            2,
            ItemType.COPPER_PICKAXE.createItemStack().apply { applyEffects() })
        player.inventory.setItem(
            8,
            ItemType.COOKED_COD.createItemStack(16).apply { applyEffects() })

        player.updateInventory()
    }


    private fun ItemStack.applyEffects() {
        val meta = itemMeta ?: return

        meta.isUnbreakable = true

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
        meta.setEnchantable(1)
        meta.persistentDataContainer.set(equipmentItemKey, PersistentDataType.BOOLEAN, true)
    }
}