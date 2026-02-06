package dev.slne.surf.event.collectit.display.content.contents

import dev.slne.surf.event.collectit.display.content.AbstractDisplayContent
import dev.slne.surf.event.collectit.display.outline.type.DisplayOutlineType
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.joml.Quaternionf
import org.joml.Vector3f

class ItemDisplayContent(
    baseLocation: Location,
    facing: BlockFace,
    displayOutlineType: DisplayOutlineType,
    val itemStack: ItemStack,
    val scale: Float,
) : AbstractDisplayContent(
    baseLocation = baseLocation,
    facing = facing,
    displayOutlineType = displayOutlineType
) {
    private var itemDisplay: ItemDisplay? = null
    private val displayLocation: Location get() = displayOutlineType.containerCenter

    override fun spawn() {
        findOldItemDisplay()

        if (itemDisplay != null) {
            despawn()
        }

        displayLocation.world.spawnEntity(
            displayLocation,
            EntityType.ITEM_DISPLAY,
            CreatureSpawnEvent.SpawnReason.CUSTOM
        ) { entity ->
            require(entity is ItemDisplay) { "Spawned entity is not an ItemDisplay" }

            itemDisplay = entity

            itemDisplay!!.billboard = Display.Billboard.FIXED
            itemDisplay!!.transformation = calculateTransformation()
            itemDisplay!!.itemDisplayTransform = ItemDisplay.ItemDisplayTransform.GROUND
            
            itemDisplay!!.setItemStack(itemStack)
        }
    }

    private fun calculateTransformation(): Transformation {
        val translation = Vector3f(0.0f, 0.0f, 0.0f)
        val leftRotation = Quaternionf(0.0, 0.0, 0.0, 0.0)
        val rightRotation = Quaternionf(0.0, 0.0, 0.0, 0.0)
        val scaleVector = Vector3f(scale, scale, scale)

        return Transformation(translation, leftRotation, scaleVector, rightRotation)
    }

    override fun despawn() {
        itemDisplay?.remove()
        itemDisplay = null
    }

    private fun findOldItemDisplay() {
        itemDisplay = displayLocation.getNearbyEntities(1.0, 1.0, 1.0)
            .filterIsInstance<ItemDisplay>()
            .firstOrNull()
    }
}