package dev.slne.surf.event.oneblock.db

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.event.oneblock.data.PlayerStateDTO
import dev.slne.surf.event.oneblock.db.table.PlayerStateTable
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

object PlayerStateRepository {
    suspend fun findByUuid(uuid: UUID) = suspendTransaction {
        PlayerStateTable.selectAll().where(PlayerStateTable.playerUuid eq uuid).firstOrNull()
            ?.let { row ->
                PlayerStateDTO(
                    uuid = row[PlayerStateTable.playerUuid],
                    relocating = row[PlayerStateTable.relocating],
                    relocateTimestamp = row[PlayerStateTable.relocateTimestamp]
                )

            }
    }

    suspend fun flushState(dto: PlayerStateDTO) = suspendTransaction {
        PlayerStateTable.upsert {
            it[playerUuid] = dto.uuid
            it[relocating] = dto.relocating
            it[relocateTimestamp] = dto.relocateTimestamp
        }
    }
}