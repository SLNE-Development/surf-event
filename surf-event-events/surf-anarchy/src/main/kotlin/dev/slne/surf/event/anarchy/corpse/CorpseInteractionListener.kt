package dev.slne.surf.event.anarchy.corpse

import com.github.benmanes.caffeine.cache.Caffeine
import com.jeff_media.morepersistentdatatypes.DataType
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.mutableObject2ObjectMapOf
import dev.slne.surf.api.core.util.random
import dev.slne.surf.api.paper.util.getPrefixedName
import dev.slne.surf.event.anarchy.permission.PermissionList
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import org.bukkit.Bukkit
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Inventory
import java.util.*
import kotlin.time.Duration.Companion.minutes

object CorpseInteractionListener : Listener {
    private data class CorpseSession(
        val standUuid: UUID,
        val inventory: Inventory,
    )

    private val sessionByInventory = mutableObject2ObjectMapOf<Inventory, CorpseSession>()
    private val sessionByStand = mutableObject2ObjectMapOf<UUID, CorpseSession>()

    private val dupeMitigations = Caffeine.newBuilder()
        .expireAfterWrite(5.minutes)
        .build<Int, UUID>()

    @EventHandler(priority = EventPriority.HIGH)
    fun onInteractAtEntity(event: PlayerInteractAtEntityEvent) {
        val stand = event.rightClicked as? ArmorStand ?: return
        if (!CorpseManager.isCorpse(stand)) return

        event.isCancelled = true

        if (event.player.openInventory.topInventory.type != InventoryType.CRAFTING) {
            handleDupe(event.player)
            return
        }

        val standUuid = stand.uniqueId

        sessionByStand[standUuid]?.let { session ->
            event.player.openInventory(session.inventory)
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

            CorpseManager.corpseOwners.remove(stand.uniqueId)
            stand.remove()
            return
        }

        val size = minOf(54, ((stored.size / 9) + 1) * 9).coerceAtLeast(9)
        val gui = Bukkit.createInventory(null, size, buildText {
            error("☠ $ownerName's Leiche")
        })

        stored.take(size).forEachIndexed { i, item -> gui.setItem(i, item) }
        pdc.remove(CorpseManager.KEY_INVENTORY)

        val session = CorpseSession(standUuid, gui)
        sessionByInventory[gui] = session
        sessionByStand[standUuid] = session

        event.player.openInventory(gui)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val session = sessionByInventory[event.inventory] ?: return
        val remainingViewers = event.inventory.viewers.size - 1

        if (remainingViewers > 0) {
            return
        }

        finalizeSession(session)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        handleForcedClose(event.player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerKick(event: PlayerKickEvent) {
        handleForcedClose(event.player)
    }

    private fun handleForcedClose(player: Player) {
        val inventory = player.openInventory.topInventory
        val session = sessionByInventory[inventory] ?: return

        inventory.viewers.toList().forEach { it.closeInventory() }

        if (inventory.viewers.isEmpty()) {
            finalizeSession(session)
        }
    }

    private fun finalizeSession(session: CorpseSession) {
        sessionByInventory.remove(session.inventory)
        sessionByStand.remove(session.standUuid)

        sessionByInventory.remove(session.inventory)
        sessionByStand.remove(session.standUuid)

        val remaining = session.inventory.contents
            .filterNotNull()
            .filter { !it.isEmpty }

        val stand = findStand(session.standUuid) ?: return

        if (remaining.isEmpty()) {
            CorpseManager.corpseOwners.remove(stand.uniqueId)
            stand.remove()
        } else {
            stand.persistentDataContainer.set(
                CorpseManager.KEY_INVENTORY,
                DataType.ITEM_STACK_ARRAY,
                remaining.toTypedArray()
            )
        }
    }

    private fun findStand(uuid: UUID): ArmorStand? =
        Bukkit.getEntity(uuid) as? ArmorStand

    private const val MAX_MITIGATIONS = 5

    private fun handleDupe(player: Player) {
        dupeMitigations.put(random.nextInt(), player.uniqueId)

        val summedMitigations =
            dupeMitigations.asMap().filter { it.value == player.uniqueId }.size

        Bukkit.broadcast(buildText {
            darkSpacer(">>")
            darkRed(" AntiCheat")
            darkSpacer(" | ")
            append(player.getPrefixedName())
            info(" failed ")
            white("Dupe *")
            info("(")
            white("Type A")
            info(")")
            white("*")
            info(" [")
            darkRed(summedMitigations)
            info("/")
            darkRed(MAX_MITIGATIONS)
            info("]")

            hoverEvent(buildText {
                info("Player tried to open a corpse while already having an inventory open")
            })
        }, PermissionList.ANTI_DUPE_NOTIFY)

        if (summedMitigations >= MAX_MITIGATIONS) {
            player.kick(buildText {
                white("You are sending suspicious packets. If you think this is an error, please contact the server staff.")
            })
            dupeMitigations.asMap()
                .filter { it.value == player.uniqueId }.keys.forEach { dupeMitigations.invalidate(it) }
        }
    }
}