package dev.slne.surf.event.playtimechecker.listener

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import dev.slne.surf.event.playtimechecker.config.PlaytimeCheckerConfigManager
import dev.slne.surf.event.playtimechecker.plugin
import dev.slne.surf.proxy.api.ProxyApi
import dev.slne.surf.proxy.api.event.SurfProxyEventListener
import dev.slne.surf.proxy.api.event.events.SurfProxyPrePlaytimeUpdateEvent
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import java.util.*
import kotlin.jvm.optionals.getOrNull

object PlayerPlaytimeListener : SurfProxyEventListener<SurfProxyPrePlaytimeUpdateEvent> {

    private const val BYPASS_PERMISSION = "surf.event.playtimechecker.bypass"

    private val kickMessage = buildText {
        appendKickDisconnectMessage({
            variableValue("Du hast nicht genügend Spielzeit, um diesen Server zu betreten.")
        })
    }

    private val chatMessage = buildText {
        appendPrefix()
        error("Du hast nicht genügend Spielzeit, um diesen Server zu betreten.")
    }

    override fun onEvent(event: SurfProxyPrePlaytimeUpdateEvent) {
        val player = plugin.proxy.getPlayer(event.user.uuid).getOrNull() ?: return
        val serverName = player.currentServer.getOrNull()?.serverInfo?.name ?: return

        if (player.hasPermission(BYPASS_PERMISSION)) return

        if (!serverName.isAllowed(event.newPlaytime.playtime)) {
            player.disconnect(kickMessage)
        }
    }

    @Subscribe
    fun onServerPreConnect(event: ServerPreConnectEvent) = with(event) {
        if (player.hasPermission(BYPASS_PERMISSION)) return

        val serverName = result.server.getOrNull()?.serverInfo?.name ?: return
        if (!serverName.isAllowed(player.uniqueId)) {
            result = ServerPreConnectEvent.ServerResult.denied()
            player.sendMessage(chatMessage)
        }
    }

    @Subscribe(order = PostOrder.LAST)
    fun onPlayerChooseInitialServer(event: PlayerChooseInitialServerEvent) = with(event) {
        if (player.hasPermission(BYPASS_PERMISSION)) return

        val serverName = initialServer.getOrNull()?.serverInfo?.name ?: return
        if (!serverName.isAllowed(player.uniqueId)) {
            player.disconnect(kickMessage)
        }
    }

    private fun String.isAllowed(uuid: UUID): Boolean {
        val check = PlaytimeCheckerConfigManager.getPlaytimeCheck(this) ?: return true
        val playtime = ProxyApi.getUser(uuid).getOrNull()
            ?.getPlaytime(check.category, check.server)
            ?.getOrNull()
            ?.playtime
            ?: return true
        return playtime > check.maxPlaytime
    }

    private fun String.isAllowed(playtime: Long): Boolean {
        val max = PlaytimeCheckerConfigManager.getPlaytimeCheck(this)?.maxPlaytime ?: return true
        return playtime > max
    }
}