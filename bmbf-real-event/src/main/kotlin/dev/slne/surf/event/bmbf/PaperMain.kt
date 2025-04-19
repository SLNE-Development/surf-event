package dev.slne.surf.event.bmbf

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.plotsquared.core.PlotAPI
import dev.slne.surf.event.bmbf.listener.PlayerConnectionListener
import dev.slne.surf.surfapi.bukkit.api.event.register
import org.bukkit.plugin.java.JavaPlugin

lateinit var plotAPI: PlotAPI
class PaperMain: SuspendingJavaPlugin() {
    override suspend fun onEnableAsync() {
        plotAPI = PlotAPI()
        BmbfCategory.createAreas()

        PlayerConnectionListener.register()
    }
}

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)