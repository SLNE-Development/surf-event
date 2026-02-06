package dev.slne.surf.event.collectit.display.content

import dev.slne.surf.event.collectit.display.outline.type.DisplayOutlineType
import org.bukkit.Location
import org.bukkit.block.BlockFace

abstract class AbstractDisplayContent(
    override val baseLocation: Location,
    override val facing: BlockFace,
    override val displayOutlineType: DisplayOutlineType,
) : DisplayContent 