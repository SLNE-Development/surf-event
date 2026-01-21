package dev.slne.surf.event.oneblock.config

import dev.slne.surf.surfapi.bukkit.api.extensions.server
import org.bukkit.block.BlockType
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
data class OneBlockConfig(
    @Setting("island-placement")
    val islandPlacement: IslandPlacementConfig = IslandPlacementConfig(),
    @Setting("loot")
    val loot: LootConfig = LootConfig(),
    @Setting("progression")
    val progression: ProgressionConfig = ProgressionConfig(),
    @Setting("caching")
    val caching: CachingConfig = CachingConfig(),
    @Setting("global-goals")
    val globalGoals: GlobalGoalsConfig = GlobalGoalsConfig(),
    @Setting("relocate")
    val relocate: RelocateConfig = RelocateConfig(),
) {

    @ConfigSerializable
    data class IslandPlacementConfig(
        val spacing: Int = 100,
        val checkAround: Int = 10,
        val islandType: String = BlockType.GRASS_BLOCK.key().asString(),
        val tryCount: Int = 10,
        val minY: Int = 64,
        val maxY: Int = 200,
        val startX: Int = 0,
        val startZ: Int = 0,
    ) {
        val islandBlockData = server.createBlockData(islandType)

        init {
            require(spacing > 0) { "Spacing must be greater than 0" }
            require(checkAround in 0..16) { "Check around must be between 0 and 16" }
        }
    }

    @ConfigSerializable
    data class LootConfig(
        val chestSpawnChancePercentage: Int = 5,
        val mobSpawnChancePercentage: Int = 10,
    )

    @ConfigSerializable
    data class ProgressionConfig(
        val baseRequired: Int = 16,
        val multiplier: Int = 8
    )

    @ConfigSerializable
    data class CachingConfig(
        val flushActions: Int = 50,
        val flushOnWorldSave: Boolean = true
    )

    @ConfigSerializable
    data class GlobalGoalsConfig(
        val thresholds: List<Long> = listOf(1_000, 10_000, 100_000),
        val reward: String = "HASTE"
    )

    @ConfigSerializable
    data class RelocateConfig(
        val relocateRadius: Int = 5,
        val relocateCooldownSeconds: Int = 300,
    )

}