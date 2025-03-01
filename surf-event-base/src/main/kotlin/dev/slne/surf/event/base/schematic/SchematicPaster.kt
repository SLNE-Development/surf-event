package dev.slne.surf.event.base.schematic

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.WorldEditException
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.world.block.BlockType
import dev.slne.surf.event.base.schematic.exception.SchematicPasteException
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.exists

class SchematicPaster(private val plugin: JavaPlugin) {

    fun canPaste(
        clipboard: Clipboard,
        pasteLocation: Location,
        safeList: List<BlockType>
    ): Boolean {
        val adaptedWorld = BukkitAdapter.adapt(pasteLocation.world)

        val pasteVector3 = BlockVector3.at(
            pasteLocation.x,
            pasteLocation.y,
            pasteLocation.z
        )

        for (x in 0..<clipboard.dimensions.x()) {
            for (y in 0..<clipboard.dimensions.y()) {
                for (z in 0..<clipboard.dimensions.z()) {
                    val relativePosition = BlockVector3.at(x, y, z)
                    val worldPosition = pasteVector3.add(relativePosition)

                    val block = adaptedWorld.getBlock(worldPosition)

                    if (!safeList.contains(block.blockType)) {
                        return false
                    }

                    if (!pasteLocation.world.worldBorder.isInside(
                            Location(
                                pasteLocation.world,
                                worldPosition.x().toDouble(),
                                worldPosition.y().toDouble(),
                                worldPosition.z().toDouble()
                            )
                        )
                    ) {
                        return false
                    }
                }
            }
        }

        return true
    }

    @Throws(IllegalArgumentException::class, SchematicPasteException::class)
    fun readSchematic(schematicName: String): Clipboard {
        val pluginDataFolder: Path = plugin.dataPath
        val schematicPath = pluginDataFolder.resolve("schematics")

        if (!schematicPath.exists()) {
            schematicPath.toFile().mkdirs()
        }

        val schematicFile = File(schematicPath.toFile(), schematicName)
        require(schematicFile.exists()) { "Schematic file does not exist." }

        val format = ClipboardFormats.findByFile(schematicFile)
        requireNotNull(format) { "Schematic file is not a valid format." }

        try {
            format.getReader(FileInputStream(schematicFile)).use { reader ->
                return reader.read()
            }
        } catch (exception: IOException) {
            throw SchematicPasteException("Failed to read schematic.", exception)
        } catch (exception: WorldEditException) {
            throw SchematicPasteException("Failed to read schematic.", exception)
        }
    }

    fun pasteSchematic(world: World, clipboard: Clipboard, pasteLocation: Location) {
        WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world)).use { editSession ->
            val operation: Operation = ClipboardHolder(clipboard)
                .createPaste(editSession)
                .to(BlockVector3.at(pasteLocation.x, pasteLocation.y, pasteLocation.z))
                .ignoreAirBlocks(false)
                .copyEntities(true)
                .build()

            Operations.complete(operation)
        }
    }
}
