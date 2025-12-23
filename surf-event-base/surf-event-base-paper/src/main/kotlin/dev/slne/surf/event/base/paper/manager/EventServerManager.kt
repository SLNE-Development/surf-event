package dev.slne.surf.event.base.paper.manager

import dev.slne.surf.event.base.paper.eventServerConfig

val eventServerManager = EventServerManager()

class EventServerManager {
    var state = eventServerConfig.state
}