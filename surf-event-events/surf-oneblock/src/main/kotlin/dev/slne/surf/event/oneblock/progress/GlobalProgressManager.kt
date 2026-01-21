package dev.slne.surf.event.oneblock.progress

import dev.slne.surf.event.oneblock.db.IslandService

object GlobalProgressManager {

    fun totalMinedBlocks() = IslandService.all().sumOf { it.totalMined }
}