package dev.slne.surf.event.bmbf

import com.plotsquared.core.PlotSquared
import com.plotsquared.core.configuration.ConfigurationNode
import com.plotsquared.core.configuration.ConfigurationUtil
import com.plotsquared.core.configuration.caption.StaticCaption
import com.plotsquared.core.plot.BlockBucket
import com.plotsquared.core.plot.PlotAreaTerrainType
import com.plotsquared.core.plot.PlotAreaType
import com.plotsquared.core.setup.PlotAreaBuilder
import com.plotsquared.core.setup.SettingsNodesWrapper
import com.sk89q.worldedit.world.block.BlockTypes
import dev.slne.surf.surfapi.core.api.util.logger

enum class BmbfCategory(val displayName: String, val plotAreaPrefix: String) {
    HOUSE("Haus", "house"),
    JUMP_AND_RUN("Jump 'n' Run", "jump-and-run"),
    WATER_PARK("Wasserpark", "water-park");

    private val log = logger()

    fun getPlotArea(challenge: BmbfChallenge) = plotAPI.plotSquared.plotAreaManager.getPlotArea(
        plotAreaPrefix + challenge.plotAreaSuffix,
        null
    ) ?: error("PlotArea not found: $plotAreaPrefix${challenge.plotAreaSuffix}")

    fun createAreas() {
        for (challenge in BmbfChallenge.entries) {
            val worldName = "$plotAreaPrefix${challenge.plotAreaSuffix}"
            setupArea(worldName)
        }
    }

    private fun setupArea(worldName: String) {
        log.atInfo()
            .log("Creating world for plot area: $worldName")
        val builder = PlotAreaBuilder.newBuilder().apply {
            generatorName("PlotSquared")
            plotAreaType(PlotAreaType.NORMAL)
            plotManager(generatorName())
            settingsNodesWrapper(
                SettingsNodesWrapper(
                    arrayOf(
                        createConfigNode("plot.height", ConfigurationUtil.INTEGER, -1),
                        createConfigNode("plot.size", ConfigurationUtil.INTEGER, 44),
                        createConfigNode("plot.filling", ConfigurationUtil.BLOCK_BUCKET, BlockBucket(BlockTypes.AIR!!)
                        ),
                        createConfigNode("plot.floor", ConfigurationUtil.BLOCK_BUCKET, BlockBucket(BlockTypes.GRAY_CONCRETE!!)
                        ),
                        createConfigNode("plot.bedrock", ConfigurationUtil.BOOLEAN, false),
                        createConfigNode("plot.create_signs", ConfigurationUtil.BOOLEAN, false),

                        createConfigNode("wall.place_top_block", ConfigurationUtil.BOOLEAN, true),
                        createConfigNode("wall.block", ConfigurationUtil.BLOCK_BUCKET, BlockBucket(BlockTypes.WHITE_CONCRETE!!)
                        ),
                        createConfigNode("wall.block_claimed", ConfigurationUtil.BLOCK_BUCKET, BlockBucket(BlockTypes.RED_CONCRETE!!)
                        ),
                        createConfigNode("wall.filling", ConfigurationUtil.BLOCK_BUCKET, BlockBucket(BlockTypes.AIR!!)
                        ),
                        createConfigNode("wall.height", ConfigurationUtil.INTEGER, -2),

                        createConfigNode("road.width", ConfigurationUtil.INTEGER, 16),
                        createConfigNode("road.height", ConfigurationUtil.INTEGER, -1),
                        createConfigNode("road.block", ConfigurationUtil.BLOCK_BUCKET, BlockBucket(BlockTypes.AIR!!)
                        ),

                        createConfigNode("world.component_below_bedrock", ConfigurationUtil.BOOLEAN, false)
                    ), null
                )
            )

            areaName(worldName)
            terrainType(PlotAreaTerrainType.NONE)
            worldName(worldName)
        }

        val setupUtils = PlotSquared.platform().setupUtils()
        setupUtils.setupWorld(builder)
    }

    private fun <T> createConfigNode(
        key: String,
        type: ConfigurationUtil.SettingValue<T>,
        defaultValue: T
    ) = ConfigurationNode(key, defaultValue, StaticCaption.of(""), type)


    fun nextCategory(): BmbfCategory? {
        return when (this) {
            HOUSE -> JUMP_AND_RUN
            JUMP_AND_RUN -> WATER_PARK
            WATER_PARK -> null
        }
    }

    companion object {
        fun createAreas() {
            for (category in entries) {
                category.createAreas()
            }
        }
    }
}