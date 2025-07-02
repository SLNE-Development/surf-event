package dev.slne.surf.event.mythicmobs.listener

import dev.slne.surf.event.mythicmobs.listener.mob.MythicMobSpawnListener
import dev.slne.surf.surfapi.bukkit.api.event.register

object ListenerManager {
    fun register() {
        MythicMobSpawnListener.register()
    }
}