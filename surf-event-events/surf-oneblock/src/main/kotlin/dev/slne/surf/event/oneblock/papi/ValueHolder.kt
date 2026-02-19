package dev.slne.surf.event.oneblock.papi

import dev.slne.surf.event.oneblock.db.IslandService
import java.util.*

object ValueHolder {
    fun getUuid(place: Int) = IslandService.all()
        .sortedByDescending { it.totalMined }
        .getOrNull(place - 1)
        ?.owner

    fun getPlace(uuid: UUID) = IslandService.all()
        .sortedByDescending { it.totalMined }
        .indexOfFirst { it.owner == uuid }
        .takeIf { it != -1 }
        ?.plus(1)

    fun getMined(uuid: UUID) = IslandService.all()
        .firstOrNull { it.owner == uuid }
        ?.totalMined
}