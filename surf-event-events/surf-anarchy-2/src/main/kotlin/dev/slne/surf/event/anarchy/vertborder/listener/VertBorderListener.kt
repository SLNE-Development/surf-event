package dev.slne.surf.event.anarchy.vertborder.listener

import dev.slne.surf.event.anarchy.vertborder.VertBorderService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object VertBorderListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        VertBorderService.startFor(event.player)
    }
}