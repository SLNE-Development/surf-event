package dev.slne.surf.event.collectit

import dev.slne.surf.event.collectit.commands.displayCommand
import dev.slne.surf.event.collectit.display.DisplayManager
import dev.slne.surf.event.collectit.display.HologramHolder
import dev.slne.surf.event.collectit.utils.EXCLUDED_ITEMS
import dev.slne.surf.surfapi.core.api.util.toObjectList
import org.bukkit.Bukkit
import org.bukkit.Registry

object CollectItInstance {
    val ITEMS = Registry.ITEM.filter { itemType ->
        !EXCLUDED_ITEMS.contains(itemType)
    }.toObjectList()

    val ENTITIES = Registry.ENTITY_TYPE.filter { entityType ->
        true
    }.toObjectList()

    val ADVANCEMENTS = Bukkit.advancementIterator().asSequence().filter { advancement ->
        true
    }.toObjectList()

    fun onEnable() {
        HologramHolder.clearOldHolograms()

        displayCommand()

        DisplayManager.spawnDisplays(
            startLocation = Bukkit.getWorld("world")!!.spawnLocation.clone().add(0.0, 50.0, 0.0)
        )
    }

    fun onDisable() {
        DisplayManager.removeDisplays()
        HologramHolder.clearOldHolograms()
    }
}