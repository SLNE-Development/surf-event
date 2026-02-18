package dev.slne.surf.event.base.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.PluginManager
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.event.base.core.loader.redisLoader
import dev.slne.surf.event.base.velocity.command.eventServerStateCommand
import org.slf4j.Logger
import java.nio.file.Path

class VelocityMain @Inject constructor(
    val proxy: ProxyServer,
    val pluginManager: PluginManager,
    val eventManager: EventManager,
    @param:DataDirectory val dataPath: Path,
    val pluginContainer: PluginContainer,
    val logger: Logger,
    suspendingPluginContainer: SuspendingPluginContainer
) {
    init {
        suspendingPluginContainer.initialize(this)
        instance = this
    }

    @Subscribe
    fun onProxyInitialize(event: ProxyInitializeEvent) {
        eventServerStateCommand()

        redisLoader.connect()
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        redisLoader.disconnect()
    }

    companion object {
        lateinit var instance: VelocityMain
    }
}

val proxy get() = VelocityMain.instance.proxy
val plugin get() = VelocityMain.instance