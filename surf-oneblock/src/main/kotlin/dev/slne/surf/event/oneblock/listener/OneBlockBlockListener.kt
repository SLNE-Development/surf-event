package dev.slne.surf.event.oneblock.listener

import com.destroystokyo.paper.event.block.BlockDestroyEvent
import dev.slne.surf.event.oneblock.island.IslandManager
import dev.slne.surf.event.oneblock.session.PlayerSession
import dev.slne.surf.surfapi.bukkit.api.event.cancel
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.event.world.StructureGrowEvent

object OneBlockBlockListener : Listener {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val owner = IslandManager.getOwnerFromBlock(event.block) ?: return
        event.cancel()
        if (owner != event.player.uniqueId) {
            event.player.sendText {
                appendPrefix()
                error("Du kannst nur deinen eigenen OneBlock abbauen.")
            }
            return
        }

        PlayerSession[owner].onMine(event.player, event.block)
    }

    @EventHandler
    fun onBlockDestroy(event: BlockDestroyEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            return event.cancel()
        }

        event.blockList().removeIf { IslandManager.isOneBlock(it) }
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        event.blockList().removeIf { IslandManager.isOneBlock(it) }
    }

    @EventHandler
    fun onBlockFade(event: BlockFadeEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onBlockBurn(event: BlockBurnEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onBlockSpread(event: BlockSpreadEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onBlockFromTo(event: BlockFromToEvent) {
        if (IslandManager.isOneBlock(event.block) || IslandManager.isOneBlock(event.toBlock)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onBlockForm(event: BlockFormEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onSpongeAbsorb(event: SpongeAbsorbEvent) {
        event.blocks.removeIf { IslandManager.isOneBlock(it.block) }
    }

    @EventHandler
    fun onLeavesDecay(event: LeavesDecayEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onBlockGrow(event: BlockGrowEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            event.cancel()
        }
    }


    @EventHandler
    fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onEntityBlockForm(event: EntityBlockFormEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onBlockPistonExtend(event: BlockPistonExtendEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            return event.cancel()
        }

        if (event.blocks.any { IslandManager.isOneBlock(it) }) {
            event.cancel()
        }
    }

    @EventHandler
    fun onBlockPistonRetract(event: BlockPistonRetractEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            return event.cancel()
        }

        if (event.blocks.any { IslandManager.isOneBlock(it) }) {
            event.cancel()
        }
    }

    @EventHandler
    fun onBlockDispense(event: BlockDispenseEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onBlockIgnite(event: BlockIgniteEvent) {
        if (IslandManager.isOneBlock(event.block)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onStructureGrow(event: StructureGrowEvent) {
        if (IslandManager.isOneBlock(event.location.block)) {
            event.cancel()
        }

        event.blocks.removeIf { IslandManager.isOneBlock(it.block) }
    }

    @EventHandler
    fun onPortalCreate(event: PortalCreateEvent) {
        if (event.blocks.any { IslandManager.isOneBlock(it.block) }) {
            event.cancel()
        }

    }

}