package dev.slne.surf.event.base.paper.settings.listener

import dev.slne.surf.event.base.paper.settings.PlayerVisibilityService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object PlayerConnectionListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PlayerVisibilityService.handleJoin(event.player)
    }
}