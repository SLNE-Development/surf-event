package dev.slne.surf.event.oneblock.db.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object IslandTable : LongIdTable("event_oneblock_islands") {
    val ownerUuid = nativeUuid("owner_uuid").uniqueIndex()
    val oneBlockX = double("oneblock_x")
    val oneBlockY = double("oneblock_y")
    val oneBlockZ = double("oneblock_z")
    val oneBlockWorld = nativeUuid("oneblock_world")
    val totalMined = long("total_mined").default(0)
}