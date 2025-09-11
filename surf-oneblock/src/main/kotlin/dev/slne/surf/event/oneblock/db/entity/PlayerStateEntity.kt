package dev.slne.surf.event.oneblock.db.entity

import dev.slne.surf.event.oneblock.data.PlayerStateDTO
import dev.slne.surf.event.oneblock.db.table.PlayerStateTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.*

class PlayerStateEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PlayerStateEntity>(PlayerStateTable) {
        fun createFromDTO(dto: PlayerStateDTO) = new(dto.uuid) {
            this.relocating = dto.relocating
            this.relocateTimestamp = dto.relocateTimestamp
        }
    }

    var relocating by PlayerStateTable.relocating
    var relocateTimestamp by PlayerStateTable.relocateTimestamp

    fun toDTO() = PlayerStateDTO(
        id.value,
        relocating = relocating,
        relocateTimestamp = relocateTimestamp,
    )

    fun applyDTO(dto: PlayerStateDTO) {
        relocating = dto.relocating
        relocateTimestamp = dto.relocateTimestamp
    }
}