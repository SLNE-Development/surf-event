package dev.slne.surf.event.oneblock.session

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.event.oneblock.progress.ProgressService
import dev.slne.surf.event.oneblock.progress.RollEngine
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.*

class PlayerSession(val uuid: UUID) {

    fun onMine(player: Player, block: Block) {
        val outcome = RollEngine.roll(player)

        plugin.launch(plugin.regionDispatcher(block.location)) {
            outcome.spawnAction?.invoke(block.world, block.location.add(0.5, 1.0, 0.5))
            block.blockData = outcome.blockData
        }

        ProgressService.onBlockMined(player)
    }

    companion object {
        operator fun get(uuid: UUID): PlayerSession {
            return PlayerSessionManager.getSession(uuid)
        }
    }
}