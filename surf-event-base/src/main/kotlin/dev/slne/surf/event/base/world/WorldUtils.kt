package dev.slne.surf.event.base.world

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.world.block.BlockType
import com.sk89q.worldedit.world.block.BlockTypes
import dev.slne.surf.event.base.schematic.SchematicPaster
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldBorder
import org.bukkit.entity.Player
import java.util.*

object WorldUtils {

    private val BLOCK_TYPES: List<BlockType> = listOf(
        Objects.requireNonNull<BlockType>(BlockTypes.AIR)
    )

    fun findSpawnableLocation(
        player: Player,
        world: World,
        schematicPaster: SchematicPaster,
        spawnSchematic: Clipboard,
        plugin: SuspendingJavaPlugin,
        iterations: Int = 0
    ): Location {
        val safeLocation = getRandomBoundsLocation(world)

        if (iterations > 20) {
            plugin.launch {
                withContext(plugin.entityDispatcher(player)) {
                    player.kick(
                        Component.text("Es konnte kein sicherer Spawn gefunden werden! Bitte versuche es erneut!")
                    )
                }
            }
        }

        if (!isLocationSafe(schematicPaster, spawnSchematic, safeLocation)) {
            return findSpawnableLocation(
                player,
                world,
                schematicPaster,
                spawnSchematic,
                plugin,
                iterations + 1
            )
        }

        return safeLocation
    }

    fun getRandomBoundsLocation(world: World): Location {
        val worldBorder: WorldBorder = world.worldBorder
        val worldBorderSizeDistance: Double = worldBorder.getSize()
        val worldBorderSizeRadius = worldBorderSizeDistance / 2

        val xNegative = Math.random() < 0.5
        val randomX = (Math.random() * worldBorderSizeRadius).toInt()

        val minY = world.minHeight
        val maxY = world.maxHeight
        val randomY = (Math.random() * (maxY - minY) + minY).toInt()

        val zNegative = Math.random() < 0.5
        val randomZ = (Math.random() * worldBorderSizeRadius).toInt()

        return Location(
            world,
            (if (xNegative) -randomX else randomX).toDouble(),
            randomY.toDouble(),
            (if (zNegative) -randomZ else randomZ).toDouble()
        )
    }

    private fun isLocationSafe(
        schematicPaster: SchematicPaster,
        clipboard: Clipboard,
        location: Location
    ) = schematicPaster.canPaste(clipboard, location, BLOCK_TYPES)
}
