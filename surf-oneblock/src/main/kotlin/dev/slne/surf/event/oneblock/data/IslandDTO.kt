package dev.slne.surf.event.oneblock.data

import org.bukkit.Location
import java.util.*

data class IslandDTO(
    val owner: UUID,
    var oneBlock: Location,
    var totalMined: Long = 0
)