package dev.slne.surf.event.collectit.display

import dev.slne.surf.event.collectit.display.content.DisplayContent
import dev.slne.surf.event.collectit.display.outline.DisplayContainer
import dev.slne.surf.event.collectit.display.outline.DisplayContainerType
import dev.slne.surf.event.collectit.display.text.DisplayText
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.key.Key
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import java.time.OffsetDateTime
import java.util.*

open class Display(
    val location: Location,
    val key: Key,
    val facing: BlockFace,
    val displayContainerType: DisplayContainerType,
    val text: SurfComponentBuilder.() -> Unit,

    acquiredByUuid: UUID? = null,
    acquiredAt: OffsetDateTime? = null,

    private val displayContent: DisplayContent
) {
    private val displayContainer = DisplayContainer(
        key = key,
        baseLocation = location,
        facing = facing,
        displayContainerType = displayContainerType
    )

    private val displayText = DisplayText(
        key = key,
        baseLocation = location,
        facing = facing,
        displayContainerType = displayContainerType,
        text = text,
        acquiredByUuid = acquiredByUuid,
        acquiredAt = acquiredAt
    )

    val acquiredByUuid: UUID? by displayText::acquiredByUuid
    val acquiredAt: OffsetDateTime? by displayText::acquiredAt

    val acquiredBy: OfflinePlayer?
        get() = acquiredByUuid?.let { server.getOfflinePlayer(it) }

    fun acquire(player: Player) {
        displayText.acquire(player)
        displayContainer.updateDisplayContainerType(displayContainer.displayContainerType.next())
    }

    fun reset() {
        displayText.reset()
        displayContainer.updateDisplayContainerType(displayContainer.displayContainerType.next())
    }

    fun spawn() {
        displayContainer.spawn()
        displayText.spawn()
    }

    fun despawn() {
        displayText.despawn()
        displayContainer.despawn()
    }
}