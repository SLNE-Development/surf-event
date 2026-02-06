package dev.slne.surf.event.collectit.display.displays.dsl

import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper
import dev.slne.surf.event.collectit.display.AcquiredState
import dev.slne.surf.event.collectit.display.Display
import dev.slne.surf.event.collectit.display.content.DisplayContent
import dev.slne.surf.event.collectit.display.outline.DisplayOutline
import dev.slne.surf.event.collectit.display.text.DisplayText
import org.bukkit.Location
import org.bukkit.block.BlockFace

abstract class DisplayBuilder<D : Display, DC : DisplayContent> {
    var location: Location? = null
    var facing: BlockFace? = null
    var displayOutline: DisplayOutline? = null
    var displayText: DisplayText? = null
    var acquiredState: AcquiredState? = null

    @OverridingMethodsMustInvokeSuper
    open fun validate() {
        requireNotNull(location) { "Location must be set" }
        requireNotNull(facing) { "Facing must be set" }
        requireNotNull(displayOutline) { "DisplayOutline must be set" }
        requireNotNull(displayText) { "DisplayText must be set" }
        requireNotNull(acquiredState) { "AcquiredState must be set" }
    }

    abstract fun build(): D
}