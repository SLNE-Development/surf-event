package dev.slne.surf.event.anarchy.finale.listener

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.event.anarchy.finale.FinaleLifecycle
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import org.bukkit.GameRule
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import kotlin.random.Random

object FinaleDimensionListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onTeleport(event: PlayerTeleportEvent) = handle(event)

    @EventHandler(ignoreCancelled = true)
    fun onPortal(event: PlayerPortalEvent) = handle(event)

    @EventHandler
    fun onEndReturn(event: PlayerRespawnEvent) {
        if (!FinaleLifecycle.isRunning() || !FinaleLifecycle.isOverworldLocked()) {
            return
        }

        if (event.respawnReason != PlayerRespawnEvent.RespawnReason.END_PORTAL) {
            return
        }

        val end = event.player.world.takeIf { it.environment == World.Environment.THE_END }
            ?: server.worlds.firstOrNull { it.environment == World.Environment.THE_END }
            ?: return

        event.respawnLocation = randomEndSpawn(end)
        event.player.sendText {
            appendAnarchyPrefix()
            error("Das Portal funktioniert nicht.")
        }
    }

    private fun handle(event: PlayerTeleportEvent) {
        if (!FinaleLifecycle.isRunning()) {
            return
        }

        when (event.cause) {
            TeleportCause.NETHER_PORTAL -> {
                if (event.player.world.environment != World.Environment.NORMAL) {
                    return
                }

                event.isCancelled = true
                event.player.sendText {
                    appendAnarchyPrefix()
                    error("Der Nether ist während des Finales geschlossen!")
                }
            }

            TeleportCause.END_PORTAL -> {
                val target = event.to ?: return

                when (target.world?.environment) {
                    World.Environment.THE_END -> event.to = randomEndSpawn(target.world!!)

                    World.Environment.NORMAL -> if (FinaleLifecycle.isOverworldLocked()) {
                        event.isCancelled = true
                        event.player.sendText {
                            appendAnarchyPrefix()
                            error("Das Portal funktioniert nicht.")
                        }
                    }

                    else -> {}
                }
            }

            TeleportCause.END_GATEWAY -> if (FinaleLifecycle.isOverworldLocked()) {
                event.isCancelled = true
                event.player.sendText {
                    appendAnarchyPrefix()
                    error("Die äußeren End-Portale sind blockiert.")
                }
            }

            else -> {}
        }
    }

    @Suppress("removal")
    private fun randomEndSpawn(end: World): Location {
        val spawn = end.spawnLocation
        val radius = end.getGameRuleValue(GameRule.SPAWN_RADIUS).coerceAtLeast(0)

        if (radius == 0) {
            return spawn.clone()
        }

        val x = spawn.blockX + Random.nextInt(-radius, radius + 1) + 0.5
        val z = spawn.blockZ + Random.nextInt(-radius, radius + 1) + 0.5

        return Location(end, x, spawn.y, z, spawn.yaw, spawn.pitch)
    }
}
