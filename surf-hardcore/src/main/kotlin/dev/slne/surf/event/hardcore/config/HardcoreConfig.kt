package dev.slne.surf.event.hardcore.config

import dev.slne.surf.event.hardcore.plugin
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class HardcoreConfig(
    val lavaCheckRadius: Double = 10.0,
    val endWorldborderSideLength: Double = 100.0,
)

object HardcoreConfigHolder {
    private val manager: SpongeConfigManager<HardcoreConfig>
    val config: HardcoreConfig
        get() = manager.config

    init {
        surfConfigApi.createSpongeYmlConfig<HardcoreConfig>(plugin.dataPath, "confing.yml")
        manager = surfConfigApi.getSpongeConfigManagerForConfig(HardcoreConfig::class.java)
    }

    fun reload() {
        manager.reloadFromFile()
    }
}