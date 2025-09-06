package dev.slne.surf.event.oneblock.listener

import dev.slne.surf.event.oneblock.island.IslandManager
import dev.slne.surf.event.oneblock.messages.MessageManager
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@Suppress("UnstableApiUsage")
class OneBlockConnectionListener : Listener {
    @EventHandler
    fun onAsyncPlayerConnectionConfigure(event: AsyncPlayerConnectionConfigureEvent) {
        val playerId = event.connection.profile.id ?: error("Player ID is null")
        if (IslandManager.hasIsland(playerId)) return

        runBlocking {
            val created = IslandManager.createIslandForPlayer(playerId)
            if (!created) {
                event.connection.disconnect(MessageManager.unableToCreateIslandDisconnect)
            }
        }
    }
}