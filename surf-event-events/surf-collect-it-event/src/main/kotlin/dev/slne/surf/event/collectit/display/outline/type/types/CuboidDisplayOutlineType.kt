package dev.slne.surf.event.collectit.display.outline.type.types

import dev.slne.surf.event.collectit.display.AcquiredState
import dev.slne.surf.event.collectit.display.outline.type.AbstractDisplayOutlineType
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.util.BoundingBox

class CuboidDisplayOutlineType(
    baseLocation: Location,
    radius: Int,
    baseHeight: Int,
    acquiredState: AcquiredState
) : AbstractDisplayOutlineType(
    baseLocation = baseLocation,
    radius = radius,
    baseHeight = baseHeight,
    acquiredState = acquiredState,
) {
    private val boundingBox = BoundingBox(
        baseLocation.x - radius, baseLocation.y, baseLocation.z - radius,
        baseLocation.x + radius, baseLocation.y + radius, baseLocation.z + radius
    )

    override fun setBase(): ObjectList<Location> {
        val yStart = baseLocation.y
        val yEnd = yStart + baseHeight

        val locations = mutableObjectListOf<Location>()

        for (y in yStart.toInt()..yEnd.toInt()) {
            locations.addAll(walkAndSet(y.toDouble(), acquiredState.baseMaterial))
        }

        return locations
    }

    override fun setContainer(): ObjectList<Location> {
        val yStart = baseLocation.y + baseHeight
        val yEnd = yStart + radius

        val locations = mutableObjectListOf<Location>()

        for (y in yStart.toInt()..yEnd.toInt()) {
            locations.addAll(walkAndSet(y.toDouble(), acquiredState.containerMaterial))
        }

        return locations
    }

    private fun walkAndSet(y: Double, material: Material): ObjectList<Location> {
        val locations = mutableObjectListOf<Location>()

        val min = boundingBox.min
        val max = boundingBox.max

        for (x in min.x.toInt() until max.x.toInt()) {
            for (z in min.z.toInt() until max.z.toInt()) {
                val location = Location(baseLocation.world, x.toDouble(), y, z.toDouble())

                location.block.type = material

                locations.add(location)
            }
        }

        return locations
    }
}