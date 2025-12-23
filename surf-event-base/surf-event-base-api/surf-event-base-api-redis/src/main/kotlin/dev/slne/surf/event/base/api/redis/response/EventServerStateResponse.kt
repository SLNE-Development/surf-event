package dev.slne.surf.event.base.api.redis.response

import dev.slne.surf.event.base.api.common.state.EventServerState
import dev.slne.surf.redis.request.RedisResponse
import kotlinx.serialization.Serializable

@Serializable
data class EventServerStateResponse(
    val state: EventServerState
) : RedisResponse()
