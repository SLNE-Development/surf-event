package dev.slne.surf.event.collectit.display.content

import dev.slne.surf.event.collectit.display.outline.type.DisplayOutlineType
import org.bukkit.Location
import org.bukkit.block.BlockFace

interface DisplayContent {
    val baseLocation: Location
    val facing: BlockFace
    val displayOutlineType: DisplayOutlineType

    fun spawn()
    fun despawn()
}