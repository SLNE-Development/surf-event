package dev.slne.surf.event.oneblock.db.entity

import dev.slne.surf.event.oneblock.db.table.PlayerStateTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.*

class PlayerStateEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PlayerStateEntity>(PlayerStateTable)

    var relocating by PlayerStateTable.relocating
}