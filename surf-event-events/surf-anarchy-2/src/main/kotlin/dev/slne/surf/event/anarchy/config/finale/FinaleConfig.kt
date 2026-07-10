package dev.slne.surf.event.anarchy.config.finale

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.time.ZonedDateTime

@ConfigSerializable
data class FinaleConfig(
    var start: ZonedDateTime? = null,
    var finaleDurationMinutes: Int? = null,
    var startedAt: ZonedDateTime? = null,
)
