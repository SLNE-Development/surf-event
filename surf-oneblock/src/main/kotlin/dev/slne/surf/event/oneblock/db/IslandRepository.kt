package dev.slne.surf.event.oneblock.db

import dev.slne.surf.event.oneblock.db.entity.IslandEntity
import dev.slne.surf.event.oneblock.db.table.IslandTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.eq
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

    suspend fun updateProgress(uuid: UUID, level: Int, totalMined: Long) =
        newSuspendedTransaction(Dispatchers.IO) {
            IslandEntity.find { IslandTable.ownerUuid eq uuid }
                .forUpdate()
                .singleOrNull()?.apply {
                    this.level = level
                    this.totalMined = totalMined
                }
        }

    suspend fun updatePosition(uuid: UUID, x: Double, y: Double, z: Double, worldUuid: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            IslandEntity.find { IslandTable.ownerUuid eq uuid }
                .forUpdate()
                .singleOrNull()?.apply {
                    this.oneBlockX = x
                    this.oneBlockY = y
                    this.oneBlockZ = z
                    this.oneBlockWorld = worldUuid
                }
        }
}