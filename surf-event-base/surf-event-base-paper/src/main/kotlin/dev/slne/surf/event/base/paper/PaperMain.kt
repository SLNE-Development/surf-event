package dev.slne.surf.event.base.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.event.base.api.common.state.EventServerState
import dev.slne.surf.event.base.core.access.eventServerAccess
import dev.slne.surf.event.base.core.loader.redisLoader
import dev.slne.surf.event.base.paper.command.eventServerCommand
import dev.slne.surf.event.base.paper.config.EventServerConfigHolder
import dev.slne.surf.event.base.paper.settings.command.playerVisibilityCommand
import dev.slne.surf.event.base.paper.settings.listener.PlayerConnectionListener
import dev.slne.surf.event.base.paper.settings.settingsHook
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.bukkit.api.extensions.pluginManager
import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
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
        eventServerAccess.setEventServerState(EventServerState.UNKNOWN)
        redisLoader.disconnect()
    }

    fun hasSettingsHook() = pluginManager.isPluginEnabled("surf-settings-paper")
}

val eventServerConfigHolder = EventServerConfigHolder()
val eventServerConfig get() = eventServerConfigHolder.config