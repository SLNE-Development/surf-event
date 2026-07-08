package dev.slne.surf.event.anarchy.finale.listener

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.event.anarchy.finale.FinaleLifecycle
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause

object FinaleDimensionListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onDimensionTravel(event: PlayerTeleportEvent) {
        if (!FinaleLifecycle.isRunning()) {
            return
        }

        if (event.player.world.environment == World.Environment.NORMAL) {
            return
        }

        if (event.cause != TeleportCause.NETHER_PORTAL && event.cause != TeleportCause.END_PORTAL) {
            return
        }

        event.isCancelled = true
        event.player.sendText {
            appendAnarchyPrefix()
            error("Der Nether und das End sind während des Finales geschlossen!")
        }
    }
}
