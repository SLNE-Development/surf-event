package dev.slne.surf.event.mythicmobs.listener

import dev.slne.surf.api.paper.event.register
import dev.slne.surf.event.mythicmobs.listener.mob.MythicMobSpawnListener

object ListenerManager {
    fun register() {
        MythicMobSpawnListener.register()
    }
}