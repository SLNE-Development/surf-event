package dev.slne.surf.event.randomdrops.db.entities

import dev.slne.surf.event.randomdrops.db.tables.PlayerBlockDropsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BlockDropEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<BlockDropEntity>(PlayerBlockDropsTable)
    var uuid by PlayerBlockDropsTable.uuid
    var originalKey by PlayerBlockDropsTable.originalKey
    var replacedKey by PlayerBlockDropsTable.replacedKey
}