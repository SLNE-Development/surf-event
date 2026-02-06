package dev.slne.surf.event.collectit.display.text

import de.oliver.fancyholograms.api.data.TextHologramData
import de.oliver.fancyholograms.api.hologram.Hologram
import dev.slne.surf.event.collectit.display.HologramHolder
import dev.slne.surf.event.collectit.display.outline.DisplayContainerType
import dev.slne.surf.event.collectit.display.utils.DisplayLocationHelper
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.block.BlockFace
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.joml.Vector3f
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DisplayText(
    key: Key,
    baseLocation: Location,
    facing: BlockFace,

    private val displayContainerType: DisplayContainerType,

    text: SurfComponentBuilder.() -> Unit,

    var acquiredByUuid: UUID? = null,
    var acquiredAt: OffsetDateTime? = null,
) : HologramHolder<TextHologramData>(
    key = "${key.asString()}:$KEY_SUFFIX",
    baseLocation = baseLocation,
    facing = facing,
) {
    val acquiredBy: OfflinePlayer?
        get() = acquiredByUuid?.let { server.getOfflinePlayer(it) }

    fun acquire(acquiredBy: Player) {
        acquiredByUuid = acquiredBy.uniqueId
        acquiredAt = OffsetDateTime.now()

        updateText()
    }

    fun reset() {
        acquiredByUuid = null
        acquiredAt = null

        updateText()
    }

    private fun updateText() = updateHologramData {
        removeLines()
        addLines()
    }

    override fun buildHologramData(): TextHologramData {
        return TextHologramData(
            key,
            DisplayLocationHelper.calculateDisplayTextLocation(
                baseLocation,
                facing,
                displayContainerType.textOffset
            )
        ).apply {
            text = translateDisplayLines()

            scale = Vector3f(0.25f, 0.25f, 0.25f)
            textAlignment = TextDisplay.TextAlignment.CENTER
            billboard = Display.Billboard.FIXED
            textUpdateInterval = -1
            isSeeThrough = false
            isPersistent = false
            setTextShadow(true)
            background = Hologram.TRANSPARENT
        }
    }

    private val builtText = SurfComponentBuilder.builder().apply(text).build()
    private val displayLines
        get() = buildList<Component> {
            add(builtText)
            add(Component.empty())
            add(Component.empty())

            add(buildText {
                variableKey("Gesammelt durch:")
                appendSpace()
                variableValue(acquiredBy?.name ?: "/")
            })
            add(Component.empty())

            add(buildText {
                variableKey("Gesammelt am:")
                appendSpace()
                variableValue(acquiredAt?.format(FORMATTER) ?: "/")
            })
        }

    private fun translateDisplayLines(): List<String> =
        displayLines.map { MiniMessage.miniMessage().serialize(it) }

    private fun TextHologramData.addLines() {
        translateDisplayLines().forEach { addLine(it) }
    }

    private fun TextHologramData.removeLines() {
        for (i in 0 until displayLines.size) {
            removeLine(0)
        }
    }

    companion object {
        private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

        const val KEY_SUFFIX = "text"
    }
}