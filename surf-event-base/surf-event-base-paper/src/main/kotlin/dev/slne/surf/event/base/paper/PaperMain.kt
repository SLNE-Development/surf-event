package dev.slne.surf.event.base.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.event.base.paper.command.eventServerStateChangeCommand
import dev.slne.surf.event.base.paper.config.EventServerConfigHolder
import dev.slne.surf.event.base.paper.manager.eventServerManager
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        redisLoader.connect()

        eventServerStateChangeCommand()
    }

    override fun onDisable() {
        redisLoader.disconnect()

        eventServerConfigHolder.edit {
            state = eventServerManager.state
        }
    }
}

val eventServerConfigHolder = EventServerConfigHolder()
val eventServerConfig get() = eventServerConfigHolder.config