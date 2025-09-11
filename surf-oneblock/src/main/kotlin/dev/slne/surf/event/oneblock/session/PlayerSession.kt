package dev.slne.surf.event.oneblock.session

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import dev.slne.surf.event.oneblock.config.config
import dev.slne.surf.event.oneblock.data.PlayerStateDTO
import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.db.PlayerStateService
import dev.slne.surf.event.oneblock.island.IslandManager
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.event.oneblock.progress.ProgressService
import dev.slne.surf.event.oneblock.progress.RollEngine
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import glm_.pow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.ComponentLike
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockType
import org.bukkit.entity.Player
import java.io.Closeable
import java.util.*

class PlayerSession(val uuid: UUID, private val state: PlayerStateDTO) : Closeable {

    val isRelocating: Boolean
        get() = state.relocating

    fun onMine(player: Player, block: Block) {
        if (isRelocating) {
            return player.sendText {
                appendPrefix()
                error("Du kannst keine Blöcke abbauen, während du deinen OneBlock umziehst.")
            }
        }

        val outcome = RollEngine.roll(player)

        val blockLocation = block.location
        plugin.launch(plugin.regionDispatcher(blockLocation)) {
            outcome.spawnAction?.invoke(block.world, blockLocation.add(0.5, 1.0, 0.5))
            val drops = block.getDrops(player.inventory.itemInMainHand, player)
            for (drop in drops) {
                block.world.dropItemNaturally(blockLocation.add(0.5, 1.0, 0.5), drop)
            }

            block.type = Material.AIR

            withContext(Dispatchers.IO) {
                WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(block.world))
                    .use { session ->
                        session.setBlock(
                            block.x,
                            block.y,
                            block.z,
                            BukkitAdapter.adapt(outcome.blockData)
                        )
                    }
            }


//            block.blockData = outcome.blockData
//            block.state.update(true, false)
//            block.state.update(true, false)
//            block.world.setBlockData(block.location, outcome.blockData)


        }

        ProgressService.onBlockMined(player)
    }

    suspend fun startRelocate(player: Player): RelocateResult {
        if (isRelocating) {
            return RelocateResult.ALREADY_RELOCATING
        }

        if (!isInRangeOfOneBlock(player)) {
            return RelocateResult.NOT_IN_RANGE
        }

        if (isOnRelocationCooldown()) {
            return RelocateResult.COOLDOWN
        }

        state.relocating = true
        flushState()

        return RelocateResult.START_RELOCATING
    }

    suspend fun finishRelocate(loc: Location): RelocateResult {
        val island = IslandService.getIsland(uuid) ?: error("Island not found for player $uuid")

        return withContext(plugin.regionDispatcher(loc)) {
            val block = loc.block
            if (!block.isEmpty) {
                return@withContext RelocateResult.LOCATION_OCCUPIED
            }

            block.blockData = BlockType.DIRT.createBlockData()
            IslandManager.migrateOneBlock(block, uuid, island.oneBlock)
            IslandService.updateOneBlockLocation(uuid, loc)

            state.relocating = false
            state.relocateTimestamp = System.currentTimeMillis()
            flushState()

            RelocateResult.RELOCATED
        }
    }

    fun abortRelocate() {
        if (isRelocating) {
            state.relocating = false
            state.relocateTimestamp = System.currentTimeMillis()
            flushState()
        }
    }

    private fun isOnRelocationCooldown(): Boolean {
        val cooldown = config.relocate.relocateCooldownSeconds * 1000L
        return System.currentTimeMillis() - state.relocateTimestamp < cooldown
    }

    suspend fun isInRangeOfOneBlock(player: Player): Boolean {
        val island = IslandService.getIsland(uuid) ?: return false
        if (player.world != island.oneBlock.world) {
            return false
        }

        val maxDistanceSquared = config.relocate.relocateRadius pow 2

        return withContext(plugin.entityDispatcher(player)) {
            player.location.distanceSquared(island.oneBlock) <= maxDistanceSquared
        }
    }

    private fun flushState() {
        plugin.launch {
            PlayerStateService.flushState(state)
        }
    }

    override fun close() {
        abortRelocate()
    }

    companion object {
        operator fun get(uuid: UUID): PlayerSession {
            return PlayerSessionManager.getSession(uuid)
        }
    }

    enum class RelocateResult(message: SurfComponentBuilder.() -> Unit) : ComponentLike {
        START_RELOCATING({
            appendPrefix()
            success("Du kannst nun einen neuen Ort für deinen OneBlock auswählen.")
            appendNewPrefixedLine {
                info("Wähle dazu einen Block aus und benutze ")
                info("/relocate place <location>")
            }
        }),
        RELOCATED({
            appendPrefix()
            success("Dein OneBlock wurde erfolgreich umgezogen.")
        }),
        COOLDOWN({
            appendPrefix()
            error("Du musst noch warten, bevor du erneut Umziehen kannst.")
        }),
        NOT_IN_RANGE({
            appendPrefix()
            error("Du bist zu weit von deinem OneBlock entfernt.")
        }),
        ALREADY_RELOCATING({
            appendPrefix()
            error("Du befindest dich bereits im Umzugsmodus.")
        }),
        LOCATION_OCCUPIED({
            appendPrefix()
            error("Der Zielort ist ungültig oder bereits belegt.")
        });

        val message = buildText(message)
        override fun asComponent() = message
    }
}