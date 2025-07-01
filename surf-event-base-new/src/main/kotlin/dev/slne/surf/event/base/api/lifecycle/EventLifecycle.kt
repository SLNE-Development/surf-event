package dev.slne.surf.event.base.api.lifecycle

interface EventLifecycle {
    suspend fun onLoad() = Unit
    suspend fun onEnable() = Unit
    suspend fun onDisable() = Unit
}