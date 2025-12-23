package dev.slne.surf.event.oneblock.world

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator

object VoidWorldGenerator {

    fun generateVoidWorld(worldName: String): World {
        val chunkGenerator = VoidChunkGenerator()
        val world = Bukkit.createWorld(WorldCreator(worldName).generator(chunkGenerator))
            ?: error("Failed to create world.")

        world.getBlockAt(0, 64, 0).type = org.bukkit.Material.BEDROCK
        world.setSpawnLocation(0, 66, 0)

        return world
    }

}