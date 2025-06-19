package dev.slne.surf.event.hardcore

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.event.hardcore.message.MessageManager
import dev.slne.surf.event.hardcore.sound.SoundManager
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.bukkit.api.util.dispatcher
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.block.data.type.Bed
import org.bukkit.block.data.type.RespawnAnchor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerTeleportEvent

object HardcoreListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.player
        val location = player.location

        plugin.launch(player.dispatcher()) {
            try {
                launch(location.dispatcher()) {
                    location.world.strikeLightningEffect(location)
                }

                SoundManager.broadcastDeathSound()
                MessageManager.broadcastDeathMessage(event.deathMessage(), location)
            } finally {
                plugin.tryBanPlayer(player)
            }
        }
    }

    @EventHandler
    fun onPlayerBucketEmpty(event: PlayerBucketEmptyEvent) {
        if (EndManager.isEnd) return
        if (event.bucket != Material.LAVA_BUCKET) return
        val player = event.player
        val otherPlayers = player.location.getNearbyPlayers(10.0) { it != player }
        if (otherPlayers.isEmpty()) return
        event.isCancelled = true
        player.sendMessage(MessageManager.nearbyPlayersWhenEmptyingLavaBucket)
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        if (!EndManager.isEnd) return
        if (event.cause == PlayerTeleportEvent.TeleportCause.END_PORTAL || event.cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            event.isCancelled = true
            event.player.sendMessage(MessageManager.cannotChangeDimensionDuringFinalEvent)
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (EndManager.isEnd) return
        val item = event.item ?: return

        if (item.type == Material.END_CRYSTAL) {
            event.isCancelled = true
            event.player.sendMessage(MessageManager.cannotPlaceEndCrystalBeforeEnd)
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (EndManager.isEnd) return
        val block = event.blockPlaced.blockData
        val player = event.player

        when {
            block is Bed && player.world != server.worlds.first() -> {
                event.isCancelled = true
                player.sendMessage(MessageManager.cannotPlaceBedsInOtherWorldsBeforeEnd)
            }

            block is RespawnAnchor -> {
                event.isCancelled = true
                player.sendMessage(MessageManager.cannotPlaceRespawnAnchorBeforeEnd)
            }
        }
    }
}