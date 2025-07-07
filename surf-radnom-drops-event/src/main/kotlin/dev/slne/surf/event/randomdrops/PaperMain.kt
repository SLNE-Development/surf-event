package dev.slne.surf.event.randomdrops

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.database.DatabaseProvider
import dev.slne.surf.event.randomdrops.data.PlayerDataStorage
import dev.slne.surf.event.randomdrops.listener.ListenerManager
import org.bukkit.plugin.java.JavaPlugin
import kotlin.io.path.div

class PaperMain : SuspendingJavaPlugin() {
    private val databaseProvider = DatabaseProvider(dataPath, dataPath / "storage")

    override suspend fun onLoadAsync() {
        databaseProvider.connect()
        PlayerDataStorage.createTables()
    }

    override suspend fun onEnableAsync() {
        ListenerManager.register()
    }

    override suspend fun onDisableAsync() {
        PlayerDataStorage.flush()
        databaseProvider.disconnect()
    }
}

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)