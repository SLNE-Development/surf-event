package dev.slne.surf.event.oneblock.db

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.event.oneblock.data.PlayerStateDTO
import java.util.*
import kotlin.time.Duration.Companion.minutes

object PlayerStateService {
    private val states = Caffeine.newBuilder()
        .expireAfterWrite(30.minutes)
        .asLoadingCache<UUID, PlayerStateDTO> { uuid ->
            PlayerStateRepository.findByUuid(uuid) ?: PlayerStateDTO.empty(uuid)
        }

    suspend fun getState(uuid: UUID): PlayerStateDTO = states.get(uuid)

    suspend fun flushState(state: PlayerStateDTO) {
        PlayerStateRepository.flushState(state)
    }
}