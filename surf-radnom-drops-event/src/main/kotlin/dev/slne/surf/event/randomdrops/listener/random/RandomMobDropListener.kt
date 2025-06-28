package dev.slne.surf.event.randomdrops.listener.random

import dev.slne.surf.event.randomdrops.service.PlayerDropService
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

object RandomMobDropListener : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val killer = event.damageSource.causingEntity as? Player ?: return
        val entity = event.entity
        with(event.drops) {
            clear()
            addAll(PlayerDropService.getReplacedMobDrops(killer, entity))
        }
    }
}