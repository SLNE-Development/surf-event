package dev.slne.surf.event.oneblock.data

import java.util.UUID

data class PlayerStateDTO(
    val uuid: UUID,
    var relocating: Boolean = false,
    var relocateTimestamp: Long = 0L,
) {
    companion object {
        fun empty(uuid: UUID) = PlayerStateDTO(
            uuid = uuid,
        )
    }
}