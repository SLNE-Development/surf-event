package dev.slne.surf.event.anarchy

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.event.anarchy.finale.FinaleLifecycle
import dev.slne.surf.event.anarchy.finale.command.finaleCommand
import dev.slne.surf.event.anarchy.finale.listener.FinaleDimensionListener
import dev.slne.surf.event.anarchy.vertborder.VertBorderService
import dev.slne.surf.event.anarchy.vertborder.listener.VertBorderListener
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onEnableAsync() {
        FinaleLifecycle.create()

        VertBorderListener.register()
        FinaleDimensionListener.register()
        VertBorderService.start()

        finaleCommand()
    }

    override suspend fun onDisableAsync() {
        VertBorderService.stop()
        FinaleLifecycle.cancel()
    }
}