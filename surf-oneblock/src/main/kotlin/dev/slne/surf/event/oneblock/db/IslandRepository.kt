package dev.slne.surf.event.oneblock.db

import dev.slne.surf.event.oneblock.db.entity.IslandEntity
import dev.slne.surf.surfapi.core.api.messages.DefaultFontInfo
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import java.util.*

@Suppress("DEPRECATION")
object IslandRepository {
    suspend fun createIslandForPlayer(
        uuid: UUID,
        x: Double,
        y: Double,
        z: Double,
        worldUuid: UUID
    ) = newSuspendedTransaction(Dispatchers.IO) {
        IslandEntity.new {
            ownerUuid = uuid
            oneBlockX = x
            oneBlockY = y
            oneBlockZ = z
            oneBlockWorld = worldUuid
        }.toDTO()
    }

    suspend fun findAll() = newSuspendedTransaction(Dispatchers.IO) {
        IslandEntity.all().map { it.toDTO() }
    }
}