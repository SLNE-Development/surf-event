package dev.slne.surf.event.bmbf.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.event.bmbf.BmbfManager
import dev.slne.surf.event.bmbf.plugin
import dev.slne.surf.event.bmbf.scoreboard.createBmbfScoreboard
import dev.slne.surf.surfapi.bukkit.api.scoreboard.ObsoleteScoreboardApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object PlayerConnectionListener : Listener {

    @OptIn(ObsoleteScoreboardApi::class)
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        plugin.launch {
            BmbfManager.configurePlayerOnJoin(player)
        }

        val scoreboard = createBmbfScoreboard(player)
        scoreboard.enable()
        scoreboard.addViewer(player)
    }
}