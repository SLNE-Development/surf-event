package dev.slne.surf.event.mythicmobs

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.event.mythicmobs.listener.ListenerManager
import io.lumine.mythic.bukkit.MythicBukkit
import org.bukkit.plugin.java.JavaPlugin

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onEnableAsync() {
        ListenerManager.register()
    }
}

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)
val mythic: MythicBukkit get() = MythicBukkit.inst()