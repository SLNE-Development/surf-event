package dev.slne.surf.event.anarchy.equipment

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.inventory.PrepareGrindstoneEvent
import org.bukkit.event.player.PlayerJoinEvent

object EquipmentListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (!EquipmentManager.hasEquipment(player)) {
            EquipmentManager.giveEquipment(player)
        }
    }

    @EventHandler
    fun onAnvil(event: PrepareAnvilEvent) {
        val left = event.inventory.firstItem ?: return
        val right = event.inventory.secondItem ?: return

        if (left.type.isAir || right.type.isAir) return

        if (EquipmentManager.isEquipment(left) || EquipmentManager.isEquipment(right)) {
            event.result = null
        }
    }

    @EventHandler
    fun onEnchant(event: EnchantItemEvent) {
        if (EquipmentManager.isEquipment(event.item)) {
            event.enchantsToAdd.clear()
        }
    }

    @EventHandler
    fun onGrindstone(event: PrepareGrindstoneEvent) {
        if (EquipmentManager.isEquipment(event.result)) {
            event.result = null
        }
    }
}