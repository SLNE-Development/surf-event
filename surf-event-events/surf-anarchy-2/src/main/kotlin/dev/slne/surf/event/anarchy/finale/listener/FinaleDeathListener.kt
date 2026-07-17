package dev.slne.surf.event.anarchy.finale.listener

import com.github.shynixn.mccoroutine.folia.launch
import com.google.common.io.ByteStreams
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.api.paper.util.BukkitSound
import dev.slne.surf.event.anarchy.finale.FinaleLifecycle
import dev.slne.surf.event.anarchy.plugin
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import kotlin.time.Duration.Companion.seconds

object FinaleDeathListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.player
        val killer = player.killer

        if (!FinaleLifecycle.isRunning()) {
            return
        }

        val playersKills = player.getStatistic(Statistic.PLAYER_KILLS)
        val playersDeaths = player.getStatistic(Statistic.DEATHS)

        val reason = CommonComponents.renderDisconnectMessage(
            SurfComponentBuilder(),
            "DU BIST GESTORBEN!",
            {
                if (killer == null) {
                    spacer("Du bist gestorben.")
                } else {
                    spacer("Du wurdest von ")
                    variableValue(killer.name)
                    spacer(" getötet.")
                }

                appendNewline()
                appendNewline()

                if (playersDeaths > 0) {
                    info("Du bist ")
                    success(playersDeaths.toString())
                    info(" mal gestorben.")
                } else {
                    info("Du bist nicht gestorben.")
                }

                appendNewline()

                if (playersKills > 0) {
                    info("Du hast ")
                    success(playersKills.toString())
                    info(" Spieler getötet.")
                } else {
                    info("Du hast keine Spieler getötet.")
                }
            },
            {
                primary("Danke für deine Teilnahme.")
            }
        )

        kickPlayerGlobally(player, reason)

        plugin.launch {
            delay(1.seconds)

            if (player.isOnline) {
                player.kick(reason)
                plugin.logger.warning("Proxy kick failed on player ${player.name} (${player.uniqueId}), kicked them locally instead.")
            }
        }

        player.location.world.strikeLightningEffect(player.location)
        player.location.world.playSound(player.location, BukkitSound.ENTITY_WITHER_SPAWN, 0.2f, 1f)
    }


    private fun kickPlayerGlobally(player: Player, reason: Component) {
        player.sendPluginMessage(plugin, "BungeeCord", ByteStreams.newDataOutput().apply {
            this.writeUTF("KickPlayerRaw")
            this.writeUTF(player.name)
            this.writeUTF(GsonComponentSerializer.gson().serialize(reason))
        }.toByteArray())
    }
}