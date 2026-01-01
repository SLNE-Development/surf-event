package dev.slne.surf.event.randomdrops.listener

import dev.slne.surf.event.randomdrops.listener.random.RandomBlockDropListener
import dev.slne.surf.event.randomdrops.listener.random.RandomLootTable
import dev.slne.surf.event.randomdrops.listener.random.RandomMobDropListener
import dev.slne.surf.event.randomdrops.listener.save.PlayerDataListener
import dev.slne.surf.event.randomdrops.listener.save.WorldDataListener
import dev.slne.surf.surfapi.bukkit.api.event.register

object ListenerManager {
    fun register() {
        RandomBlockDropListener.register()
        RandomMobDropListener.register()
        PlayerDataListener.register()
        WorldDataListener.register()
        RandomLootTable.register()
    }
}