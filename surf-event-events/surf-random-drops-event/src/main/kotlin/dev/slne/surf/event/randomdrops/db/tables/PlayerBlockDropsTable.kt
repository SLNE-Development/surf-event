package dev.slne.surf.event.randomdrops.db.tables

import net.kyori.adventure.key.Key
import org.jetbrains.exposed.dao.id.LongIdTable

object PlayerBlockDropsTable: LongIdTable("player_block_drops") {
    val uuid = uuid("uuid")
    val originalKey = varchar("original_key", 255).transform(
        wrap = { Key.key(it) },
        unwrap = { it.asString() }
    )

    val replacedKey = varchar("replaced_key", 255).transform(
        wrap = { Key.key(it) },
        unwrap = { it.asString() }
    )

    init {
        uniqueIndex(uuid, originalKey)
    }
}