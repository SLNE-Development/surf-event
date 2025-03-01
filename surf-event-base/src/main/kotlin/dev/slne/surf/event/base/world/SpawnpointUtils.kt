package dev.slne.surf.event.base.world

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.jeff_media.morepersistentdatatypes.DataType
import com.sk89q.worldedit.extent.clipboard.Clipboard
import dev.slne.surf.event.base.schematic.SchematicPaster
import dev.slne.surf.event.base.schematic.exception.SchematicPasteException
import dev.slne.surf.surfapi.bukkit.api.util.key
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataContainer
import java.util.function.Consumer

object SpawnpointUtils {

    private val JOIN_KEY = key("joined")
    private val JOIN_LOCATION_KEY = key("joined_location")

    fun Player.hasJoinedBefore() = persistentDataContainer.has(JOIN_KEY)

    suspend fun generateSpawnpoint(
        player: Player,
        world: World,
        changeDataContainer: Boolean,
        schematicPaster: SchematicPaster,
        schematic: Clipboard,
        plugin: SuspendingJavaPlugin,
        generatedSpawnpoint: Consumer<Location?>? = null,
    ) {
        try {
            val spawnable = WorldUtils.findSpawnableLocation(
                spawnSchematic = schematic,
                world = world,
                schematicPaster = schematicPaster,
                player = player,
                plugin = plugin
            )
            val pdc: PersistentDataContainer = player.persistentDataContainer

            schematicPaster.pasteSchematic(world, schematic, spawnable)

            val result = runCatching { player.teleportAsync(spawnable).await() }.getOrNull()

            if (result == null || !result) {
                plugin.launch {
                    withContext(plugin.entityDispatcher(player)) {
                        if (changeDataContainer) {
                            pdc.remove(JOIN_KEY)
                        }

                        generatedSpawnpoint?.accept(null)
                    }
                }

                throw RuntimeException("Failed to teleport player to spawnpoint.")
            }

            plugin.launch {
                withContext(plugin.entityDispatcher(player)) {
                    if (changeDataContainer) {
                        pdc.set(JOIN_KEY, DataType.BOOLEAN, true)
                        pdc.set(JOIN_LOCATION_KEY, DataType.LOCATION, spawnable)
                    }

                    player.setRespawnLocation(spawnable, true)
                }
            }

            generatedSpawnpoint?.accept(spawnable)
        } catch (exception: SchematicPasteException) {
            throw RuntimeException(exception)
        }
    }
}
