package dev.slne.surf.event.bmbf.listener

import dev.slne.surf.event.bmbf.BmbfManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object PlayerConnectionListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        BmbfManager.getOrCreatePlotForPlayer(event.player)
    }
}