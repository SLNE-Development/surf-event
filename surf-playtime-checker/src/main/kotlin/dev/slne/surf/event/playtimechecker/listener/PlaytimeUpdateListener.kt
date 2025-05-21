package dev.slne.surf.event.playtimechecker.listener

import dev.slne.surf.event.playtimechecker.config.PlaytimeCheckerConfigManager
import dev.slne.surf.event.playtimechecker.plugin
import dev.slne.surf.proxy.api.event.SurfProxyEventListener
import dev.slne.surf.proxy.api.event.events.SurfProxyPrePlaytimeUpdateEvent
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import kotlin.jvm.optionals.getOrNull

object PlaytimeUpdateListener : SurfProxyEventListener<SurfProxyPrePlaytimeUpdateEvent> {
    override fun onEvent(event: SurfProxyPrePlaytimeUpdateEvent) {
        val player = plugin.proxy.getPlayer(event.user.uuid).getOrNull() ?: return
        val server = player.currentServer.getOrNull() ?: return
        val serverName = server.serverInfo.name
        val serverConfig = PlaytimeCheckerConfigManager.getPlaytimeCheck(serverName) ?: return
        val playtime = event.newPlaytime.playtime
        if (playtime > serverConfig.maxPlaytime) {
            player.disconnect(buildText {
                appendKickDisconnectMessage({
                    variableValue("Du hast die maximale Spielzeit auf")
                    appendNewline()
                    variableValue("diesem Server erreicht.")
                })
            })
        }
    }
}