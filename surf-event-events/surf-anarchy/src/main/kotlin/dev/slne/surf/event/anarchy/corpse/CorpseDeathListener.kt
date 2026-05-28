package dev.slne.surf.event.anarchy.corpse

import dev.slne.surf.event.anarchy.spectating.SpectatingManager
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

object CorpseDeathListener : Listener {
    private val spectatingGamemodes = listOf(GameMode.CREATIVE, GameMode.SPECTATOR)

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity

        if (player.gameMode in spectatingGamemodes) {
            return
        }

        if (SpectatingManager.isSpectator(player)) {
            return
        }

        val contents = player.inventory.contents.clone()

        event.drops.clear()
        event.droppedExp = 0

        CorpseManager.spawnCorpse(
            player = player,
            deathLocation = player.location,
            inventoryContents = contents
        )
    }
}