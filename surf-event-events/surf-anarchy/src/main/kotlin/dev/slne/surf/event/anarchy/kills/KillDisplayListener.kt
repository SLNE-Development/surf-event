package dev.slne.surf.event.anarchy.kills

import com.destroystokyo.paper.profile.PlayerProfile
import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.event.anarchy.spectating.SpectatingManager
import org.bukkit.BanEntry
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.time.Duration

object KillDisplayListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        KillDisplayManager.updateDisplayForNewPlayer(event.player)
        KillDisplayManager.updateNewPlayerForAll(event.player)
    }

    @EventHandler
    fun onKill(event: PlayerDeathEvent) {
        val player = event.player
        val killer = player.killer

        val playerKills = KillDisplayManager.getKills(player)
        val duration: Duration? = null

        if (SpectatingManager.isAvailableSpectator(player) && !SpectatingManager.isSpectator(player)) {
            SpectatingManager.setSpectator(player, true)
        }

        if (!SpectatingManager.isSpectator(player)) {
            player.ban<BanEntry<PlayerProfile>>(
                "Du bist ausgeschieden. Danke fürs Spielen!",
                duration,
                "Anarchy Event 2026",
                false
            )

            player.kick(
                CommonComponents.renderDisconnectMessage(
                    SurfComponentBuilder(),
                    "DU BIST GESTORBEN!",
                    {
                        if (killer == null) {
                            spacer("Du bist an ")
                            translatable(event.damageSource.damageType.translationKey).color(Colors.VARIABLE_VALUE)
                            spacer(" gestorben.")
                        } else {
                            spacer("Du wurdest von ")
                            variableValue(killer.name)
                            spacer(" getötet.")
                        }

                        appendNewline()

                        if (playerKills < 1) {
                            spacer("Du hattest noch keine Kills.")
                        } else if (playerKills == 1) {
                            spacer("Du hast ")
                            variableValue("einen")
                            spacer(" Spieler getötet.")
                        } else {
                            spacer("Du hast ")
                            variableValue(playerKills)
                            spacer(" Spieler getötet.")
                        }
                    },
                    {
                        primary("Danke fürs Spielen!")
                    }
                ))
        }

        player.location.world.strikeLightningEffect(player.location)
        player.location.world.playSound(player.location, Sound.ENTITY_WITHER_SPAWN, 0.2f, 1.4f)

        if (killer == null) {
            return
        }

        KillDisplayManager.incrementKills(killer)
        KillDisplayManager.updateDisplay(killer)
    }
}