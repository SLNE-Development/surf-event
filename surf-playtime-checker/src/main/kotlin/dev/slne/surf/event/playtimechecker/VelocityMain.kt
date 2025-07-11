package dev.slne.surf.event.playtimechecker

import com.google.inject.Inject
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.event.playtimechecker.command.playtimeCheckerCommand
import dev.slne.surf.event.playtimechecker.listener.PlayerPlaytimeListener
import dev.slne.surf.proxy.api.event.SurfProxyEventManager
import java.nio.file.Path

lateinit var plugin: VelocityMain
    private set

class VelocityMain @Inject constructor(@DataDirectory val datapath: Path, val proxy: ProxyServer) {
    init {
        plugin = this
    }

    @Subscribe(order = PostOrder.LATE)
    fun onProxyInitialize(unused: ProxyInitializeEvent) {
        proxy.eventManager.register(this, PlayerPlaytimeListener)
        SurfProxyEventManager.get().registerListener(PlayerPlaytimeListener)
        playtimeCheckerCommand()
    }
}