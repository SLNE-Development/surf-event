package dev.slne.surf.event.collectit.display.displays.dsl

import dev.slne.surf.event.collectit.display.content.contents.ItemDisplayContent
import dev.slne.surf.event.collectit.display.displays.ItemDisplay
import dev.slne.surf.event.collectit.display.outline.DisplayOutline
import dev.slne.surf.event.collectit.display.outline.type.types.CuboidDisplayOutlineType
import dev.slne.surf.event.collectit.display.text.DisplayText
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.inventory.ItemStack

@DslMarker
annotation class ItemDisplayDslMarker

class ItemDisplayBuilder : DisplayBuilder<ItemDisplay, ItemDisplayContent>() {
    var itemStack: ItemStack? = null
    var scale: Float = 1.0f

    override fun validate() {
        super.validate()

        requireNotNull(itemStack) { "ItemStack must be set" }
    }

    override fun build() = ItemDisplay(
        location = location!!,
        displayOutline = DisplayOutline(
            baseLocation = location!!,
            facing = facing!!,
            displayOutlineType = CuboidDisplayOutlineType(
                baseLocation = location!!,
                radius = 1,
                baseHeight = 1,
                acquiredState = acquiredState!!,
            ),
            acquiredState = acquiredState!!,
        ),
        displayContent = ItemDisplayContent(
            baseLocation = location!!,
            facing = facing!!,
            displayOutlineType = displayOutline!!.displayOutlineType,
            itemStack = itemStack!!,
            scale = scale
        ),
        displayText = DisplayText(
            baseLocation = location!!,
            facing = facing!!,

            ),
    )
}

@ItemDisplayDslMarker
fun itemDisplay(block: ItemDisplayBuilder.() -> Unit): ItemDisplay {
    val builder = ItemDisplayBuilder()
    builder.block()
    return builder.build()
}

fun test(location: Location, itemStack: ItemStack) {
    val itemDisplay = itemDisplay {
        this.location = location
        this.facing = BlockFace.NORTH
        this.itemStack = itemStack
    }
}