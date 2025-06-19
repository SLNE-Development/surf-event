package dev.slne.surf.event.hardcore.sound

import dev.slne.surf.surfapi.bukkit.api.util.dispatcher
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayerInRegion
import dev.slne.surf.surfapi.bukkit.api.util.key
import dev.slne.surf.surfapi.core.api.messages.adventure.sound
import kotlinx.coroutines.withContext
import net.kyori.adventure.sound.Sound.Source
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

object SoundManager {
    private val key = key("sound_state")
    private val sound = sound {
        type(Sound.ENTITY_LIGHTNING_BOLT_THUNDER)
        source(Source.MASTER)
    }

    suspend fun broadcastDeathSound() = forEachPlayerInRegion({ player ->
        if (getSoundStateUnsafe(player)) {
            player.playSound(sound)
        }
    }, concurrent = true)

    private fun getSoundStateUnsafe(player: Player): Boolean {
        return player.persistentDataContainer.getOrDefault(key, PersistentDataType.BOOLEAN, true)
    }

    suspend fun getSoundState(player: Player) = withContext(player.dispatcher()) {
        getSoundStateUnsafe(player)
    }
}