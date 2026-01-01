package dev.slne.surf.event.randomdrops.db.entities

import dev.slne.surf.event.randomdrops.db.tables.PlayerMobDropsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MobDropEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MobDropEntity>(PlayerMobDropsTable)

    var uuid by PlayerMobDropsTable.uuid
    var originalType by PlayerMobDropsTable.originalType
    var replacedType by PlayerMobDropsTable.replacedType
}