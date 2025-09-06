package dev.slne.surf.event.oneblock.data

import org.bukkit.Location
import java.util.UUID

data class IslandDTO(
    val owner: UUID,
    var oneBlock: Location,
    var level: Int = 0,
    var totalMined: Long = 0
)