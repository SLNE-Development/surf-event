package dev.slne.surf.event.oneblock

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.event.oneblock.command.*
import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.db.table.IslandTable
import dev.slne.surf.event.oneblock.db.table.PlayerStateTable
import dev.slne.surf.event.oneblock.island.IslandManager
import dev.slne.surf.event.oneblock.listener.OneBlockBlockListener
import dev.slne.surf.event.oneblock.listener.OneBlockConnectionListener
import dev.slne.surf.event.oneblock.listener.OneBlockSaveListener
import dev.slne.surf.event.oneblock.listener.OneBlockSpawnListener
import dev.slne.surf.event.oneblock.papi.OneBlockPapiExpansion
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.bukkit.api.hook.papi.papiHook
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin

class PaperMain : SuspendingJavaPlugin() {
    private lateinit var databaseApi: DatabaseApi

    override suspend fun onLoadAsync() {
        databaseApi = DatabaseApi.create(plugin.dataPath)

        suspendTransaction {
            SchemaUtils.create(
                IslandTable,
                PlayerStateTable
            )
        }
    }

    override fun onEnable() {
        OneBlockConnectionListener.register()
        OneBlockBlockListener.register()
        OneBlockSaveListener.register()
        OneBlockSpawnListener.register()

        relocateCommand()
        locateOneBlockCommand()
        phaseChestCommand()
        phaseCommand()
        oneBlockCommand()

        IslandManager.loadIdx()
        papiHook.register(OneBlockPapiExpansion())

        plugin.launch {
            IslandService.fetchIslands()
        }
    }

    override suspend fun onDisableAsync() {
        IslandManager.saveIdx()
        IslandService.flushAll()

        databaseApi.shutdown()
    }
}

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)
val overworld: World get() = server.worlds.first()