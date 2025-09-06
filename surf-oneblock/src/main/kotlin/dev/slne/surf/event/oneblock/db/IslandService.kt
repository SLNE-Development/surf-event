package dev.slne.surf.event.oneblock.db

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.event.oneblock.data.IslandDTO
import org.bukkit.Location
import java.util.*

object IslandService {
    private val islands = Caffeine.newBuilder()
        .build<UUID, IslandDTO>()


    suspend fun fetchIslands() {
        IslandRepository.findAll().forEach { island ->
            islands.put(island.owner, island)
        }
    }

    fun hasIsland(uuid: UUID): Boolean = islands.getIfPresent(uuid) != null
    fun getIsland(uuid: UUID): IslandDTO? = islands.getIfPresent(uuid)

    fun getOwnerFromBlock(location: Location): UUID? {
        return islands.asMap().values.find { island -> island.oneBlock == location }?.owner
    }

    suspend fun createIslandForPlayer(uuid: UUID, oneBlockLocation: Location): IslandDTO {
        islands.getIfPresent(uuid)?.let { return it }

        val island = IslandRepository.createIslandForPlayer(
            uuid,
            oneBlockLocation.x,
            oneBlockLocation.y,
            oneBlockLocation.z,
            oneBlockLocation.world.uid
        )

        islands.put(uuid, island)

        return island
    }
}