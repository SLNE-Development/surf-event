package dev.slne.surf.event.playtimechecker.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import dev.slne.surf.event.playtimechecker.config.PlaytimeCheckerConfigManager
import dev.slne.surf.proxy.api.ProxyApi
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlin.jvm.optionals.getOrNull

object PlayerConnectToServerListener {

    @Subscribe
    fun onServerPreConnect(event: ServerPreConnectEvent) {
        val serverName = event.result.server.getOrNull()?.serverInfo?.name ?: return
        val playtimeCheck = PlaytimeCheckerConfigManager.getPlaytimeCheck(serverName) ?: return
        val user = ProxyApi.getUser(event.player.uniqueId).getOrNull() ?: return
        val playtime =
            user.getPlaytime(playtimeCheck.category, playtimeCheck.server).getOrNull() ?: return
        if (playtime.playtime > playtimeCheck.maxPlaytime) {
            event.result = ServerPreConnectEvent.ServerResult.denied()
            event.player.sendText {
                appendPrefix()
                error("Du hast die maximale Spielzeit auf diesem Server erreicht.")
            }
        }
    }
}