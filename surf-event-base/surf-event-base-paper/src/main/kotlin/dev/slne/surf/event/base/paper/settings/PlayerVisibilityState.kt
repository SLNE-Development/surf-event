package dev.slne.surf.event.base.paper.settings

enum class PlayerVisibilityState(val displayName: String, val simpleName: String) {
    ALL("Alle Spieler", "Alle"),
    VIP("Nur VIPs", "VIPs"),
    FRIENDS("Nur Freunde", "Freunde"),
    NONE("Keine Spieler", "Niemand");
}