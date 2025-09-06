package dev.slne.surf.event.oneblock.db.entity

import dev.slne.surf.event.oneblock.data.IslandDTO
import dev.slne.surf.event.oneblock.db.table.IslandTable
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import org.bukkit.Location
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

class IslandEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<IslandEntity>(IslandTable)

    var ownerUuid by IslandTable.ownerUuid
    var oneBlockX by IslandTable.oneBlockX
    var oneBlockY by IslandTable.oneBlockY
    var oneBlockZ by IslandTable.oneBlockZ
    var oneBlockWorld by IslandTable.oneBlockWorld
    var level by IslandTable.level
    var totalMined by IslandTable.totalMined

    fun toDTO() = IslandDTO(
        owner = ownerUuid,
        oneBlock = Location(
            server.getWorld(oneBlockWorld) ?: error("World with UUID $oneBlockWorld not found"),
            oneBlockX,
            oneBlockY,
            oneBlockZ
        ),
        level = level,
        totalMined = totalMined
    )
}