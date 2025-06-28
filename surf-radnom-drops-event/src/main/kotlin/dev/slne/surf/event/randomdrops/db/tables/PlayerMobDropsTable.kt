package dev.slne.surf.event.randomdrops.db.tables

import org.bukkit.entity.EntityType
import org.jetbrains.exposed.dao.id.LongIdTable

object PlayerMobDropsTable: LongIdTable("player_mob_drops") {
    val uuid = uuid("uuid").uniqueIndex()
    val originalType = enumerationByName<EntityType>("original_type", 255)
    val replacedType = enumerationByName<EntityType>("replaced_type", 255)

    init {
        uniqueIndex(uuid, originalType)
    }
}