package dev.slne.surf.event.base.paper.config

import dev.slne.surf.event.base.api.common.state.EventServerState
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class EventServerConfig(
    var state: EventServerState = EventServerState.CLOSED
)
