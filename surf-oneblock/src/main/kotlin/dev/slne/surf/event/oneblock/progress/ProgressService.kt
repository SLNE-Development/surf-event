package dev.slne.surf.event.oneblock.progress

import dev.slne.surf.event.oneblock.config.config
import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.global.GlobalGoals
import org.bukkit.entity.Player

object ProgressService {
    fun requiredForNext(level: Int): Long {
        val base = config.progression.baseRequired
        val mult = config.progression.multiplier
        return (base + (level * mult)).toLong()
    }

    fun onBlockMined(player: Player) {
        val island = IslandService.incrementMined(player.uniqueId) ?: return
        val needed = requiredForNext(island.level)

        if (island.totalMined >= needed) {
            IslandService.setLevel(player.uniqueId, island.level + 1)
            player.sendMessage("§aLevel Up! §7Jetzt Level §e${island.level + 1}§7.")
        }

        GlobalGoals.onBlockMined()
    }

}