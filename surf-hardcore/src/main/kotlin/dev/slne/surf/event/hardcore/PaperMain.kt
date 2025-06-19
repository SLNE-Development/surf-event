package dev.slne.surf.event.hardcore

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import dev.slne.surf.event.hardcore.papi.HardcorePapiHook
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.bukkit.api.hook.papi.papiHook
import kotlinx.coroutines.withContext
import org.bukkit.GameRule
import org.bukkit.plugin.java.JavaPlugin

class PaperMain : SuspendingJavaPlugin() {

    companion object {
        const val HARDCORE_BAN_SOURCE = "Hardcore"
    }

    override suspend fun onEnableAsync() {
        withContext(globalRegionDispatcher) {
            for (world in server.worlds) {
                world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false)
            }
        }
        HardcoreListener.register()
        papiHook.register(HardcorePapiHook)
    }
}

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)