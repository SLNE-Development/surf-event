package dev.slne.surf.event.oneblock.listener

import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.global.GlobalGoals
import dev.slne.surf.event.oneblock.island.IslandManager
import dev.slne.surf.event.oneblock.overworld
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldSaveEvent

object OneBlockSaveListener : Listener {
    @EventHandler
    fun onWorldSave(event: WorldSaveEvent) {
        if (event.world != overworld) return
        IslandService.flushAll()
        GlobalGoals.flush()
        IslandManager.saveIdx()
    }

}