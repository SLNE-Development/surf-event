package dev.slne.surf.event.collectit.display.displays

import dev.slne.surf.event.collectit.display.Display
import dev.slne.surf.event.collectit.display.content.contents.ItemDisplayContent
import dev.slne.surf.event.collectit.display.outline.DisplayContainerType
import dev.slne.surf.surfapi.core.api.messages.Colors
import net.kyori.adventure.key.Key
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.inventory.ItemStack
import java.time.OffsetDateTime
import java.util.*

class ItemDisplay(
    key: Key,
    location: Location,
    facing: BlockFace,
    displayContainerType: DisplayContainerType,
    displayContent: ItemDisplayContent,
    acquiredByUuid: UUID? = null,
    acquiredAt: OffsetDateTime? = null
) : Display(
    key = key,
    location = location,
    facing = facing,
    displayContent = displayContent,
    acquiredByUuid = acquiredByUuid,
    acquiredAt = acquiredAt,
    displayContainerType = displayContainerType,
    text = {
        append(displayContent.itemStack.effectiveName().colorIfAbsent(Colors.INFO))
    }
) {
    val itemStack: ItemStack by displayContent::itemStack
}