package dev.slne.surf.event.anarchy.finale.listener

import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.api.paper.util.BukkitSound
import dev.slne.surf.event.anarchy.finale.FinaleLifecycle
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

object FinaleDeathListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.player
        val killer = player.killer

        if (!FinaleLifecycle.isRunning()) {
            return
        }

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
                },
                {
                    primary("Danke fürs Spielen!")
                }
            ))

        player.location.world.strikeLightningEffect(player.location)
        player.location.world.playSound(player.location, BukkitSound.ENTITY_WITHER_SPAWN, 0.2f, 1f)
    }
}