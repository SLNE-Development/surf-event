package dev.slne.surf.event.oneblock.db

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.batchUpsert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.update
import dev.slne.surf.event.oneblock.data.IslandDTO
import dev.slne.surf.event.oneblock.db.table.IslandTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*

@Suppress("DEPRECATION")
object IslandRepository {
    suspend fun createIslandForPlayer(
        uuid: UUID,
        x: Double,
        y: Double,
        z: Double,
        worldUuid: UUID
    ) = suspendTransaction {
        IslandTable.insert {
            it[ownerUuid] = uuid
            it[oneBlockX] = x
            it[oneBlockY] = y
            it[oneBlockZ] = z
            it[oneBlockWorld] = worldUuid
        }

        return@suspendTransaction IslandDTO(
            owner = uuid,
            oneBlock = Location(
                Bukkit.getWorld(worldUuid) ?: error("World not found"),
                x,
                y,
                z
            ),
            totalMined = 0L
        )
    }

    suspend fun findAll() = suspendTransaction {
        IslandTable.selectAll()
            .map { row ->
                IslandDTO(
                    owner = row[IslandTable.ownerUuid],
                    oneBlock = Location(
                        Bukkit.getWorld(row[IslandTable.oneBlockWorld]) ?: error("World not found"),
                        row[IslandTable.oneBlockX],
                        row[IslandTable.oneBlockY],
                        row[IslandTable.oneBlockZ],
                    ),
                    totalMined = row[IslandTable.totalMined]
                )
            }.toList()
    }


    suspend fun saveAll(dtos: List<IslandDTO>) = suspendTransaction {
        IslandTable.batchUpsert(dtos, shouldReturnGeneratedValues = false) { dto ->
            this[IslandTable.ownerUuid] = dto.owner
            this[IslandTable.oneBlockX] = dto.oneBlock.x
            this[IslandTable.oneBlockY] = dto.oneBlock.y
            this[IslandTable.oneBlockZ] = dto.oneBlock.z
            this[IslandTable.oneBlockWorld] = dto.oneBlock.world.uid
            this[IslandTable.totalMined] = dto.totalMined
        }
    }

    suspend fun updateProgress(uuid: UUID, totalMined: Long) = suspendTransaction {
        IslandTable.update({ IslandTable.ownerUuid eq uuid }) {
            it[this.totalMined] = totalMined
        }
    }

    suspend fun updatePosition(uuid: UUID, x: Double, y: Double, z: Double, worldUuid: UUID) =
        suspendTransaction {
            IslandTable.update({ IslandTable.ownerUuid eq uuid }) {
                it[oneBlockX] = x
                it[oneBlockY] = y
                it[oneBlockZ] = z
                it[oneBlockWorld] = worldUuid
            }
        }

}