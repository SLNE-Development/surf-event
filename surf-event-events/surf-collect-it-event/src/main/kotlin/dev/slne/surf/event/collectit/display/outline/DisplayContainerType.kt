@file:Suppress("UnstableApiUsage")

package dev.slne.surf.event.collectit.display.outline

import dev.slne.surf.surfapi.bukkit.api.builder.ItemStack
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import org.bukkit.Material
import org.bukkit.util.Vector

enum class DisplayContainerType(
    private val customModelData: Float,
    private val material: Material = Material.PAPER,
    val textOffset: Float = 1.0f,
    val centerOffset: Vector = Vector(0.0, 0.0, 0.0),
) {
    ITEM_ACQUIRED(customModelData = 0.0f),
    ITEM_NOT_ACQUIRED(customModelData = 1.0f),

    ENTITY_ACQUIRED(customModelData = 2.0f),
    ENTITY_NOT_ACQUIRED(customModelData = 3.0f),

    ACHIEVEMENT_ACQUIRED(customModelData = 4.0f),
    ACHIEVEMENT_NOT_ACQUIRED(customModelData = 5.0f);

    fun next() = when (this) {
        ITEM_ACQUIRED -> ITEM_NOT_ACQUIRED
        ITEM_NOT_ACQUIRED -> ITEM_ACQUIRED
        ENTITY_ACQUIRED -> ENTITY_NOT_ACQUIRED
        ENTITY_NOT_ACQUIRED -> ENTITY_ACQUIRED
        ACHIEVEMENT_ACQUIRED -> ACHIEVEMENT_NOT_ACQUIRED
        ACHIEVEMENT_NOT_ACQUIRED -> ACHIEVEMENT_ACQUIRED
    }

    fun createItemStack() = ItemStack(material) {
        setData(DataComponentTypes.CUSTOM_MODEL_DATA, buildCustomModelData())
    }

    fun buildCustomModelData() = CustomModelData.customModelData()
        .addFloat(customModelData)
        .build()
}