package dev.slne.surf.event.oneblock.progress

import dev.slne.surf.event.oneblock.config.config
import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.surfapi.core.api.util.random
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.BlockType
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import kotlin.random.asKotlinRandom

object RollEngine {
    data class Outcome(
        val blockData: BlockData,
        val spawnAction: ((World, Location) -> Unit)? = null
    )

    fun roll(player: Player): Outcome {
        val island = IslandService.getIsland(player.uniqueId)
            ?: return Outcome(BlockType.DIRT.createBlockData())
        val phase = phaseConfig.currentPhase(island.totalMined)
        val weightedBlocks = mutableListOf<Pair<BlockData, Int>>()
        weightedBlocks += phase.blocks.map { it.blockData to it.weight }
        var carry = phase.weight - 1
        for (pid in phase.parents) {
            val p = phaseConfig.phases.first { it.id == pid }
            weightedBlocks += p.blocks.map { it.blockData to maxOf(1, carry) }
            carry = maxOf(1, carry - 1)
        }

        val total = weightedBlocks.sumOf { it.second }
        val r = random.nextInt(total)
        var acc = 0
        var data = BlockType.DIRT.createBlockData()
        for ((dat, w) in weightedBlocks) {
            acc += w; if (r < acc) {
                data = dat
                break
            }
        }

        val chestChance = config.loot.chestSpawnChancePercentage
        val mobChance = config.loot.mobSpawnChancePercentage
        val spawnAction: ((World, Location) -> Unit)? = when {
            random.nextInt(100) < chestChance -> { w, l -> spawnChestLoot(w, l) }
            random.nextInt(100) < mobChance -> phase.entities.randomOrNull(random.asKotlinRandom())
                ?.let { e -> { w, l -> w.spawnEntity(l, e.type) } }

            else -> null
        }

        return Outcome(data, spawnAction)
    }

    private fun spawnChestLoot(world: World, at: Location) {
        val b = at.block
        b.type = Material.CHEST
        val inv = (b.state as org.bukkit.block.Container).inventory
        inv.addItem(org.bukkit.inventory.ItemStack(Material.BREAD, 3))
        inv.addItem(org.bukkit.inventory.ItemStack(Material.OAK_SAPLING, 1))
    }
}