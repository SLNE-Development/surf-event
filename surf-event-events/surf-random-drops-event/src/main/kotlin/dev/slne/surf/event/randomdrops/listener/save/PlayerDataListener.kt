package dev.slne.surf.event.randomdrops.listener.save

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.event.randomdrops.config.GLOBAL_UUID
import dev.slne.surf.event.randomdrops.config.RandomizationScope
import dev.slne.surf.event.randomdrops.config.config
import dev.slne.surf.event.randomdrops.data.PlayerDataStorage
import dev.slne.surf.event.randomdrops.plugin
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.surfapi.core.api.util.logger
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.WorldSaveEvent
import kotlin.system.measureTimeMillis

object PlayerDataListener : Listener {

    private val log = logger()

    @EventHandler
    fun onWorldSave(event: WorldSaveEvent) {
        plugin.launch {
            try {
                val ms = measureTimeMillis { PlayerDataStorage.flush() }
                log.atInfo().log("Player data flushed in $ms ms")
            } catch (e: Throwable) {
                log.atWarning().withCause(e).log("Failed to flush player data on world save")
            }
        }
    }

    @EventHandler
    fun onAsyncPlayerPreLogin(event: AsyncPlayerPreLoginEvent) {
        try {
            when (config.randomizationScope) {
                RandomizationScope.PLAYER -> runBlocking {
                    PlayerDataStorage.loadCache(event.uniqueId)
                }
                RandomizationScope.GLOBAL -> runBlocking {
                    PlayerDataStorage.loadCache(GLOBAL_UUID)
                }
                RandomizationScope.WORLD -> {
                    // Will be loaded on world load
                }
            }
        } catch (e: Throwable) {
            log.atWarning()
                .withCause(e)
                .log("Failed to load player data for ${event.name} (${event.uniqueId})")

            event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                CommonComponents.renderDisconnectMessage(
                    SurfComponentBuilder(),
                    "SPIELERDATEN KONNTEN NICHT GELADEN WERDEN",
                    {
                        variableValue("Deine Spieler-Daten konnten nicht geladen werden.")
                        appendNewline()
                        variableValue("Leider ist es so nicht möglich, auf den Server zu joinen.")
                    },
                    issue = true
                )
            )
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (server.isStopping) return // Should be saved by onDisableAsync
        when (config.randomizationScope) {
            RandomizationScope.PLAYER -> plugin.launch {
                PlayerDataStorage.destroyCache(event.player.uniqueId)
            }
            RandomizationScope.GLOBAL, RandomizationScope.WORLD -> {
                // Nothing to do
            }
        }
    }
}