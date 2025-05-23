package dev.slne.surf.event.playtimechecker.config

import dev.slne.surf.event.playtimechecker.config.PlaytimeCheckerConfig.PlaytimeCheck
import dev.slne.surf.event.playtimechecker.plugin
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.synchronize
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
    private val configManager: SpongeConfigManager<PlaytimeCheckerConfig>
    private val cache = mutableObject2ObjectMapOf<String, PlaytimeCheck>().synchronize()

    init {
        surfConfigApi.createSpongeYmlConfig<PlaytimeCheckerConfig>(plugin.datapath, "config.yml")
        configManager =
            surfConfigApi.getSpongeConfigManagerForConfig(PlaytimeCheckerConfig::class.java)
        reloadFromFile()
    }

    fun reloadFromFile() {
        configManager.reloadFromFile()
        cache.clear()
        configManager.config.playtimeChecks
            .filter { it.enabled }
            .forEach { cache[it.server] = it }
    }

    fun getPlaytimeCheck(server: String): PlaytimeCheck? = cache[server]

    fun addPlaytimeCheck(server: String, category: String, maxPlaytime: Long) {
        val newCheck = PlaytimeCheck(server, category, maxPlaytime)
        cache[server] = newCheck
        with(configManager.config.playtimeChecks) {
            removeIf { it.server == server }
            add(newCheck)
        }
        configManager.save()
    }

    fun removePlaytimeCheck(server: String): Boolean {
        val removed = cache.remove(server) != null
        if (removed) {
            configManager.config.playtimeChecks.removeIf { it.server == server }
            configManager.save()
        }
        return removed
    }

    fun getPlaytimeChecksServers(): List<String> = cache.keys.toList()
}