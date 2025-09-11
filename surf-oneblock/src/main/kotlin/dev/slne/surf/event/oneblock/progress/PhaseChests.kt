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
        .build<String, RandomSelector<ChestEntry>> { phaseId ->
            RandomSelector.fromWeightedIterable(chests.filter { phaseId in it.phases })
        }

    @ConfigSerializable
    data class ChestEntry(
        val name: String,
        val phases: MutableList<String> = mutableListOf(),
        val content: ByteArray,
        override val weight: Double
    ) : Weighted {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ChestEntry) return false

            if (weight != other.weight) return false
            if (name != other.name) return false
            if (phases != other.phases) return false
            if (!content.contentEquals(other.content)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = weight.hashCode()
            result = 31 * result + name.hashCode()
            result = 31 * result + phases.hashCode()
            result = 31 * result + content.contentHashCode()
            return result
        }
    }

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
