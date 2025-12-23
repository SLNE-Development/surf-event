package dev.slne.surf.event.base.paper.redis.listener

import dev.slne.surf.event.base.api.redis.request.EventServerStateRequest
import dev.slne.surf.event.base.api.redis.response.EventServerStateResponse
import dev.slne.surf.event.base.paper.manager.eventServerManager
import dev.slne.surf.redis.request.HandleRedisRequest
import dev.slne.surf.redis.request.RequestContext

object EventServerStateRequestListener {
    @HandleRedisRequest
    fun handleEventServerStateRequest(context: RequestContext<EventServerStateRequest>) {
        context.respond(EventServerStateResponse(eventServerManager.state))
    }
}