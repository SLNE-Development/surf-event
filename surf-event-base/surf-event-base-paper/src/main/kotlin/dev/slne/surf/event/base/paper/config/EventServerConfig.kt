package dev.slne.surf.event.base.paper.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class EventServerConfig(
    var playerVisibilityEnabled: Boolean = true
)
