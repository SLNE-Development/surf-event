@file:OptIn(NmsUseWithCaution::class)
@file:Suppress("UnstableApiUsage")

package dev.slne.surf.event.collectit.display.outline

import de.oliver.fancyholograms.api.data.ItemHologramData
import dev.slne.surf.event.collectit.display.HologramHolder
import dev.slne.surf.event.collectit.display.utils.DisplayLocationHelper
import dev.slne.surf.surfapi.bukkit.api.nms.NmsUseWithCaution
import net.kyori.adventure.key.Key
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.Display

class DisplayContainer(
    key: Key,
    baseLocation: Location,
    facing: BlockFace,

    var displayContainerType: DisplayContainerType,
) : HologramHolder<ItemHologramData>(
    key = "${key.asString()}:$KEY_SUFFIX",
    baseLocation = baseLocation,
    facing = facing,
) {
    fun updateDisplayContainerType(newType: DisplayContainerType) {
        displayContainerType = newType

        updateItemStack()
    }

    override fun buildHologramData(): ItemHologramData {
        return ItemHologramData(
            key,
            DisplayLocationHelper.calculateDisplayContentLocation(
                baseLocation,
                facing,
                displayContainerType.centerOffset
            )
        ).apply {
            billboard = Display.Billboard.FIXED
            setPersistent(false)
        }
    }

    fun updateItemStack() = updateHologramData {
        itemStack = displayContainerType.createItemStack()
    }

    companion object {
        const val KEY_SUFFIX = "container"
    }
}