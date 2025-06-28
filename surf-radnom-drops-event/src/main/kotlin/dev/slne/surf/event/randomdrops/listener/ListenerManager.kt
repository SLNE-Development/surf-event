package dev.slne.surf.event.randomdrops.listener

import dev.slne.surf.event.randomdrops.listener.random.RandomBlockDropListener
import dev.slne.surf.event.randomdrops.listener.random.RandomMobDropListener
import dev.slne.surf.event.randomdrops.listener.save.PlayerDataSaveListener
import dev.slne.surf.surfapi.bukkit.api.event.register

object ListenerManager {
    fun register() {
        RandomBlockDropListener.register()
        RandomMobDropListener.register()
        PlayerDataSaveListener.register()
    }
}