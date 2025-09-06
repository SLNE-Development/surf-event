package dev.slne.surf.event.oneblock.db.table

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable

object PlayerStateTable: UUIDTable("player_state") {
    val relocating = bool("relocating").default(false)
}