package dev.slne.surf.event.oneblock.listener

import dev.slne.surf.event.oneblock.island.IslandManager
import dev.slne.surf.event.oneblock.session.PlayerSession
import dev.slne.surf.surfapi.bukkit.api.event.cancel
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object OneBlockBlockListener : Listener {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val owner = IslandManager.getOwnerFromBlock(event.block) ?: return
        event.cancel()
        if (owner != event.player.uniqueId) {
            event.player.sendMessage("§cYou cannot break blocks on another player's island!")
            return
        }

        PlayerSession[owner].onMine(event.player, event.block)
    }
}