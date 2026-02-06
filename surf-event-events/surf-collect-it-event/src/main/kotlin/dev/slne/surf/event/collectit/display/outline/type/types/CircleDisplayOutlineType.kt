package dev.slne.surf.event.collectit.display.outline.type.types

import dev.slne.surf.event.collectit.display.AcquiredState
import dev.slne.surf.event.collectit.display.outline.type.AbstractDisplayOutlineType
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Location
import org.joml.Vector2i

class CircleDisplayOutlineType(
    baseLocation: Location,
    radius: Int,
    baseHeight: Int,
    acquiredState: AcquiredState,
) : AbstractDisplayOutlineType(
    baseLocation = baseLocation,
    radius = radius,
    baseHeight = baseHeight,
    acquiredState = acquiredState,
) {
    private fun calculatePossibleLocations(): ObjectList<Vector2i> {
        val locations = mutableObjectListOf<Vector2i>()
        val centerX = baseLocation.x
        val centerZ = baseLocation.z

        for (x in (centerX - radius).toInt()..(centerX + radius).toInt()) {
            for (z in (centerZ - radius).toInt()..(centerZ + radius).toInt()) {
                val distanceSquared = (x - centerX) * (x - centerX) + (z - centerZ) * (z - centerZ)

                if (distanceSquared <= radius * radius) {
                    locations.add(Vector2i(x, z))
                }
            }
        }

        return locations
    }

    private val possibleLocations = calculatePossibleLocations()

    override fun setBase(): ObjectList<Location> {
        val locations = mutableObjectListOf<Location>()

        for (vector in possibleLocations) {
            val location = Location(
                baseLocation.world,
                vector.x.toDouble(),
                baseLocation.y,
                vector.y.toDouble()
            )

            location.block.type = acquiredState.baseMaterial

            locations.add(location)
        }

        return locations
    }

    override fun setContainer(): ObjectList<Location> {
        val locations = mutableObjectListOf<Location>()

        for (vector in possibleLocations) {
            for (y in (baseLocation.y + 1).toInt()..(baseLocation.y + radius).toInt()) {
                val location = Location(
                    baseLocation.world,
                    vector.x.toDouble(),
                    y.toDouble(),
                    vector.y.toDouble()
                )

                location.block.type = acquiredState.containerMaterial

                locations.add(location)
            }
        }

        return locations
    }
}