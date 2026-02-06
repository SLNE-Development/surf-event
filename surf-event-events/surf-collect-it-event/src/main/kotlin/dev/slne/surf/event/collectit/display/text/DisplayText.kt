package dev.slne.surf.event.collectit.display.text

import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.block.BlockFace
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.Quaternionf
import org.joml.Vector3f
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class DisplayText(
    private val baseLocation: Location,
    private val facing: BlockFace,
    val text: SurfComponentBuilder.() -> Unit,
) {
    val displayLocation: Location = calculateDisplayLocationFromBase()
    val builtText = SurfComponentBuilder.builder().apply(text).build()

    var acquiredByUuid: UUID? = null
    var acquiredAt: OffsetDateTime? = null

    var textDisplay: TextDisplay? = null

    val acquiredBy: OfflinePlayer?
        get() = acquiredByUuid?.let { server.getOfflinePlayer(it) }

    fun acquire(player: OfflinePlayer) {
        acquiredByUuid = player.uniqueId
        acquiredAt = OffsetDateTime.now()

        setDisplayText()
    }

    fun reset() {
        acquiredByUuid = null
        acquiredAt = null

        setDisplayText()
    }

    private fun calculateDisplayLocationFromBase(): Location {
        val offset = when (facing) {
            BlockFace.NORTH -> Vector(0.5, 0.0, 0.5)
            BlockFace.SOUTH -> Vector(0.5, 0.0, -0.5)
            BlockFace.EAST -> Vector(-0.5, 0.0, 0.5)
            BlockFace.WEST -> Vector(0.5, 0.0, 0.5)
            else -> null
        }

        if (offset == null) return baseLocation.clone()

        return baseLocation.clone().add(offset)
    }

    fun spawn() {
        findOldTextDisplay()

        if (textDisplay != null) {
            despawn()
        }

        displayLocation.world.spawnEntity(
            displayLocation,
            EntityType.TEXT_DISPLAY,
            CreatureSpawnEvent.SpawnReason.CUSTOM
        ) { entity ->
            require(entity is TextDisplay) { "Spawned entity is not a TextDisplay" }
            textDisplay = entity

            textDisplay!!.alignment = TextDisplay.TextAlignment.CENTER
            textDisplay!!.isDefaultBackground = false
            textDisplay!!.isShadowed = false
            textDisplay!!.billboard = Display.Billboard.FIXED
            textDisplay!!.transformation = calculateTransformation()

            setDisplayText()
        }
    }

    private fun calculateTransformation(): Transformation {
        val translation = Vector3f(0.0f, 0.0f, 0.0f)
        val leftRotation = Quaternionf(0.0, 0.0, 0.0, 0.0)
        val rightRotation = Quaternionf(0.0, 0.0, 0.0, 0.0)
        val scale = Vector3f(1.0f, 1.0f, 1.0f)

        return Transformation(translation, leftRotation, scale, rightRotation)
    }

    fun despawn() {
        textDisplay?.remove()
        textDisplay = null
    }

    private fun setDisplayText() {
        val textDisplay = textDisplay ?: return

        textDisplay.text(buildText {
            append(builtText)
            appendNewline(2)

            variableKey("Gesammelt durch:")
            appendSpace()
            variableValue(acquiredBy?.name ?: "/")
            appendNewline()

            variableKey("Gesammelt am:")
            appendSpace()
            variableValue(acquiredAt?.format(FORMATTER) ?: "/")
        })
    }

    private fun findOldTextDisplay() {
        textDisplay = displayLocation.getNearbyEntities(1.0, 1.0, 1.0)
            .filterIsInstance<TextDisplay>()
            .firstOrNull()
    }

    companion object {
        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    }
}