package dev.slne.surf.event.randomdrops.config

import java.util.UUID

val GLOBAL_UUID: UUID = UUID(0L, 0L)
val isGlobalScope: Boolean get() = config.randomizationScope == RandomizationScope.GLOBAL
fun effectiveUuid(uuid: UUID): UUID = if (isGlobalScope) GLOBAL_UUID else uuid
