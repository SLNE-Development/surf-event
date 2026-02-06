package dev.slne.surf.event.collectit.display.outline.type

import dev.slne.surf.event.collectit.display.AcquiredState
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Location

abstract class AbstractDisplayOutlineType(
    override val baseLocation: Location,
    override val radius: Int,
    override val baseHeight: Int,
    override var acquiredState: AcquiredState
) : DisplayOutlineType {
    init {
        require(baseHeight > 0) {
            "Base height must be greater than 0, but was $baseHeight"
        }

        require(baseHeight < radius) {
            "Base height must be smaller than radius, but was $baseHeight"
        }
    }

    override val containerCenter: Location
        get() = Location(
            baseLocation.world,
            baseLocation.x,
            baseLocation.y + baseHeight + (radius / 2),
            baseLocation.z
        )

    override fun setOutline(): ObjectList<Location> {
        val locations = mutableObjectListOf<Location>()

        locations.addAll(setBase())
        locations.addAll(setContainer())

        return locations
    }

    override fun changeAcquiredState(state: AcquiredState): ObjectList<Location> {
        acquiredState = state

        return setOutline()
    }
}