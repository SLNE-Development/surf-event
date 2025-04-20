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
    FUSION_POWER_PLANT("Fusionskraftwerk", "fusion-power"),
    DAM("Staudamm", "dam"),
    JUMP_AND_RUN("Jump 'n' Run", "jump-and-run"),
    SKYBLOCK_ISLAND("Skyblock-Insel", "skyblock"),
    DOG("Hund", "dog"),
    EIFFEL_TOWER("Eiffelturm", "eiffel-tower");

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
                        createConfigNode("plot.height", ConfigurationUtil.INTEGER, 62),
                        createConfigNode("plot.size", ConfigurationUtil.INTEGER, 48),
                        createConfigNode(
                            "plot.filling",
                            ConfigurationUtil.BLOCK_BUCKET,
                            BlockBucket(BlockTypes.STONE!!)
                        ),
                        createConfigNode("wall.place_top_block", ConfigurationUtil.BOOLEAN, true),
                        createConfigNode(
                            "plot.floor",
                            ConfigurationUtil.BLOCK_BUCKET,
                            BlockBucket(BlockTypes.GRASS_BLOCK!!)
                        ),
                        createConfigNode(
                            "wall.block",
                            ConfigurationUtil.BLOCK_BUCKET,
                            BlockBucket(BlockTypes.STONE_SLAB!!)
                        ),
                        createConfigNode(
                            "wall.block_claimed",
                            ConfigurationUtil.BLOCK_BUCKET,
                            BlockBucket(BlockTypes.SANDSTONE_SLAB!!)
                        ),
                        createConfigNode("road.width", ConfigurationUtil.INTEGER, 16),
                        createConfigNode("road.height", ConfigurationUtil.INTEGER, 62),
                        createConfigNode(
                            "road.block",
                            ConfigurationUtil.BLOCK_BUCKET,
                            BlockBucket(BlockTypes.QUARTZ_BLOCK!!)
                        ),
                        createConfigNode(
                            "wall.filling",
                            ConfigurationUtil.BLOCK_BUCKET,
                            BlockBucket(BlockTypes.STONE!!)
                        ),
                        createConfigNode("wall.height", ConfigurationUtil.INTEGER, 62),
                        createConfigNode("plot.bedrock", ConfigurationUtil.BOOLEAN, true),
                        createConfigNode(
                            "world.component_below_bedrock",
                            ConfigurationUtil.BOOLEAN,
                            false
                        )
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
            FUSION_POWER_PLANT -> DAM
            DAM -> JUMP_AND_RUN
            JUMP_AND_RUN -> SKYBLOCK_ISLAND
            SKYBLOCK_ISLAND -> DOG
            DOG -> EIFFEL_TOWER
            EIFFEL_TOWER -> null
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