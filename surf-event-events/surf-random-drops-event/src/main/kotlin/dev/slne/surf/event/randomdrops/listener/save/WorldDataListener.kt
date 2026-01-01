package dev.slne.surf.event.randomdrops.listener.save

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.event.randomdrops.config.isWorldScope
import dev.slne.surf.event.randomdrops.data.PlayerDataStorage
import dev.slne.surf.event.randomdrops.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent
import dev.slne.surf.surfapi.core.api.util.logger
import kotlinx.coroutines.runBlocking

object WorldDataListener : Listener {

    private val log = logger()

    fun initialLoad() {
        if (!isWorldScope) return
        plugin.server.worlds.forEach { world ->
            try {
                runBlocking { PlayerDataStorage.loadCache(world.uid) }
                println("Loaded data for world ${world.name} (${world.uid})")
            } catch (e: Throwable) {
                log.atWarning()
                    .withCause(e)
                    .log("Failed to load data for world ${world.name} (${world.uid})")
            }
        }
    }

    @EventHandler
    fun onWorldLoad(event: WorldLoadEvent) {
        if (!isWorldScope) return
        plugin.launch {
            try {
                PlayerDataStorage.loadCache(event.world.uid)
                println("Loaded player data for world ${event.world.name} (${event.world.uid})")
            } catch (e: Throwable) {
                log.atWarning()
                    .withCause(e)
                    .log("Failed to load data for world ${event.world.name} (${event.world.uid})")
            }
        }
    }

    @EventHandler
    fun onWorldUnload(event: WorldUnloadEvent) {
        if (!isWorldScope) return
        plugin.launch {
            try {
                PlayerDataStorage.destroyCache(event.world.uid)
                println("Unloaded player data for world ${event.world.name} (${event.world.uid})")
            } catch (e: Throwable) {
                log.atWarning()
                    .withCause(e)
                    .log("Failed to unload data for world ${event.world.name} (${event.world.uid})")
            }
        }
    }
}
