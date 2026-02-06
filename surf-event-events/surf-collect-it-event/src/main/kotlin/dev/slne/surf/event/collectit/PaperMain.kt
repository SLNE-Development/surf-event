package dev.slne.surf.event.collectit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import org.bukkit.plugin.java.JavaPlugin

class CollectItEvent : SuspendingJavaPlugin() {
}

private val plugin = JavaPlugin.getPlugin(CollectItEvent::class.java)