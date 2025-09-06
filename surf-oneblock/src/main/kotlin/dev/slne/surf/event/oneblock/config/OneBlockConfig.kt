package dev.slne.surf.event.oneblock.config

import dev.slne.surf.surfapi.bukkit.api.extensions.server
import org.bukkit.block.BlockType
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
data class OneBlockConfig(
    @Setting("island-placement")
    val islandPlacement: IslandPlacementConfig = IslandPlacementConfig(),
) {

    data class IslandPlacementConfig(
        val spacing: Int = 100,
        val checkAround: Int = 10,
        val islandType: String = BlockType.GRASS_BLOCK.key().asString(),
        val tryCount: Int = 10,
        val yLevel: Int = 100,
        val startX: Int = 0,
        val startZ: Int = 0,
    ) {
        val islandBlockData = server.createBlockData(islandType)

        init {
            require(spacing > 0) { "Spacing must be greater than 0" }
            require(checkAround in 0..16) { "Check around must be between 0 and 16" }
        }
    }
}