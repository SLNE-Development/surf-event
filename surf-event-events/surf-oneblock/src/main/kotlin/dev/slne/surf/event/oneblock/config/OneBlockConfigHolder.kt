package dev.slne.surf.event.oneblock.config

import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

object OneBlockConfigHolder {
    private val manager: SpongeConfigManager<OneBlockConfig>

    init {
        surfConfigApi.createSpongeYmlConfig<OneBlockConfig>(plugin.dataPath, "config.yml")
        manager = surfConfigApi.getSpongeConfigManagerForConfig(OneBlockConfig::class.java)
    }

    val config: OneBlockConfig
        get() = manager.config

    fun save() {
        manager.save()
    }

    fun reloadFromFile() {
        manager.reloadFromFile()
    }
}

val config get() = OneBlockConfigHolder.config