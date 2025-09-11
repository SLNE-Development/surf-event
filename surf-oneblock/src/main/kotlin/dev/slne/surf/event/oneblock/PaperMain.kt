package dev.slne.surf.event.oneblock

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.database.DatabaseManager
import dev.slne.surf.event.oneblock.command.locateOneBlockCommand
import dev.slne.surf.event.oneblock.command.phaseChestCommand
import dev.slne.surf.event.oneblock.command.relocateCommand
import dev.slne.surf.event.oneblock.config.OneBlockConfigHolder
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
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.io.path.div

class PaperMain : SuspendingJavaPlugin() {
    private val dbManager by lazy { DatabaseManager(dataPath, dataPath / "storage") }

    override suspend fun onLoadAsync() {
        OneBlockConfigHolder.config
        dbManager.databaseProvider.connect()

        transaction {
            SchemaUtils.create(
                IslandTable,
                PlayerStateTable
            )
        }
    }

    override suspend fun onEnableAsync() {
        IslandService.fetchIslands()
        OneBlockConnectionListener.register()
        OneBlockBlockListener.register()
        OneBlockSaveListener.register()
        OneBlockSpawnListener.register()

        relocateCommand()
        locateOneBlockCommand()
        phaseChestCommand()

        IslandManager.loadIdx()
        papiHook.register(OneBlockPapiExpansion())
    }

    override suspend fun onDisableAsync() {
        IslandManager.saveIdx()
        IslandService.fetchIslands()
        dbManager.databaseProvider.disconnect()
    }
}

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)
val overworld: World get() = server.worlds.first()