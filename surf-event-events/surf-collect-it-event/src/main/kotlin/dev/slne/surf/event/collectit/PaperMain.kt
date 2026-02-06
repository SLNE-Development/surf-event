package dev.slne.surf.event.collectit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import de.oliver.fancyholograms.api.FancyHologramsPlugin
import de.oliver.fancyholograms.api.HologramManager
import org.bukkit.plugin.java.JavaPlugin

class PaperMain : SuspendingJavaPlugin() {
    val hologramManager: HologramManager
        get() = FancyHologramsPlugin.get().hologramManager

    override suspend fun onLoadAsync() {

    }

    override suspend fun onEnableAsync() {
        CollectItInstance.onEnable()
    }

    override suspend fun onDisableAsync() {
        CollectItInstance.onDisable()
    }
}

val plugin = JavaPlugin.getPlugin(PaperMain::class.java)