package dev.slne.surf.event.oneblock.db

import dev.slne.surf.event.oneblock.data.PlayerStateDTO
import dev.slne.surf.event.oneblock.db.entity.PlayerStateEntity
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import java.util.*

@Suppress("DEPRECATION")
object PlayerStateRepository {
    suspend fun findByUuid(uuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        PlayerStateEntity.findById(uuid)?.toDTO()
    }

    suspend fun flushState(dto: PlayerStateDTO) = newSuspendedTransaction(Dispatchers.IO) {
        val existing = PlayerStateEntity.findByIdAndUpdate(dto.uuid) { it.applyDTO(dto) }
        if (existing == null) {
            PlayerStateEntity.createFromDTO(dto)
        }
    }
}