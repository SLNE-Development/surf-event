package dev.slne.surf.event.hardcore

import com.destroystokyo.paper.profile.PlayerProfile
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.event.hardcore.message.MessageManager
import dev.slne.surf.event.hardcore.sound.SoundManager
import dev.slne.surf.surfapi.bukkit.api.util.dispatcher
import kotlinx.coroutines.launch
import org.bukkit.BanEntry
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import java.time.Duration

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
                if (!player.hasPermission(HardcorePermissions.HARDCORE_BYPASS)) {
                    player.ban<BanEntry<PlayerProfile>>(
                        "Game Over!",
                        null as? Duration,
                        PaperMain.HARDCORE_BAN_SOURCE,
                        true
                    )
                }
            }
        }
    }
}