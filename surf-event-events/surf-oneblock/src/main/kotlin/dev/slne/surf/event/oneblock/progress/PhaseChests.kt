package dev.slne.surf.event.oneblock.progress

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import dev.slne.surf.surfapi.core.api.random.RandomSelector
import dev.slne.surf.surfapi.core.api.random.Weighted
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class PhaseChests(
    val chests: MutableList<ChestEntry> = mutableListOf(),
) {
    val chestSelectors = Caffeine.newBuilder()
        .build<String, RandomSelector<ChestEntry>?> { phaseId ->
            val chestEntries = chests.filter { phaseId in it.phases }
            if (chestEntries.isEmpty()) {
                null
            } else {
                RandomSelector.fromWeightedIterable(chestEntries)
            }
        }

    @ConfigSerializable
    data class ChestEntry(
        val name: String,
        val phases: MutableList<String> = mutableListOf(),
        val contentBase64: String,
        override val weight: Double
    ) : Weighted

    companion object {
        private val manager: SpongeConfigManager<PhaseChests>

        init {
            surfConfigApi.createSpongeYmlConfig<PhaseChests>(plugin.dataPath, "chests.yml")
            manager = surfConfigApi.getSpongeConfigManagerForConfig(PhaseChests::class.java)
        }

        val instance: PhaseChests
            get() = manager.config

        fun reloadFromFile() {
            manager.reloadFromFile()
        }

        fun saveToFile() {
            manager.save()
        }
    }
}

val phaseChests: PhaseChests
    get() = PhaseChests.instance
