package dev.slne.surf.event.oneblock.progress

import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import dev.slne.surf.surfapi.core.api.random.Weighted
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.Object2DoubleLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.block.BlockType
import org.bukkit.block.data.BlockData
import org.bukkit.entity.EntityType
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting
import java.util.random.RandomGenerator

@ConfigSerializable
data class PhaseConfig(
    @Setting("phases")
    val unsortedPhases: List<Phase> = defaultPhases,
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
    data class ParentPhase(
        val id: String,
        val weightOverride: Int? = null
    )

    @ConfigSerializable
    data class Phase(
        val id: String,
        val displayName: String = id.replaceFirstChar { it.uppercaseChar() }.replace('_', ' '),
        val startsAt: Int,
        val weight: Int,
        val parents: List<ParentPhase>,
        val blocks: List<BlockEntry>,
        val entities: List<EntityEntry>
    ) {
        companion object {
            private const val PARENT_SHARE_PER_WEIGHT = 0.15
            private const val PARENT_SHARE_MAX = 0.60
            private const val PARENT_DECAY = 0.5
        }

        val entityChoices by lazy { buildEntityChoices() }
        val entitySelector by lazy {
            entityChoices.takeIf { it.isNotEmpty() }?.let { SimpleWeightedSelector(it) }
        }

        val blockChoices by lazy { buildChoices() }
        val blockSelector by lazy { SimpleWeightedSelector(blockChoices) }

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
            val levelFactors = parents.map {
                val f = levelFactor
                levelFactor *= PARENT_DECAY
                f
            }

            val effectiveParentWeights = parents.mapIndexed { idx, parentPhase ->
                val override = parentPhase.weightOverride?.toDouble()
                (levelFactors[idx]) * (override ?: 1.0)
            }

            val totalParentWeight = effectiveParentWeights.sum()

            for ((idx, parentPhase) in parents.withIndex()) {
                if (remainingBudget <= 1e-9) break
                val parent = config.findById(parentPhase.id) ?: continue
                val parentBlocks = parent.blocks
                if (parentBlocks.isEmpty()) continue

                val shareForThisParent =
                    if (totalParentWeight > 0.0)
                        parentBudget * (effectiveParentWeights[idx] / totalParentWeight)
                    else 0.0

                val assigned = shareForThisParent.coerceAtMost(remainingBudget)
                val scale = assigned / parentBlocks.sumOf { it.weight.toDouble() }

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
            return choices
        }

        private fun buildEntityChoices(): ObjectList<EntityEntry> {
            val agg = Object2DoubleLinkedOpenHashMap<EntityType>(entities.size)

            for (e in entities) {
                agg.mergeDouble(e.type, e.weight) { a, b -> a + b }
            }
            val ownTotal = agg.values.sumOf { it }

            val extraSteps = (this.weight - 1).coerceAtLeast(0)
            val parentShare = (extraSteps * PARENT_SHARE_PER_WEIGHT).coerceIn(0.0, PARENT_SHARE_MAX)
            val parentBudget = (if (ownTotal > 0.0) ownTotal else 1.0) * parentShare
            var remainingBudget = parentBudget

            var levelFactor = 1.0
            val levelFactors = parents.map {
                val f = levelFactor
                levelFactor *= PARENT_DECAY
                f
            }

            val effectiveParentWeights = parents.mapIndexed { idx, parentPhase ->
                val override = parentPhase.weightOverride?.toDouble()
                (levelFactors[idx]) * (override ?: 1.0)
            }

            val totalParentWeight = effectiveParentWeights.sum()

            for ((idx, parentPhase) in parents.withIndex()) {
                if (remainingBudget <= 1e-9) break
                val parent = config.findById(parentPhase.id) ?: continue
                val parentEntities = parent.entities
                if (parentEntities.isEmpty()) continue

                val shareForThisParent =
                    if (totalParentWeight > 0.0)
                        parentBudget * (effectiveParentWeights[idx] / totalParentWeight)
                    else 0.0

                val assigned = shareForThisParent.coerceAtMost(remainingBudget)
                val parentTotal = parentEntities.sumOf { it.weight }
                if (parentTotal <= 0.0) continue
                val scale = assigned / parentTotal

                for (e in parentEntities) {
                    val w = e.weight * scale
                    if (w > 0.0) {
                        agg.mergeDouble(e.type, w) { a, b -> a + b }
                    }
                }
                remainingBudget -= assigned
            }

            val out = mutableObjectListOf<EntityEntry>(agg.size)
            for ((type, w) in agg) {
                out += EntityEntry(type, w)
            }

            return out
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