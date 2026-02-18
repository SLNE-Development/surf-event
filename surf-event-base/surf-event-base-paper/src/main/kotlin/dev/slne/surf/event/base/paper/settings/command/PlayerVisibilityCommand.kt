package dev.slne.surf.event.base.paper.settings.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.event.base.paper.eventServerConfig
import dev.slne.surf.event.base.paper.permission.PermissionRegistry
import dev.slne.surf.event.base.paper.plugin
import dev.slne.surf.event.base.paper.settings.PlayerVisibilityState
import dev.slne.surf.event.base.paper.settings.command.argument.playerVisibilityState
import dev.slne.surf.event.base.paper.settings.menu.showPlayerVisibilityMenu
import dev.slne.surf.event.base.paper.settings.playerVisibilityService
import dev.slne.surf.event.base.paper.settings.settingsHook
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun playerVisibilityCommand() = commandTree("playervisibility") {
    withPermission(PermissionRegistry.COMMAND_PLAYER_VISIBILITY)
    withAliases("spielersichtbarkeit", "pv", "sv")

    playerExecutor { player, _ ->
        if (!eventServerConfig.playerVisibilityEnabled) {
            player.sendText {
                appendErrorPrefix()
                error("Die Spieler-Sichtbarkeit ist derzeit deaktiviert.")
            }
            return@playerExecutor
        }

        showPlayerVisibilityMenu(player)
    }

    literalArgument("state") {
        playerVisibilityState("state") {
            playerExecutor { player, args ->
                if (!eventServerConfig.playerVisibilityEnabled) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Die Spieler-Sichtbarkeit ist derzeit deaktiviert.")
                    }
                    return@playerExecutor
                }

                if (!plugin.hasSettingsHook()) {
                    player.sendText {
                        appendErrorPrefix() //TODO: Error Code (Surf-Player-Error)
                        error("Ein Interner Fehler ist aufgetreten. Bitte kontaktiere den Support.")
                    }
                    return@playerExecutor
                }

                val state: PlayerVisibilityState by args
                val currentState = settingsHook.getSelectedState(player.uniqueId)

                if (state == currentState) {
                    player.sendText {
                        appendInfoPrefix()
                        info("Deine Sichtbarkeit ist bereits auf ")
                        variableValue(state.displayName)
                        info(" gesetzt.")
                    }
                    return@playerExecutor
                }

                if (state == PlayerVisibilityState.FRIENDS) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Die Sichtbarkeit 'Freunde' ist derzeit nicht verfügbar.")
                    }
                    return@playerExecutor
                }

                plugin.launch {
                    settingsHook.setState(player.uniqueId, state)
                    playerVisibilityService.refreshState(player)

                    player.sendText {
                        appendSuccessPrefix()
                        success("Du hast deine Sichtbarkeit auf ")
                        variableValue(state.displayName)
                        success(" gesetzt.")
                    }
                }
            }
        }
    }

    literalArgument("info") {
        playerExecutor { player, _ ->
            if (!plugin.hasSettingsHook()) {
                player.sendText {
                    appendErrorPrefix() //TODO: Error Code (Surf-Player-Error)
                    error("Ein Interner Fehler ist aufgetreten. Bitte kontaktiere den Support.")
                }
                return@playerExecutor
            }

            val state = settingsHook.getSelectedState(player.uniqueId)

            player.sendText {
                appendInfoPrefix()
                info("Du siehst momentan ")
                variableValue(state.displayName)
                info(".")
            }
        }
    }
}