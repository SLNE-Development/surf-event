package dev.slne.surf.event.oneblock.db

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.event.oneblock.config.OneBlockConfigHolder.config
import dev.slne.surf.event.oneblock.data.IslandDTO
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.surfapi.core.api.util.toObjectList
import org.bukkit.Location
import org.gradle.internal.impldep.jcifs.util.LogStream.level
import java.util.*

object IslandService {
    private val islands = Caffeine.newBuilder()
        .build<UUID, IslandDTO>()

    suspend fun fetchIslands() {
        IslandRepository.findAll().forEach { island ->
            islands.put(island.owner, island)
        }
    }

    fun all() = islands.asMap().values.toObjectList()

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

    fun incrementMined(uuid: UUID): IslandDTO? {
        val dto = islands.getIfPresent(uuid) ?: return null
        dto.totalMined += 1
        dto.unsavedActions += 1

        if (dto.unsavedActions >= config.caching.flushActions) flush(uuid)

        return dto
    }

    fun updateOneBlockLocation(uuid: UUID, oneBlockLocation: Location) {
        val dto = islands.getIfPresent(uuid) ?: return
        dto.oneBlock = oneBlockLocation
        flushPosition(uuid)
    }

    fun flush(uuid: UUID) {
        val dto = islands.getIfPresent(uuid) ?: return
        val mined = dto.totalMined

        plugin.launch {
            IslandRepository.updateProgress(uuid, mined)
        }

        dto.unsavedActions = 0
    }

    fun flushPosition(uuid: UUID) {
        val dto = islands.getIfPresent(uuid) ?: return
        val loc = dto.oneBlock
        plugin.launch {
            IslandRepository.updatePosition(uuid, loc.x, loc.y, loc.z, loc.world.uid)
        }
    }

    fun flushAll() {
        islands.asMap().forEach { (uuid, dto) ->
            if (dto.unsavedActions > 0) flush(uuid)
        }
    }
}