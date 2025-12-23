package dev.slne.surf.event.base.paper.config

import dev.slne.surf.event.base.paper.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

class EventServerConfigHolder {
    private val configManager: SpongeConfigManager<EventServerConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            EventServerConfig::class.java,
            plugin.dataPath,
            "config.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            EventServerConfig::class.java
        )
        reload()
    }

    fun edit(actions: EventServerConfig.() -> Unit) {
        configManager.config = configManager.config.apply { actions() }
        configManager.save()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}