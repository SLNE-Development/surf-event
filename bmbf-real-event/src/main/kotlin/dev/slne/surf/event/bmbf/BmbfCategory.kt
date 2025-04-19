package dev.slne.surf.event.bmbf

import com.plotsquared.core.PlotSquared
import com.plotsquared.core.plot.PlotAreaTerrainType
import com.plotsquared.core.plot.PlotAreaType
import com.plotsquared.core.setup.PlotAreaBuilder
import com.plotsquared.core.setup.SetupProcess
import com.plotsquared.core.util.SetupUtils
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
//        val world = WorldCreator.name(worldName).createWorld() ?: error("Error creating world: $worldName")
////        plotAPI.plotSquared.plotAreaManager.addWorld(worldName)
//        plotAPI.plotSquared.loadWorld(worldName, BukkitPlotGenerator(world.name, world.generator, plotAPI.plotSquared.plotAreaManager))

        val builder = PlotAreaBuilder.newBuilder().apply {
            worldName(worldName)
            areaName(worldName)
            plotAreaType(PlotAreaType.NORMAL)
            terrainType(PlotAreaTerrainType.NONE)
        }

        val setupUtils = PlotSquared.platform().setupUtils()
        val finalWorldName = setupUtils.setupWorld(builder)
    }

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
//        private val setupUtils =
//            PlotSquared.platform().injector().getInstance(SetupUtils::class.java)

        fun createAreas() {
            for (category in entries) {
                category.createAreas()
            }
        }
    }
}