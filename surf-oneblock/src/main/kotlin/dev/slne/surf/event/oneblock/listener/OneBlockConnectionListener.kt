package dev.slne.surf.event.oneblock.listener

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent
import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.island.IslandManager
import dev.slne.surf.event.oneblock.messages.MessageManager
import dev.slne.surf.event.oneblock.session.PlayerSessionManager
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent

@Suppress("UnstableApiUsage")
object OneBlockConnectionListener : Listener {

    @EventHandler
    fun onAsyncPlayerConnectionConfigure(event: AsyncPlayerConnectionConfigureEvent) {
        val playerId = event.connection.profile.id ?: error("Player ID is null")
        PlayerSessionManager.createSession(playerId)
        if (IslandService.hasIsland(playerId)) return

        runBlocking {
            val created = IslandManager.createIslandForPlayer(playerId)
            if (!created) {
                event.connection.disconnect(MessageManager.unableToCreateIslandDisconnect)
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (!server.isStopping) {
            IslandService.flush(event.player.uniqueId)
        }
    }

    @EventHandler
    fun onPlayerSpawnLocation(event: PlayerSpawnLocationEvent) {
        val playerId = event.player.uniqueId
        val island = IslandService.getIsland(playerId) ?: return
        event.spawnLocation = island.oneBlock.clone().add(0.5, 1.0, 0.5)
    }

    @EventHandler
    fun onPlayerConnectionClose(event: PlayerConnectionCloseEvent) {
        PlayerSessionManager.clearSession(event.playerUniqueId)
    }
}