package dev.slne.surf.event.oneblock.db.table

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object IslandTable: LongIdTable("islands") {
    val ownerUuid = uuid("owner_uuid").uniqueIndex()
    val oneBlockX = double("oneblock_x")
    val oneBlockY = double("oneblock_y")
    val oneBlockZ = double("oneblock_z")
    val oneBlockWorld = uuid("oneblock_world")
    val level = integer("level").default(0)
    val totalMined = long("total_mined").default(0)
}