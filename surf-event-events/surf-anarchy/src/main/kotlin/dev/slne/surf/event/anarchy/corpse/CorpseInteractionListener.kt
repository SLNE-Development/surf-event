package dev.slne.surf.event.anarchy.corpse

import com.jeff_media.morepersistentdatatypes.DataType
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.mutableObject2ObjectMapOf
import dev.slne.surf.event.anarchy.permission.PermissionList
import dev.slne.surf.event.anarchy.plugin
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import org.bukkit.Bukkit
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.Inventory
import java.util.*

object CorpseInteractionListener : Listener {
    private val openCorpseInventories = mutableObject2ObjectMapOf<Inventory, UUID>()

    @EventHandler
    fun onInteractAtEntity(event: PlayerInteractAtEntityEvent) {
        val stand = event.rightClicked as? ArmorStand ?: return

        if (!CorpseManager.isCorpse(stand)) {
            return
        }

        event.isCancelled = true

        if (event.player.openInventory.topInventory.type != InventoryType.CRAFTING) {
            Bukkit.broadcast(buildText {
                appendAnarchyPrefix()
                error("${event.player.name} may tried to dupe!")
            }, PermissionList.DUPE_NOTIFY)
            return
        }
        
        val pdc = stand.persistentDataContainer
        val stored = pdc.get(CorpseManager.KEY_INVENTORY, DataType.ITEM_STACK_ARRAY)
        val ownerName = pdc.getOrDefault(CorpseManager.KEY_OWNER, DataType.STRING, "#unknown")

        if (stored.isNullOrEmpty()) {
            event.player.sendText {
                appendAnarchyPrefix()
                error("Die Leiche ist leer.")
            }
            return
        }

        val size = minOf(54, ((stored.size / 9) + 1) * 9).coerceAtLeast(9)
        val gui = Bukkit.createInventory(null, size, buildText {
            error("☠ $ownerName's Leiche")
        })

        stored.filterIndexed { index, _ -> index < size }.forEachIndexed { i, item ->
            gui.setItem(i, item)
        }

        openCorpseInventories[gui] = stand.uniqueId
        event.player.openInventory(gui)
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val standUuid = openCorpseInventories.remove(event.inventory) ?: return
        val stand = findStand(standUuid) ?: return
        val remaining = event.inventory.contents.filterNotNull()

        if (remaining.isEmpty()) {
            CorpseManager.corpseOwners.remove(stand.uniqueId)
            stand.remove()
            event.player.sendText {
                appendAnarchyPrefix()
                success("Die Leiche ist verschwunden...")
            }
        } else {
            stand.persistentDataContainer.set(
                CorpseManager.KEY_INVENTORY,
                DataType.ITEM_STACK_ARRAY,
                remaining.toTypedArray()
            )
        }
    }

    private fun findStand(uuid: UUID): ArmorStand? =
        plugin.server.worlds
            .flatMap { it.entities }
            .filterIsInstance<ArmorStand>()
            .firstOrNull { it.uniqueId == uuid }
}