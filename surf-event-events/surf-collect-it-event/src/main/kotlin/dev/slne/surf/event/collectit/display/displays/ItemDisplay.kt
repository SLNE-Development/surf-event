package dev.slne.surf.event.collectit.display.displays

import dev.slne.surf.event.collectit.display.Display
import dev.slne.surf.event.collectit.display.content.contents.ItemDisplayContent
import dev.slne.surf.event.collectit.display.outline.DisplayOutline
import dev.slne.surf.event.collectit.display.text.DisplayText
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

class ItemDisplay(
    location: Location,
    displayOutline: DisplayOutline,
    displayContent: ItemDisplayContent,
    displayText: DisplayText,
) : Display(
    location = location,
    displayOutline = displayOutline,
    displayContent = displayContent,
    displayText = displayText
) {
    val itemStack: ItemStack by displayContent::itemStack
}