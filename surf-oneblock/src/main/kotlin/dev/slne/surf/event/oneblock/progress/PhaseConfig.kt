package dev.slne.surf.event.oneblock.progress

import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.event.oneblock.progress.PhaseConfig.Phase
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import dev.slne.surf.surfapi.core.api.random.RandomSelector
import dev.slne.surf.surfapi.core.api.random.Weighted
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.block.BlockType
import org.bukkit.block.data.BlockData
import org.bukkit.entity.EntityType
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

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
            require(weight in 0.0..100.0) { "Chance percentage must be between 0 and 100" }
            require(type.isSpawnable) { "Entity type must be spawnable" }
        }
    }

    @ConfigSerializable
    data class Phase(
        val id: String,
        val startsAt: Int,
        val weight: Int,
        val parents: List<String>,
        val blocks: List<BlockEntry>,
        val entities: List<EntityEntry>
    ) {
        val entitySelector by lazy {
            entities.takeIf { entities.isNotEmpty() }?.let { entities ->
                RandomSelector.fromWeightedIterable(entities)
            }
        }

        val blockChoices by lazy {
            println("[OneBlock] Building block choices for phase '$id'...")
            buildChoices()
        }
        val blockSelector by lazy {

            println("[OneBlock] Phase '$id' has ${blockChoices.size} block choices.")
            RandomSelector.fromWeightedIterable(blockChoices)
        }

        private fun buildChoices(): ObjectList<WeightedBlock> {
            val choices = mutableObjectListOf<WeightedBlock>()

            choices.ensureCapacity(blocks.size)
            for (entry in this.blocks) {
                choices += WeightedBlock(entry.blockData, entry.weight.toDouble(), this.id)
            }

            var carry = this.weight - 1
            for (parentId in this.parents) {
                val parent = config.findById(parentId) ?: continue
                val parentBlocks = parent.blocks
                val weight = maxOf(1, carry)

                choices.ensureCapacity(choices.size + parentBlocks.size)
                for (entry in parentBlocks) {
                    choices += WeightedBlock(entry.blockData, weight.toDouble(), parent.id)
                }

                carry = maxOf(1, carry - 1)
            }

            if (choices.isEmpty()) {
                choices += WeightedBlock.dirt()
            }

            choices.trim()
            println("[OneBlock] Phase '$id' block choices built: $choices")
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

val phaseConfig: PhaseConfig get() = PhaseConfig.config