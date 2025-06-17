package dev.slne.surf.event.hardcore

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiExpansion
import dev.slne.surf.surfapi.bukkit.api.hook.papi.papiHook
import org.bukkit.plugin.java.JavaPlugin

class PaperMain : SuspendingJavaPlugin() {

    companion object {
        const val HARDCORE_BAN_SOURCE = "Hardcore"
    }

    override fun onEnable() {
        HardcoreListener.register()

        papiHook.register(PapiExpansion(
            "hardcore",
            listOf(HardcoreDeathCountPlaceholder())
        ))
    }
}

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)