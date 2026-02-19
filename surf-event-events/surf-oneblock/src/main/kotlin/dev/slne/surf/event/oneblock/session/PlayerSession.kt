package dev.slne.surf.event.oneblock.session

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import com.github.shynixn.mccoroutine.folia.ticks
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.ComponentLike
import org.bukkit.Location
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
                appendErrorPrefix()
                error("Du kannst deinen OneBlock nicht abbauen, während du umziehst.")
            }
        }

        val outcome = RollEngine.roll(player)

        val blockLocation = block.location
        plugin.launch(plugin.regionDispatcher(blockLocation)) {
            outcome.spawnAction?.invoke(block.world, blockLocation.add(0.5, 1.0, 0.5))
            val drops = block.getDrops(player.inventory.itemInMainHand, player)
            for (drop in drops) {
                block.world.dropItem(blockLocation.add(0.5, 1.0, 0.5), drop)
            }

            delay(1.ticks)
            block.blockData = outcome.blockData
        }

        player.inventory.itemInMainHand.damage(1, player)

        ProgressService.onBlockMined(player)
    }

    suspend fun relocate(location: Location): RelocateResult {
        val island = IslandService.getIsland(uuid) ?: error("Island not found for player $uuid")

        return withContext(plugin.regionDispatcher(location)) {
            val block = location.block
            if (!block.isEmpty) {
                return@withContext RelocateResult.LOCATION_OCCUPIED
            }

            block.blockData = BlockType.DIRT.createBlockData()

            IslandManager.migrateOneBlock(block, uuid, island.oneBlock)
            IslandService.updateOneBlockLocation(uuid, location)

            RelocateResult.RELOCATED
        }
    }

    private fun flushState() {
        plugin.launch {
            PlayerStateService.flushState(state)
        }
    }

    override fun close() {}

    companion object {
        operator fun get(uuid: UUID): PlayerSession {
            return PlayerSessionManager.getSession(uuid)
        }
    }

    enum class RelocateResult(message: SurfComponentBuilder.() -> Unit) : ComponentLike {
        RELOCATED({
            appendSuccessPrefix()
            success("Dein OneBlock wurde erfolgreich umgezogen.")
        }),
        LOCATION_OCCUPIED({
            appendErrorPrefix()
            error("Der Zielort ist ungültig oder bereits belegt.")
        });

        val message = buildText(message)
        override fun asComponent() = message

        fun isSuccess() = this == RELOCATED
    }
}