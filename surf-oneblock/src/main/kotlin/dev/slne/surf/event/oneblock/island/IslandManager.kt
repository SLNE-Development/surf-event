package dev.slne.surf.event.oneblock.island

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import com.jeff_media.morepersistentdatatypes.DataType
import de.oliver.fancyholograms.api.FancyHologramsPlugin
import de.oliver.fancyholograms.api.data.TextHologramData
import de.oliver.fancyholograms.api.hologram.Hologram
import dev.slne.surf.event.oneblock.config.config
import dev.slne.surf.event.oneblock.data.IslandDTO
import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.overworld
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.surfapi.bukkit.api.pdc.block.pdc
import dev.slne.surf.surfapi.bukkit.api.util.key
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.random
import glm_.glm.sqrt
import io.papermc.paper.math.BlockPosition
import io.papermc.paper.math.Position
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.util.BoundingBox
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object IslandManager {
    private val oneBlockKey = key("one-block")
    private val idxKey = key("island-idx")

    private val idx = AtomicInteger(0)

    fun loadIdx() {
        val idx = overworld.persistentDataContainer.getOrDefault(idxKey, DataType.INTEGER, 0)
        this.idx.set(idx)
    }

    fun saveIdx() {
        overworld.persistentDataContainer.set(idxKey, DataType.INTEGER, idx.get())
    }

    fun getOwnerFromBlock(block: Block): UUID? {
        return IslandService.getOwnerFromBlock(block.location)
//        return block.pdc().get(oneBlockKey, DataType.UUID)
    }

    fun isOneBlock(block: Block): Boolean {
//        return block.pdc().has(oneBlockKey)
        return getOwnerFromBlock(block) != null
    }

    fun migrateOneBlock(to: Block, owner: UUID, oldLoc: Location) {
        to.pdc().set(oneBlockKey, DataType.UUID, owner)

        FancyHologramsPlugin.get().hologramManager.getHologram(hologramId(owner))
            .ifPresentOrElse({ hologram ->
                hologram.data.setLocation(toHologramLocation(to.location))
            }, {
                createHologram(owner, to.location)
            })

        plugin.launch(plugin.regionDispatcher(oldLoc)) {
            oldLoc.block.pdc().remove(oneBlockKey)
        }
    }

    suspend fun generateIsland(data: IslandDTO) {
        val center = data.oneBlock
        withContext(plugin.regionDispatcher(center)) {
            IslandStructure.place(center)

            val oneBlock = center.block
            oneBlock.pdc().set(oneBlockKey, DataType.UUID, data.owner)
        }
    }

    suspend fun createIslandForPlayer(uuid: UUID): Boolean {
        if (IslandService.hasIsland(uuid)) return false

        val spot = nextFreeSpot(overworld) ?: return false
        val island = IslandService.createIslandForPlayer(uuid, spot)
        generateIsland(island)
        createHologram(uuid, spot)

        return true
    }

    private fun createHologram(uuid: UUID, loc: Location) {
        val line = buildText {
            text(" ")
            primary("%oneblock_player-name_$uuid%")
            spacer(" | ")
            variableValue("%oneblock_level_$uuid%")
            spacer(" | ")
            variableValue("%oneblock_total-blocks_$uuid%")
            text(" ")
        }

        val hologramData = TextHologramData(hologramId(uuid), toHologramLocation(loc)).apply {
            text = listOf(MiniMessage.miniMessage().serialize(line))
            textUpdateInterval = 1000
            isSeeThrough = false
            isPersistent = true
            setTextShadow(true)
            background = Hologram.TRANSPARENT
        }

        val manager = FancyHologramsPlugin.get().hologramManager
        val hologram = manager.create(hologramData)

        manager.addHologram(hologram)
    }

    private fun toHologramLocation(loc: Location) = loc.clone().add(0.5, 2.3, 0.5)


    private fun hologramId(uuid: UUID) = "oneblock-$uuid"

    suspend fun nextFreeSpot(world: World): Location? {
        val step = config.islandPlacement.spacing
        val tryCount = config.islandPlacement.tryCount

        repeat(tryCount) {
            val spot = findSpot(step).toLocation(world)

            if (isSpotFree(spot)) {
                return spot
            }
        }

        return null
    }

    private fun findSpot(step: Int): BlockPosition {
        val n = idx.getAndIncrement() + 1
        val y = random.nextInt(config.islandPlacement.minY, config.islandPlacement.maxY)

        if (n == 1) {
            return Position.block(0, y, 0)
        }

        val nn = n - 1
        val a = ((sqrt(nn) + 1.0) / 2.0).toInt() // floor
        val c = 2 * a
        val b = n - (2 * a - 1) * (2 * a - 1)

        val (sx, sz) = when {
            b < c -> a to (-a + b)
            b < 2 * c -> (a - (b - c)) to a
            b < 3 * c -> (-a) to (a - (b - 2 * c))
            else -> (-a + (b - 3 * c)) to (-a)
        }

        val x = sx * step
        val z = sz * step

        return Position.block(x, y, z)
    }

    private suspend fun isSpotFree(loc: Location): Boolean {
        if (IslandService.getOwnerFromBlock(loc) != null) return false
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