package dev.slne.surf.event.collectit.display.outline.type

import dev.slne.surf.event.collectit.display.AcquiredState
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Location

interface DisplayOutlineType {
    val baseLocation: Location
    
    val baseHeight: Int
    val radius: Int

    val acquiredState: AcquiredState
    fun changeAcquiredState(state: AcquiredState): ObjectList<Location>

    fun setBase(): ObjectList<Location>
    fun setContainer(): ObjectList<Location>

    fun setOutline(): ObjectList<Location>

    val containerCenter: Location
}