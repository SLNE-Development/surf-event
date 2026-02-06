package dev.slne.surf.event.collectit.display.outline

import dev.slne.surf.event.collectit.display.AcquiredState
import dev.slne.surf.event.collectit.display.outline.type.DisplayOutlineType
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace

data class DisplayOutline(
    private val baseLocation: Location,
    private val facing: BlockFace,
    val displayOutlineType: DisplayOutlineType,
    private var acquiredState: AcquiredState,
) {
    private val locations = mutableObjectSetOf<Location>()

    fun changeAcquiredState(acquiredState: AcquiredState) {
        this.acquiredState = acquiredState

        locations.addAll(displayOutlineType.changeAcquiredState(acquiredState))
    }

    fun spawn() {
        despawn()

        locations.addAll(displayOutlineType.setOutline())
    }

    fun despawn() {
        val iterator = locations.iterator()

        while (iterator.hasNext()) {
            val location = iterator.next()

            location.block.type = Material.AIR

            iterator.remove()
        }
    }
}