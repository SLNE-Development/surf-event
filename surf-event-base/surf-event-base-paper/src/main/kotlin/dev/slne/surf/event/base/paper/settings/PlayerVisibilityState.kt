package dev.slne.surf.event.base.paper.settings

import org.bukkit.Material

enum class PlayerVisibilityState(
    val displayName: String,
    val simpleName: String,
    val material: Material
) {
    ALL("Alle Spieler", "Alle", Material.LIME_CANDLE),
    VIP("Nur VIPs", "VIPs", Material.PINK_CANDLE),
    FRIENDS("Nur Freunde", "Freunde", Material.YELLOW_CANDLE),
    NONE("Keine Spieler", "Niemand", Material.RED_CANDLE);
}