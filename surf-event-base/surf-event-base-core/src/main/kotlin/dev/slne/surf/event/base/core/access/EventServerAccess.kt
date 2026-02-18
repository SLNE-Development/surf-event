package dev.slne.surf.event.base.core.access

import dev.slne.surf.event.base.api.common.state.EventServerState
import dev.slne.surf.event.base.core.loader.redisLoader

val eventServerAccess = EventServerAccess()

class EventServerAccess {
    fun getEventServerState(): EventServerState = redisLoader.eventServerState.get()
    fun setEventServerState(state: EventServerState) = redisLoader.eventServerState.set(state)
}