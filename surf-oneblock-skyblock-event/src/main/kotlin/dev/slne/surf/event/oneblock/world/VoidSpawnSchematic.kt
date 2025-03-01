package dev.slne.surf.event.oneblock.world

import com.sk89q.worldedit.extent.clipboard.Clipboard
import dev.slne.surf.event.oneblock.oneblockPlugin

object VoidSpawnSchematic {

    val SPAWN_SCHEMATIC: Clipboard = oneblockPlugin.schematicPaster.readSchematic("spawn.schem")

}