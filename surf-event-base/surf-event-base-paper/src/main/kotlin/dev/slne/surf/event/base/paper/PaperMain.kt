package dev.slne.surf.event.base.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.api.paper.extensions.pluginManager
import dev.slne.surf.api.paper.inventory.framework.viewFrame
import dev.slne.surf.event.base.core.loader.redisLoader
import dev.slne.surf.event.base.paper.command.eventServerCommand
import dev.slne.surf.event.base.paper.config.EventServerConfigHolder
import dev.slne.surf.event.base.paper.settings.command.playerVisibilityCommand
import dev.slne.surf.event.base.paper.settings.listener.PlayerConnectionListener
import dev.slne.surf.event.base.paper.settings.menu.playerVisibilityView
import dev.slne.surf.event.base.paper.settings.settingsHook
import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        viewFrame.with(playerVisibilityView())
    }

    override fun onEnable() {
        redisLoader.connect()

        eventServerCommand()
        playerVisibilityCommand()

        PlayerConnectionListener.register()

        if (hasSettingsHook()) {
            runBlocking {
                settingsHook.createSettings()
            }
        }
    }

    override suspend fun onDisableAsync() {
        redisLoader.disconnect()
    }

    fun hasSettingsHook() = pluginManager.isPluginEnabled("surf-settings-paper")
}

val eventServerConfigHolder = EventServerConfigHolder()
val eventServerConfig get() = eventServerConfigHolder.config