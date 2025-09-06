package dev.slne.surf.event.oneblock.island

import com.github.shynixn.mccoroutine.folia.regionDispatcher
import com.jeff_media.morepersistentdatatypes.DataType
import dev.slne.surf.event.oneblock.config.config
import dev.slne.surf.event.oneblock.data.IslandDTO
import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.overworld
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.surfapi.bukkit.api.pdc.block.pdc
import dev.slne.surf.surfapi.bukkit.api.util.key
import io.papermc.paper.math.BlockPosition
import io.papermc.paper.math.Position
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockType
import org.bukkit.util.BoundingBox
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object IslandManager {
    private val oneBlockKey = key("one-block")
    private val idx = AtomicInteger(0)

    fun getOwnerFromBlock(block: Block): UUID? {
        return block.pdc().get(oneBlockKey, DataType.UUID)
    }

    suspend fun generateIsland(data: IslandDTO) {
        val center = data.oneBlock
        val world = center.world
        val chunk = world.getChunkAtAsync(center).await()
        withContext(plugin.regionDispatcher(center)) {
            chunk.addPluginChunkTicket(plugin)

            val baseY = center.blockY - 1
            for (x in -1..1) {
                for (z in -1..1) {
                    val block = world.getBlockAt(center.blockX + x, baseY, center.blockZ + z)
                    block.setBlockData(config.islandPlacement.islandBlockData, false)
                }
            }

            val oneBlock = center.block
            oneBlock.pdc().set(oneBlockKey, DataType.UUID, data.owner)
            oneBlock.blockData = BlockType.TORCH.createBlockData()

            chunk.removePluginChunkTicket(plugin)
        }
    }

    suspend fun createIslandForPlayer(uuid: UUID): Boolean {
        if (IslandService.hasIsland(uuid)) return false

        val spot = nextFreeSpot(overworld) ?: return false
        val island = IslandService.createIslandForPlayer(uuid, spot)
        generateIsland(island)

        return true
    }

    suspend fun nextFreeSpot(world: World): Location? {
        val step = config.islandPlacement.spacing
        val yLevel = config.islandPlacement.yLevel
        val tryCount = config.islandPlacement.tryCount

        repeat(tryCount) {
            val spot = findSpot(step, yLevel).toLocation(world)

            if (isSpotFree(spot)) {
                return spot
            }
        }

        return null
    }

    private fun findSpot(
        step: Int,
        y: Int,
    ): BlockPosition {
        val idx = idx.getAndIncrement()
        val x = (idx % 32) * step
        val z = (idx / 32) * step

        return Position.block(x, y, z)
    }

    private suspend fun isSpotFree(loc: Location): Boolean {
        val chunk = loc.world.getChunkAtAsync(loc).await()

        val notFree = withContext(plugin.regionDispatcher(loc)) {
            chunk.addPluginChunkTicket(plugin)

            val x = loc.x
            val y = loc.y
            val z = loc.z

            val checkAround = config.islandPlacement.checkAround / 2.0
            val box = BoundingBox(
                x - checkAround,
                y - checkAround,
                z - checkAround,
                x + checkAround,
                y + checkAround,
                z + checkAround,
            )

            loc.world.hasCollisionsIn(box).also { chunk.removePluginChunkTicket(plugin) }
        }

        return !notFree
    }
}