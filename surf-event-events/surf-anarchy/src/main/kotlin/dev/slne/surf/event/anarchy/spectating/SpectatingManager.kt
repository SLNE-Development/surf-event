package dev.slne.surf.event.anarchy.spectating

import dev.slne.surf.api.paper.util.namespacedKey
import dev.slne.surf.event.anarchy.equipment.EquipmentManager
import dev.slne.surf.event.anarchy.kills.KillDisplayManager
import dev.slne.surf.event.anarchy.permission.PermissionList
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

object SpectatingManager {
    private val spectatorKey = namespacedKey("spectator")

    fun isAvailableSpectator(player: Player) = player.hasPermission(PermissionList.DEATH_SPECTATOR)
    fun isSpectator(player: Player) =
        player.persistentDataContainer.get(spectatorKey, PersistentDataType.BOOLEAN) ?: false

    fun setSpectator(player: Player, spectator: Boolean) {
        player.persistentDataContainer.set(
            spectatorKey,
            PersistentDataType.BOOLEAN,
            spectator
        )

        player.gameMode = GameMode.SPECTATOR
    }

    fun resetSpectator(player: Player) {
        player.persistentDataContainer.remove(spectatorKey)
        player.gameMode = GameMode.SURVIVAL
        player.teleportAsync(player.world.spawnLocation)

        player.inventory.clear()

        EquipmentManager.resetEquipment(player)
        EquipmentManager.giveEquipment(player)

        KillDisplayManager.setKills(player, 0)
    }
}