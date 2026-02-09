package dev.slne.surf.event.base.paper.settings

import dev.slne.surf.event.base.paper.plugin
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

val playerVisibilityService = PlayerVisibilityService()

class PlayerVisibilityService {
    fun refreshState(player: Player) {
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
                PlayerVisibilityState.ALL -> {
                    player.showPlayer(plugin, it)
                }

                PlayerVisibilityState.NONE -> {
                    player.hidePlayer(plugin, it)
                }

                PlayerVisibilityState.VIP -> {
                    if (it.hasPermission("surf.event.base.vip")) {
                        player.showPlayer(plugin, it)
                    } else {
                        player.hidePlayer(plugin, it)
                    }
                }

                PlayerVisibilityState.FRIENDS -> {
                    player.showPlayer(plugin, it)
                }
            }
        }
    }

    fun handleJoin(player: Player) {
        if (!plugin.hasSettingsHook()) {
            return
        }

        forEachPlayer {
            if (it.uniqueId == player.uniqueId) {
                return@forEachPlayer
            }

            val otherPlayerState = settingsHook.getSelectedState(it.uniqueId)

            when (otherPlayerState) {
                PlayerVisibilityState.ALL -> {
                    it.showPlayer(plugin, player)
                }

                PlayerVisibilityState.NONE -> {
                    it.hidePlayer(plugin, player)
                }

                PlayerVisibilityState.VIP -> {
                    if (player.hasPermission("surf.event.base.vip")) {
                        it.showPlayer(plugin, player)
                    } else {
                        it.hidePlayer(plugin, player)
                    }
                }

                PlayerVisibilityState.FRIENDS -> {
                    it.showPlayer(plugin, player)
                }
            }
        }
    }
}