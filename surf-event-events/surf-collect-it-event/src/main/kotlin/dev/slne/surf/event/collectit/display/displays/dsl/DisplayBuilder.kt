package dev.slne.surf.event.collectit.display.displays.dsl

import dev.slne.surf.event.collectit.display.Display
import dev.slne.surf.event.collectit.display.content.DisplayContent
import dev.slne.surf.event.collectit.display.outline.DisplayContainerType
import net.kyori.adventure.key.Key
import org.bukkit.Location
import org.bukkit.block.BlockFace
import java.time.OffsetDateTime
import java.util.*

abstract class DisplayBuilder<D : Display, DC : DisplayContent> {
    var key: Key? = null
    var location: Location? = null
    var facing: BlockFace? = null

    var displayContainerType: DisplayContainerType? = null
    var displayContent: DisplayContent? = null

    var acquiredByUuid: UUID? = null
    var acquiredAt: OffsetDateTime? = null

    abstract fun build(): D
}