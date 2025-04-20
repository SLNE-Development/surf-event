package dev.slne.surf.event.bmbf.listener

import dev.slne.surf.event.bmbf.BmbfManager
import dev.slne.surf.event.bmbf.util.toPlotPlayer
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object PlayerConnectionListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (!BmbfManager.running) {
            player.gameMode = GameMode.ADVENTURE
        }

        val plot = BmbfManager.getOrCreatePlotForPlayer(player)
        plot.teleportPlayer(player.toPlotPlayer()) {}
    }
}