package dev.slne.surf.event.randomdrops.config

import dev.slne.surf.event.randomdrops.plugin
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import dev.slne.surf.surfapi.core.api.util.toObjectSet
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

enum class RandomizationScope { PLAYER, WORLD, GLOBAL }

@ConfigSerializable
data class RandomDropsConfig(
    @Setting("randomization-scope")
    val randomizationScope: RandomizationScope = RandomizationScope.PLAYER,

    @Setting("random-block-drop-filter")
    val randomBlockDropFilterRaw: List<String> = emptyList()
) {
    val randomBlockDropFilter by lazy {
        randomBlockDropFilterRaw.mapNotNull { raw -> Registry.ITEM.get(Key.key(raw)) }.toObjectSet()
    }
}
