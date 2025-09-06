package dev.slne.surf.event.oneblock.progress

import dev.slne.surf.event.oneblock.plugin
import org.bukkit.entity.Player

object ProgressService {
    fun requiredForNext(level: Int): Long {
        val base = plugin.config.getInt("progression.base-required")
        val mult = plugin.config.getInt("progression.multiplier")
        return (base + (level * mult)).toLong()
    }

    fun onBlockMined(player: Player) {
        val isl = repo.incrementMined(player.uniqueId) ?: return
        val need = requiredForNext(isl.level)
        if (isl.totalMined >= need) {
            repo.setLevel(player.uniqueId, isl.level + 1)
            plugin.server.scheduler.runTask(plugin) {
                player.sendMessage("§aLevel Up! §7Jetzt Level §e${isl.level + 1}§7.")
            }
        }
        plugin.globalGoals.bump(1)
    }

}