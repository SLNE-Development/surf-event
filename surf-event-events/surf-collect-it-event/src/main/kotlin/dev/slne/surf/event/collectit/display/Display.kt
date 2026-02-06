package dev.slne.surf.event.collectit.display

import dev.slne.surf.event.collectit.display.content.DisplayContent
import dev.slne.surf.event.collectit.display.outline.DisplayOutline
import dev.slne.surf.event.collectit.display.text.DisplayText
import org.bukkit.Location
import org.bukkit.OfflinePlayer

open class Display(
    val location: Location,
    val displayOutline: DisplayOutline,
    val displayContent: DisplayContent,
    val displayText: DisplayText,
) {
    val acquired: Boolean get() = displayText.acquiredAt != null

    fun acquire(player: OfflinePlayer) {
        displayText.acquire(player)
        displayOutline.changeAcquiredState(AcquiredState.ACQUIRED)
    }

    fun reset() {
        displayText.reset()
        displayOutline.changeAcquiredState(AcquiredState.NOT_ACQUIRED)
    }

    fun spawn() {
        displayOutline.spawn()
        displayText.spawn()
        displayContent.spawn()
    }

    fun despawn() {
        displayContent.despawn()
        displayText.despawn()
        displayOutline.despawn()
    }
}