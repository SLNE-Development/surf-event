@file:Suppress("UNCHECKED_CAST")

package dev.slne.surf.event.collectit.display

import de.oliver.fancyholograms.api.data.HologramData
import de.oliver.fancyholograms.api.hologram.Hologram
import dev.slne.surf.event.collectit.CollectItInstance
import dev.slne.surf.event.collectit.display.content.DisplayContent
import dev.slne.surf.event.collectit.display.outline.DisplayContainer
import dev.slne.surf.event.collectit.display.text.DisplayText
import dev.slne.surf.event.collectit.plugin
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.key.Key
import org.bukkit.Location
import org.bukkit.block.BlockFace
import kotlin.jvm.optionals.getOrNull

abstract class HologramHolder<D : HologramData>(
    val key: String,
    val baseLocation: Location,
    val facing: BlockFace,
) {
    private val hologramManager get() = plugin.hologramManager

    protected abstract fun buildHologramData(): D

    fun spawn() = findOrCreateHologram()
    fun despawn() = removeHologram()

    protected fun findOrCreateHologram(): Hologram {
        var hologram = findHologram()

        if (hologram == null) {
            hologram = createHologram()
        }

        return hologram
    }

    protected fun removeHologram() {
        val hologram = findHologram() ?: return

        hologramManager.removeHologram(hologram)
    }

    protected fun updateHologramData(updater: D.() -> Unit) {
        val hologram = findHologram() ?: return
        val data = hologram.data as? D ?: return

        updater.invoke(data)

        hologram.queueUpdate()
    }

    protected fun createHologram(): Hologram = hologramManager.create(buildHologramData()).apply {
        hologramManager.addHologram(this)
    }

    protected fun findHologram(): Hologram? =
        hologramManager.getHologram(key).getOrNull()

    companion object {
        private val ITEMS by CollectItInstance::ITEMS
        private val ENTITIES by CollectItInstance::ENTITIES
        private val ADVANCEMENTS by CollectItInstance::ADVANCEMENTS

        fun clearOldHolograms() {
            val itemKeys = ITEMS.map { it.key() }.toObjectSet()
            val entityKeys = ENTITIES.map { it.key() }.toObjectSet()
            val advancementKeys = ADVANCEMENTS.map { it.key() }.toObjectSet()

            clearOldHolograms(itemKeys)
            clearOldHolograms(entityKeys)
            clearOldHolograms(advancementKeys)
        }

        private fun clearOldHolograms(keys: ObjectSet<Key>) {
            keys.forEach(::clearOldHolograms)
        }

        private fun clearOldHolograms(key: Key) {
            val textKey = "${key.asString()}.${DisplayText.KEY_SUFFIX}"
            val containerKey = "${key.asString()}.${DisplayContainer.KEY_SUFFIX}"
            val contentKey = "${key.asString()}.${DisplayContent.KEY_SUFFIX}"

            val hologramManager = plugin.hologramManager
            
            listOf(textKey, containerKey, contentKey).forEach { hologramKey ->
                hologramManager.getHologram(hologramKey).ifPresent { hologram ->
                    hologramManager.removeHologram(hologram)
                }
            }
        }
    }
}