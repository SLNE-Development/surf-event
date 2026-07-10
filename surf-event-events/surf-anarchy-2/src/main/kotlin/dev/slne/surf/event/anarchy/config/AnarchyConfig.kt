package dev.slne.surf.event.anarchy.config

import dev.slne.surf.api.core.config.SpongeYmlConfigClass
import dev.slne.surf.api.core.config.serializer.surfSpongeConfigSerializers
import dev.slne.surf.event.anarchy.config.finale.FinaleConfig
import dev.slne.surf.event.anarchy.config.serializer.ZonedDateTimeSerializer
import dev.slne.surf.event.anarchy.plugin
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class AnarchyConfig(
    val finaleConfig: FinaleConfig = FinaleConfig()
) {
    companion object : SpongeYmlConfigClass<AnarchyConfig>(
        AnarchyConfig::class.java,
        plugin.dataPath,
        "config.yml",
    ) {
        init {
            surfSpongeConfigSerializers.registerClassSerializer(ZonedDateTimeSerializer)
        }
    }
}