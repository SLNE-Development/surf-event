package dev.slne.surf.event.collectit.display.content.contents

import dev.slne.surf.event.collectit.display.content.DisplayContent
import dev.slne.surf.event.collectit.display.outline.DisplayContainerType
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.inventory.ItemStack

class ItemDisplayContent(
    baseLocation: Location,
    facing: BlockFace,
    displayContainerType: DisplayContainerType,
    val itemStack: ItemStack,
) : DisplayContent(
    baseLocation = baseLocation,
    facing = facing,
    displayContainerType = displayContainerType,
)