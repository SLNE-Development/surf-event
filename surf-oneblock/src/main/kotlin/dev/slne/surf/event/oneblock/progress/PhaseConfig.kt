package dev.slne.surf.event.oneblock.progress

import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.event.oneblock.progress.PhaseConfig.Phase
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import dev.slne.surf.surfapi.core.api.random.Weighted
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.block.BlockType
import org.bukkit.block.data.BlockData
import org.bukkit.entity.EntityType
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting
import java.util.random.RandomGenerator

private val examplePhases = listOf(
    Phase(
        id = "phase_1",
        startsAt = 0,
        weight = 1,
        parents = emptyList(),
        blocks = listOf(
            PhaseConfig.BlockEntry("minecraft:stone", 70),
            PhaseConfig.BlockEntry("minecraft:dirt", 20),
            PhaseConfig.BlockEntry("minecraft:coal_ore", 10),
        ),
        entities = listOf(
            PhaseConfig.EntityEntry(EntityType.BAT, 5.0),
            PhaseConfig.EntityEntry(EntityType.SPIDER, 2.0),
        )
    ),
    Phase(
        id = "phase_2",
        startsAt = 100,
        weight = 2,
        parents = listOf("phase_1"),
        blocks = listOf(
            PhaseConfig.BlockEntry("minecraft:stone", 50),
            PhaseConfig.BlockEntry("minecraft:dirt", 20),
            PhaseConfig.BlockEntry("minecraft:coal_ore", 15),
            PhaseConfig.BlockEntry("minecraft:iron_ore", 10),
            PhaseConfig.BlockEntry("minecraft:cobblestone", 5),
        ),
        entities = listOf(
            PhaseConfig.EntityEntry(EntityType.BAT, 5.0),
            PhaseConfig.EntityEntry(EntityType.SPIDER, 5.0),
            PhaseConfig.EntityEntry(EntityType.ZOMBIE, 2.0),
        )
    ),
    Phase(
        id = "phase_3",
        startsAt = 500,
        weight = 3,
        parents = listOf("phase_2"),
        blocks = listOf(
            PhaseConfig.BlockEntry("minecraft:stone", 30),
            PhaseConfig.BlockEntry("minecraft:dirt", 20),
            PhaseConfig.BlockEntry("minecraft:coal_ore", 15),
            PhaseConfig.BlockEntry("minecraft:iron_ore", 15),
            PhaseConfig.BlockEntry("minecraft:cobblestone", 10),
            PhaseConfig.BlockEntry("minecraft:gold_ore", 5),
            PhaseConfig.BlockEntry("minecraft:gravel", 5),
        ),
        entities = listOf(
            PhaseConfig.EntityEntry(EntityType.BAT, 5.0),
            PhaseConfig.EntityEntry(EntityType.SPIDER, 5.0),
            PhaseConfig.EntityEntry(EntityType.ZOMBIE, 5.0),
            PhaseConfig.EntityEntry(EntityType.SKELETON, 2.0),
        )
    )
)

@ConfigSerializable
data class PhaseConfig(
    @Setting("phases")
    val unsortedPhases: List<Phase> = examplePhases,
) {
    @Transient
    val phases = unsortedPhases.sortedBy { it.startsAt }.toObjectList()

    init {
        require(phases.isNotEmpty()) { "There must be at least one phase defined" }
    }

    @ConfigSerializable
    data class BlockEntry(
        val data: String,
        val weight: Int
    ) {
        @Transient
        val blockData = server.createBlockData(data)

        init {
            require(weight > 0) { "Weight must be greater than 0" }
        }
    }

    @ConfigSerializable
    data class EntityEntry(
        val type: EntityType,
        override val weight: Double,
    ) : Weighted {
        init {
            require(type.isSpawnable) { "Entity type must be spawnable" }
        }
    }

    @ConfigSerializable
    data class Phase(
        val id: String,
        val displayName: String = id.replaceFirstChar { it.uppercaseChar() }.replace('_', ' '),
        val startsAt: Int,
        val weight: Int,
        val parents: List<String>,
        val blocks: List<BlockEntry>,
        val entities: List<EntityEntry>
    ) {
        companion object {
            private const val PARENT_SHARE_PER_WEIGHT = 0.15
            private const val PARENT_SHARE_MAX = 0.60
            private const val PARENT_DECAY = 0.5
        }

        val entitySelector by lazy {
            entities.takeIf { entities.isNotEmpty() }?.let { entities ->
                SimpleWeightedSelector(entities)
            }
        }

        val blockChoices by lazy {
            println("[OneBlock] Building block choices for phase '$id'...")
            buildChoices()
        }
        val blockSelector by lazy {
            println("[OneBlock] Phase '$id' has ${blockChoices.size} block choices.")
            SimpleWeightedSelector(blockChoices)
        }

        private fun buildChoices(): ObjectList<WeightedBlock> {
            val choices = mutableObjectListOf<WeightedBlock>()

            val ownTotal = blocks.sumOf { it.weight.toDouble() }

            choices.ensureCapacity(blocks.size)
            for (entry in this.blocks) {
                choices += WeightedBlock(entry.blockData, entry.weight.toDouble(), this.id)
            }

            val extraSteps = (this.weight - 1).coerceAtLeast(0)
            val parentShare = (extraSteps * PARENT_SHARE_PER_WEIGHT).coerceIn(0.0, PARENT_SHARE_MAX)
            val parentBudget = ownTotal * parentShare
            var remainingBudget = parentBudget

            var levelFactor = 1.0
            var levelFactorSum = 0.0
            val levelFactors = ArrayList<Double>(parents.size)
            for (i in parents.indices) {
                levelFactors += levelFactor
                levelFactorSum += levelFactor
                levelFactor *= PARENT_DECAY
            }

            for ((idx, parentId) in parents.withIndex()) {
                if (remainingBudget <= 1e-9) break
                val parent = config.findById(parentId) ?: continue
                val parentBlocks = parent.blocks
                if (parentBlocks.isEmpty()) continue

                val parentRawTotal = parentBlocks.sumOf { it.weight.toDouble() }
                if (parentRawTotal <= 0.0) continue

                val shareForThisParent = if (levelFactorSum > 0.0)
                    parentBudget * (levelFactors[idx] / levelFactorSum)
                else 0.0

                val assigned = shareForThisParent.coerceAtMost(remainingBudget)
                val scale = assigned / parentRawTotal

                choices.ensureCapacity(choices.size + parentBlocks.size)
                for (entry in parentBlocks) {
                    val w = entry.weight.toDouble() * scale
                    if (w > 0.0) {
                        choices += WeightedBlock(entry.blockData, w, parent.id)
                    }
                }
                remainingBudget -= assigned
            }

            if (choices.isEmpty()) {
                choices += WeightedBlock.dirt()
            }

            choices.trim()
            val total = choices.sumOf { it.weight }
            println(
                "[OneBlock] Phase '$id' block choices built (ownTotal=$ownTotal, parentBudget=${
                    "%.3f".format(
                        parentBudget
                    )
                }, finalTotal=${"%.3f".format(total)}):"
            )
            choices.forEach {
                val p = if (total > 0) it.weight / total * 100.0 else 0.0
                println(
                    "  - ${it.data.material} (from=${it.phaseId}) w=${"%.3f".format(it.weight)}  ~ ${
                        "%.2f".format(
                            p
                        )
                    }%"
                )
            }
            return choices
        }

        data class WeightedBlock(
            val data: BlockData,
            override val weight: Double,
            val phaseId: String
        ) : Weighted {
            companion object {
                fun dirt() = WeightedBlock(
                    BlockType.DIRT.createBlockData(),
                    1.0,
                    config.firstPhase().id
                )
            }
        }
    }

    fun currentPhase(totalMined: Long): Phase = phases.last { totalMined >= it.startsAt }

    fun findById(id: String): Phase? = phases.find { it.id == id }

    fun firstPhase(): Phase = phases.minBy { it.startsAt }

    companion object Holder {
        private val manager: SpongeConfigManager<PhaseConfig>

        init {
            surfConfigApi.createSpongeYmlConfig<PhaseConfig>(plugin.dataPath, "phases.yml")
            manager = surfConfigApi.getSpongeConfigManagerForConfig(PhaseConfig::class.java)
        }

        val config: PhaseConfig
            get() = manager.config

        fun reloadFromFile() = manager.reloadFromFile()
    }
}


interface SimpleSelector<E> {
    fun pick(randomGenerator: RandomGenerator = RandomGenerator.getDefault()): E
}

class SimpleWeightedSelector<T : Weighted>(
    items: Iterable<T>
) : SimpleSelector<T> {

    private val elements: List<T>
    private val cumulative: DoubleArray

    init {
        val tmp = items.toList()
        require(tmp.isNotEmpty()) { "Selector must have at least one element." }

        var cum = 0.0
        cumulative = DoubleArray(tmp.size)
        for (i in tmp.indices) {
            val w = tmp[i].weight
            require(w > 0.0) { "Weight must be > 0 (got $w at index $i)." }
            cum += w
            cumulative[i] = cum
        }
        elements = tmp
    }

    override fun pick(randomGenerator: RandomGenerator): T {
        val total = cumulative[cumulative.lastIndex]
        val r = randomGenerator.nextDouble(total)
        var lo = 0
        var hi = cumulative.lastIndex
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if (r <= cumulative[mid]) hi = mid else lo = mid + 1
        }
        return elements[lo]
    }
}


val phaseConfig: PhaseConfig get() = PhaseConfig.config