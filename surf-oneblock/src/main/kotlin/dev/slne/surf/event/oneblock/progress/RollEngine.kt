package dev.slne.surf.event.oneblock.progress

import dev.slne.surf.event.oneblock.config.config
import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.surfapi.core.api.util.random
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.BlockType
import org.bukkit.block.Chest
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object RollEngine {
    fun roll(player: Player): Outcome {
        val island = IslandService.getIsland(player.uniqueId)
            ?: return Outcome(BlockType.DIRT.createBlockData())

        val currentPhase = phaseConfig.currentPhase(island.totalMined)
        val picked = currentPhase.blockSelector.pick()

        val chestChance = config.loot.chestSpawnChancePercentage
        val mobChance = config.loot.mobSpawnChancePercentage

        val spawnAction: ((World, Location) -> Unit)? = when {
            random.nextInt(100) < chestChance -> { w, l -> spawnChestLoot(w, l, currentPhase.id) }
            random.nextInt(100) < mobChance -> currentPhase.entitySelector?.pick()
                ?.let { e -> { w, l -> w.spawnEntity(l, e.type) } }

            else -> null
        }

        return Outcome(picked.data, spawnAction)
    }

    private fun spawnChestLoot(world: World, at: Location, phaseId: String) {
        val block = at.block
        block.blockData = BlockType.CHEST.createBlockData()
        block.state.apply {
            require(this is Chest)
            val entry = phaseChests.chestSelectors.get(phaseId).pick()
            val rawContent = entry.content
            val content = ItemStack.deserializeItemsFromBytes(rawContent)
            val leftOver = blockInventory.addItem(*content)

            for (stack in leftOver.values) {
                world.dropItemNaturally(at, stack)
            }
        }
    }

    data class Outcome(
        val blockData: BlockData,
        val spawnAction: ((World, Location) -> Unit)? = null
    )
}