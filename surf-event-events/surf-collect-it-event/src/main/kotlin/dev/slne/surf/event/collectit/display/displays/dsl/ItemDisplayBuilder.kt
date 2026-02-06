package dev.slne.surf.event.collectit.display.displays.dsl

import dev.slne.surf.event.collectit.display.content.contents.ItemDisplayContent
import dev.slne.surf.event.collectit.display.displays.ItemDisplay
import org.bukkit.inventory.ItemStack

@DslMarker
annotation class ItemDisplayDslMarker

class ItemDisplayBuilder : DisplayBuilder<ItemDisplay, ItemDisplayContent>() {
    var itemStack: ItemStack? = null

    override fun build() = ItemDisplay(
        key = key!!,
        location = location!!,
        facing = facing!!,
        displayContent = ItemDisplayContent(
            baseLocation = location!!,
            facing = facing!!,
            itemStack = itemStack!!,
            displayContainerType = displayContainerType!!
        ),
        displayContainerType = displayContainerType!!,
        acquiredByUuid = acquiredByUuid,
        acquiredAt = acquiredAt,
    )
}

@ItemDisplayDslMarker
fun itemDisplay(block: ItemDisplayBuilder.() -> Unit) =
    ItemDisplayBuilder().apply(block).build()