package dev.slne.surf.event.oneblock.db.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable

object PlayerStateTable : UUIDTable("event_oneblock_player_state") {
    val playerUuid = nativeUuid("player_uuid").uniqueIndex()
    val relocating = bool("relocating").default(false)
    val relocateTimestamp = long("relocate_timestamp").default(0L)
}