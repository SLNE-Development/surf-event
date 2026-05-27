package dev.slne.surf.event.anarchy.spectating

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent

object SpectatingListener : Listener {
    @EventHandler
    fun onGameMode(event: PlayerGameModeChangeEvent) {
        val player = event.player
        val newGamemode = event.newGameMode
        val playableGamemodes = listOf(GameMode.SURVIVAL, GameMode.ADVENTURE)

        if(!SpectatingManager.isSpectator(player)) {
            return
        }

        if (newGamemode in playableGamemodes) {
            player.sendText {
                appendAnarchyPrefix()
                error("Du bist ein Zuschauer! Bitte greife nicht in das Spielgeschehen ein.")
            }
        }
    }
}