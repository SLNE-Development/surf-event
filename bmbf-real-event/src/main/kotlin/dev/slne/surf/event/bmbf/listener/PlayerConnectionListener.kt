package dev.slne.surf.event.bmbf.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.event.bmbf.BmbfManager
import dev.slne.surf.event.bmbf.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object PlayerConnectionListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        plugin.launch {
            BmbfManager.configurePlayerOnJoin(player)
        }
    }
}