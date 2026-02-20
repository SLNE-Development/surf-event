package dev.slne.surf.event.oneblock.island

import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.event.oneblock.plugin
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.block.BlockType

object IslandStructure {
    suspend fun place(center: Location) = withContext(plugin.regionDispatcher(center)) {
        val cx = center.blockX
        val cy = center.blockY
        val cz = center.blockZ

        center.block.blockData = BlockType.GRASS_BLOCK.createBlockData()

//        val baseY = cy - 1

//        WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(center.world)).use { session ->
//            val islandState = BukkitAdapter.adapt(config.islandPlacement.islandBlockData)
//            val bedrockState = BlockTypes.BEDROCK!!.defaultState
//            val oneBlockState = BlockTypes.GRASS_BLOCK!!.defaultState

//            val islandRegion = CuboidRegion(
//                BlockVector3.at(cx - 1, baseY - 2, cz - 1),
//                BlockVector3.at(cx + 1, baseY, cz + 1)
//            )
//            val bedrockRegion = CuboidRegion(
//                BlockVector3.at(cx - 1, baseY - 2, cz - 1),
//                BlockVector3.at(cx + 1, baseY - 2, cz + 1)
//            )
//
//
//
//            session.setBlocks<BlockState>(islandRegion, islandState)
//            session.setBlocks<BlockState>(bedrockRegion, bedrockState)
//            session.setBlock(cx, cy, cz, oneBlockState)
//            session.setBlock(cx, baseY, cz, bedrockState)
//        }
    }
}