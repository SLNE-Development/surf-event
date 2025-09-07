package dev.slne.surf.event.oneblock.island

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.world.block.BlockState
import com.sk89q.worldedit.world.block.BlockTypes
import dev.slne.surf.event.oneblock.config.OneBlockConfigHolder.config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Location

object IslandStructure {
    suspend fun place(center: Location) = withContext(Dispatchers.IO) {
        val cx = center.blockX
        val cy = center.blockY
        val cz = center.blockZ
        val baseY = cy - 1

        WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(center.world)).use { session ->
            val islandState = BukkitAdapter.adapt(config.islandPlacement.islandBlockData)
            val bedrockState = BlockTypes.BEDROCK!!.defaultState
            val oneBlockState = BlockTypes.TORCH!!.defaultState

            val islandRegion = CuboidRegion(
                BlockVector3.at(cx - 1, baseY - 2, cz - 1),
                BlockVector3.at(cx + 1, baseY, cz + 1)
            )
            val bedrockRegion = CuboidRegion(
                BlockVector3.at(cx - 1, baseY - 2, cz - 1),
                BlockVector3.at(cx + 1, baseY - 2, cz + 1)
            )



            session.setBlocks<BlockState>(islandRegion, islandState)
            session.setBlocks<BlockState>(bedrockRegion, bedrockState)
            session.setBlock(cx, cy, cz, oneBlockState)
        }
    }
}