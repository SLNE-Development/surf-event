package dev.slne.surf.event.base.paper.manager

import dev.slne.surf.event.base.api.common.state.EventServerState
import dev.slne.surf.event.base.paper.eventServerConfig
import dev.slne.surf.event.base.paper.plugin
import dev.slne.surf.event.base.paper.redisApi
import dev.slne.surf.redis.sync.value.SyncValue
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit

val eventServerManager = EventServerManager()

class EventServerManager {
    lateinit var state: SyncValue<EventServerState>
    lateinit var currentPlayers: SyncValue<Int>
    lateinit var maxPlayers: SyncValue<Int>

    fun init() {
        state = redisApi.createSyncValue("surf-event:event-server-state", EventServerState.UNKNOWN)
        currentPlayers = redisApi.createSyncValue("surf-event:event-server-current-players", 0)
        maxPlayers = redisApi.createSyncValue("surf-event:event-server-max-players", 0)
    }

    fun updateTask() {
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
            currentPlayers.set(Bukkit.getOnlinePlayers().size)
            maxPlayers.set(Bukkit.getMaxPlayers())
        }, 0L, 5L, TimeUnit.SECONDS)
    }

    fun load() {
        state.set(eventServerConfig.state)
        currentPlayers.set(Bukkit.getOnlinePlayers().size)
        maxPlayers.set(Bukkit.getMaxPlayers())
    }
}