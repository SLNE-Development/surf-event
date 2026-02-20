package dev.slne.surf.event.oneblock.listener

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.island.IslandManager
import dev.slne.surf.event.oneblock.messages.MessageManager
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.event.oneblock.progress.phaseConfig
import dev.slne.surf.event.oneblock.session.PlayerSessionManager
import dev.slne.surf.stats.api.surfStatsApi
import dev.slne.surf.surfapi.core.api.util.logger
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Suppress("UnstableApiUsage")
object OneBlockConnectionListener : Listener {
    private val log = logger()
    private val tpToIsland = ConcurrentHashMap.newKeySet<UUID>()

    @EventHandler
    fun onAsyncPlayerConnectionConfigure(event: AsyncPlayerConnectionConfigureEvent) {
        val playerId = event.connection.profile.id ?: error("Player ID is null")

        try {
            runBlocking {
                PlayerSessionManager.createSession(playerId)

                if (!IslandService.hasIsland(playerId)) {
                    val created = IslandManager.createIslandForPlayer(playerId)
                    if (!created) {
                        event.connection.disconnect(MessageManager.unableToCreateIslandDisconnect)
                    } else {
                        tpToIsland.add(playerId)
                    }
                }
            }
        } catch (e: Exception) {
            log.atSevere()
                .withCause(e)
                .log("Failed to create player session for player ${event.connection.profile.name} ($playerId)")

            event.connection.disconnect(MessageManager.unknownJoinError)
        }
    }

    @EventHandler
    fun onPlayerConnectionClose(event: PlayerConnectionCloseEvent) {
        PlayerSessionManager.clearSession(event.playerUniqueId)
        tpToIsland.remove(event.playerUniqueId)
    }

    fun shouldTeleportToIsland(playerId: UUID): Boolean {
        return tpToIsland.remove(playerId)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val island = IslandService.getIsland(event.player.uniqueId) ?: return

        plugin.launch {
            surfStatsApi.saveCustomStat(
                event.player.uniqueId,
                event.player.name,
                "minecraft:one_block_mined",
                island.totalMined
            )

            surfStatsApi.saveCustomStat(
                event.player.uniqueId,
                event.player.name,
                "minecraft:one_block_level",
                phaseConfig.currentPhase(island.totalMined).weight.toLong()
            )
        }
    }
}