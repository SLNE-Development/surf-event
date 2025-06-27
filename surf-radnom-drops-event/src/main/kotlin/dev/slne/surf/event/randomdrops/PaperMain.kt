package dev.slne.surf.event.randomdrops

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import org.bukkit.plugin.java.JavaPlugin

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onEnableAsync() {
    }

    override suspend fun onDisableAsync() {
    }
}

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)