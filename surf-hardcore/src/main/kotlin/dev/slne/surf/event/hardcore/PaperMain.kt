package dev.slne.surf.event.hardcore

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.surfapi.bukkit.api.event.register
import org.bukkit.plugin.java.JavaPlugin

class PaperMain : SuspendingJavaPlugin() {

    override fun onEnable() {
        HardcoreListener.register()
    }
}

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)