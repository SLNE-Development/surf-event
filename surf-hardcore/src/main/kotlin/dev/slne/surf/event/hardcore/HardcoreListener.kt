package dev.slne.surf.event.hardcore

import com.destroystokyo.paper.profile.PlayerProfile
import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.surfapi.bukkit.api.util.dispatcher
import kotlinx.coroutines.launch
import org.bukkit.BanEntry
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import java.time.Duration

object HardcoreListener : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.player
        plugin.launch(player.dispatcher()) {
            if (player.hasPermission(HardcorePermissions.HARDCORE_BYPASS)) return@launch
            player.ban<BanEntry<PlayerProfile>>("Game Over!", null as? Duration, "Hardcore", true)

            val location = player.location
            launch(location.dispatcher()) {
                location.world.strikeLightningEffect(location)
            }

            launch(plugin.globalRegionDispatcher) {
                // TODO: play sound
//                server.playSound {
//                }
            }
        }
    }
}