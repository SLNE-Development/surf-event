package dev.slne.surf.event.collectit.display

import dev.slne.surf.event.collectit.CollectItInstance
import dev.slne.surf.event.collectit.display.displays.dsl.itemDisplay
import dev.slne.surf.event.collectit.display.outline.DisplayContainerType
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.random
import org.bukkit.Location
import org.bukkit.block.BlockFace
import java.time.OffsetDateTime
import java.util.*

object DisplayManager {
    private val _displays = mutableObjectListOf<Display>()
    val displays = _displays.freeze()

    fun registerDisplay(display: Display) {
        _displays += display
    }

    fun removeDisplay(display: Display) {
        display.despawn()

        _displays -= display
    }

    fun removeDisplays() {
        val iterator = _displays.iterator()

        while (iterator.hasNext()) {
            iterator.next().apply {
                despawn()
            }

            iterator.remove()
        }
    }

    fun spawnDisplays(
        startLocation: Location,
        facing: BlockFace = BlockFace.NORTH,
    ) {
        val playerUuid = UUID.fromString("5c63e51b-82b1-4222-af0f-66a4c31e36ad")
        val items by CollectItInstance::ITEMS

        val currentLocation = startLocation.clone()

        items.forEach { item ->
            val acquiredBy = if (random.nextBoolean()) playerUuid else null

            itemDisplay {
                location = currentLocation
                key = item.key()
                this.facing = facing
                itemStack = item.createItemStack()
                displayContainerType = DisplayContainerType.ITEM_NOT_ACQUIRED
                acquiredByUuid = acquiredBy
                acquiredAt = acquiredBy?.let { OffsetDateTime.now() }
            }.apply {
                spawn()
                registerDisplay(this)
            }

            currentLocation.add(2.0, 0.0, 0.0)
        }
    }
}