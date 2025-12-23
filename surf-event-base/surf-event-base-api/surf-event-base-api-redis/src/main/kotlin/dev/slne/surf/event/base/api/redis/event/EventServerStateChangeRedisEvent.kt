package dev.slne.surf.event.base.api.redis.event

import dev.slne.surf.event.base.api.common.state.EventServerState
import dev.slne.surf.redis.event.RedisEvent
import kotlinx.serialization.Serializable

@Serializable
data class EventServerStateChangeRedisEvent(
    val from: EventServerState,
    val to: EventServerState
) : RedisEvent()