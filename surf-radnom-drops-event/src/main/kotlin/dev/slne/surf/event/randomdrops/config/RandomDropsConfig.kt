package dev.slne.surf.event.randomdrops.config

import dev.slne.surf.event.randomdrops.plugin
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import net.kyori.adventure.key.Key
import org.bukkit.Registry
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

val config by lazy {
    surfConfigApi.createSpongeYmlConfig<RandomDropsConfig>(
        plugin.dataPath,
        "config.yml"
    )
}

@ConfigSerializable
data class RandomDropsConfig(
    @Setting("random-block-drop-filter")
    val randomBlockDropFilterRaw: List<String>
) {
    val randomBlockDropFilter by lazy {
        randomBlockDropFilterRaw.mapNotNull {
            Registry.ITEM.get(
                Key.key(
                    it
                )
            )
        }
    }
}
