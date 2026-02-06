package dev.slne.surf.event.collectit.display

import org.bukkit.Material

enum class AcquiredState(
    val containerMaterial: Material,
    val baseMaterial: Material = Material.IRON_BLOCK
) {
    NOT_ACQUIRED(Material.LIGHT_GRAY_STAINED_GLASS),
    ACQUIRED(Material.LIME_STAINED_GLASS),
}