package dev.slne.surf.event.base.paper.settings

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.util.forEachPlayer
import dev.slne.surf.event.base.paper.eventServerConfig
import dev.slne.surf.event.base.paper.permission.PermissionRegistry
import dev.slne.surf.event.base.paper.plugin
import org.bukkit.entity.Player

object PlayerVisibilityService {
    fun refreshState(player: Player) {
        if (!eventServerConfig.playerVisibilityEnabled) {
            return
        }

        if (!plugin.hasSettingsHook()) {
            player.sendText {
                appendErrorPrefix()
                error("Ein Interner Fehler ist aufgetreten. Bitte kontaktiere den Support.")
            }
            return
        }

        val selectedState = settingsHook.getSelectedState(player.uniqueId)

        forEachPlayer {
            if (it.uniqueId == player.uniqueId) return@forEachPlayer

            when (selectedState) {
                PlayerVisibilityState.ALL -> showPlayer(player, it)
                PlayerVisibilityState.NONE -> hidePlayer(player, it)
                PlayerVisibilityState.VIP -> {
                    if (it.hasPermission(PermissionRegistry.VISIBILITY_VIP)) {
                        showPlayer(player, it)
                    } else {
                        hidePlayer(player, it)
                    }
                }

                PlayerVisibilityState.FRIENDS -> showPlayer(player, it)
            }
        }
    }

    fun handleJoin(player: Player) {
        if (!eventServerConfig.playerVisibilityEnabled) {
            return
        }

        if (!plugin.hasSettingsHook()) {
            return
        }

        forEachPlayer {
            if (it.uniqueId == player.uniqueId) return@forEachPlayer

            val otherPlayerState = settingsHook.getSelectedState(it.uniqueId)

            when (otherPlayerState) {
                PlayerVisibilityState.ALL -> showPlayer(it, player)
                PlayerVisibilityState.NONE -> hidePlayer(it, player)
                PlayerVisibilityState.VIP -> {
                    if (player.hasPermission(PermissionRegistry.VISIBILITY_VIP)) {
                        showPlayer(it, player)
                    } else {
                        hidePlayer(it, player)
                    }
                }

                PlayerVisibilityState.FRIENDS -> showPlayer(it, player)
            }
        }
    }

    private fun showPlayer(player: Player, target: Player) {
        if (player.canSee(target)) {
            return
        }

        player.showPlayer(plugin, target)
    }

    private fun hidePlayer(player: Player, target: Player) {
        if (!player.canSee(target)) {
            return
        }

        player.hidePlayer(plugin, target)
    }
}
