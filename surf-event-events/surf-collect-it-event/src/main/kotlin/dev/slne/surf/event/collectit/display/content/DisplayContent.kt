package dev.slne.surf.event.collectit.display.content

import dev.slne.surf.event.collectit.display.outline.DisplayContainerType
import org.bukkit.Location
import org.bukkit.block.BlockFace

abstract class DisplayContent(
    val baseLocation: Location,
    val facing: BlockFace,
    val displayContainerType: DisplayContainerType
) {
    companion object {
        val KEY_SUFFIX = "content"
    }
}