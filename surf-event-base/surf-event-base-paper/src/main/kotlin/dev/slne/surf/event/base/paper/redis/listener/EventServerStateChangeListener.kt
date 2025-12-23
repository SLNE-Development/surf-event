package dev.slne.surf.event.base.paper.redis.listener

import dev.slne.surf.event.base.api.redis.event.EventServerStateChangeRedisEvent
import dev.slne.surf.event.base.paper.manager.eventServerManager
import dev.slne.surf.redis.event.OnRedisEvent

object EventServerStateChangeListener {
    @OnRedisEvent
    fun onEventServerStateChange(event: EventServerStateChangeRedisEvent) {
        eventServerManager.state = event.to
    }
}