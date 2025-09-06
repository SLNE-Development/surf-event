package dev.slne.surf.event.oneblock

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin

class PaperMain : SuspendingJavaPlugin() {
}

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)
val overworld: World get() = server.worlds.first()