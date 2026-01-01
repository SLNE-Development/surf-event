package dev.slne.surf.event.base.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.event.base.api.common.state.EventServerState
import dev.slne.surf.event.base.paper.command.eventServerStateChangeCommand
import dev.slne.surf.event.base.paper.config.EventServerConfigHolder
import dev.slne.surf.event.base.paper.manager.eventServerManager
import kotlinx.coroutines.delay
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        redisLoader.connect()
        eventServerManager.load()
        eventServerManager.updateTask()
        eventServerStateChangeCommand()
    }

    override fun onDisable() {
        val previousState = eventServerManager.state.get()
        eventServerManager.state.set(EventServerState.UNKNOWN)
        eventServerConfigHolder.edit {
            state = previousState
        }
    }

    override suspend fun onDisableAsync() {
        delay(10L)
        redisLoader.disconnect()
    }
}

val eventServerConfigHolder = EventServerConfigHolder()
val eventServerConfig get() = eventServerConfigHolder.config