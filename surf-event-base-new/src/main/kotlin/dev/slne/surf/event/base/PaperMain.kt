package dev.slne.surf.event.base

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import org.bukkit.plugin.java.JavaPlugin

internal class PaperMain : SuspendingJavaPlugin() {
}

internal val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)