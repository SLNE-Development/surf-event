package dev.slne.surf.event.oneblock.progress

import dev.slne.surf.event.oneblock.config.config
import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.random
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.BlockType
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import kotlin.random.asKotlinRandom

object RollEngine {
    private data class WeightedBlock(val data: BlockData, val weight: Int, val phaseId: String)

    data class Outcome(
        val blockData: BlockData,
        val spawnAction: ((World, Location) -> Unit)? = null
    )

    fun roll(player: Player): Outcome {
        val island = IslandService.getIsland(player.uniqueId)
            ?: return Outcome(BlockType.DIRT.createBlockData())

        val currentPhase = phaseConfig.currentPhase(island.totalMined)
        val choices = buildChoices(currentPhase)
        val picked = pick(choices)

        val chestChance = config.loot.chestSpawnChancePercentage
        val mobChance = config.loot.mobSpawnChancePercentage

        val spawnAction: ((World, Location) -> Unit)? = when {
            random.nextInt(100) < chestChance -> { w, l -> spawnChestLoot(w, l) }
            random.nextInt(100) < mobChance -> currentPhase.entities.randomOrNull(random.asKotlinRandom())
                ?.let { e -> { w, l -> w.spawnEntity(l, e.type) } }

            else -> null
        }

        return Outcome(picked.data, spawnAction)
    }

    private fun buildChoices(current: PhaseConfig.Phase): ObjectList<WeightedBlock> {
        val choices = mutableObjectListOf<WeightedBlock>()

        choices += current.blocks.mapTo(mutableObjectListOf()) {
            WeightedBlock(
                it.blockData,
                it.weight,
                current.id
            )
        }

        var carry = current.weight - 1
        for (pid in current.parents) {
            val parent = phaseConfig.findById(pid) ?: continue
            val weight = maxOf(1, carry)

            choices += parent.blocks.map { WeightedBlock(it.blockData, weight, parent.id) }
            carry = maxOf(1, carry - 1)
        }

        return choices
    }

    private fun pick(choices: ObjectList<WeightedBlock>): WeightedBlock {
        if (choices.isEmpty()) {
            return WeightedBlock(BlockType.DIRT.createBlockData(), 1, phaseConfig.firstPhase().id)
        }

        val total = choices.sumOf { it.weight }
        var r = random.nextInt(total)
        for (c in choices) {
            r -= c.weight
            if (r < 0) return c
        }

        return choices.last()
    }

    private fun spawnChestLoot(world: World, at: Location) {
        val b = at.block
        b.type = Material.CHEST
        val inv = (b.state as org.bukkit.block.Container).inventory
        inv.addItem(org.bukkit.inventory.ItemStack(Material.BREAD, 3))
        inv.addItem(org.bukkit.inventory.ItemStack(Material.OAK_SAPLING, 1))
    }
}