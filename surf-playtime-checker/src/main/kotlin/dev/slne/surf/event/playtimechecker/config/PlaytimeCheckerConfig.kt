package dev.slne.surf.event.playtimechecker.config

import dev.slne.surf.event.playtimechecker.plugin
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class PlaytimeCheckerConfig(
    val playtimeChecks: MutableList<PlaytimeCheck> = mutableListOf(),
) {

    @ConfigSerializable
    data class PlaytimeCheck(
        val server: String,
        val category: String,
        val maxPlaytime: Long,
        val enabled: Boolean = true,
    )
}

object PlaytimeCheckerConfigManager {
    val configManager: SpongeConfigManager<PlaytimeCheckerConfig>

    init {
        surfConfigApi.createSpongeYmlConfig<PlaytimeCheckerConfig>(plugin.datapath, "config.yml")
        configManager =
            surfConfigApi.getSpongeConfigManagerForConfig(PlaytimeCheckerConfig::class.java)
    }

    fun reloadFromFile() {
        configManager.reloadFromFile()
    }

    fun getPlaytimeCheck(server: String): PlaytimeCheckerConfig.PlaytimeCheck? {
        return configManager.config.playtimeChecks.find { it.server == server }
    }

    fun addPlaytimeCheck(server: String, category: String, maxPlaytime: Long) {
        val newCheck = PlaytimeCheckerConfig.PlaytimeCheck(server, category, maxPlaytime)
        val playtimeChecks = configManager.config.playtimeChecks
        playtimeChecks.removeIf { it.server == server }
        playtimeChecks.add(newCheck)
        configManager.save()
    }

    fun removePlaytimeCheck(server: String): Boolean {
        return configManager.config.playtimeChecks.removeIf { it.server == server }
            .also { removed ->
                if (removed) {
                    configManager.save()
                }
            }
    }

    fun getPlaytimeChecksServers(): List<String> {
        return configManager.config.playtimeChecks.map { it.server }
    }
}