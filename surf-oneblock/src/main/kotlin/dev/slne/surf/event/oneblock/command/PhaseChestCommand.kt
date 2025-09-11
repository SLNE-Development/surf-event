package dev.slne.surf.event.oneblock.command

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.ListArgumentBuilder
import dev.jorel.commandapi.arguments.LocationType
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.event.oneblock.permission.OneBlockPermissions
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.event.oneblock.progress.PhaseChests
import dev.slne.surf.event.oneblock.progress.phaseChests
import dev.slne.surf.event.oneblock.progress.phaseConfig
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Location
import org.bukkit.block.Chest
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack

fun phaseChestCommand() = commandAPICommand("phasechest") {
    withPermission(OneBlockPermissions.PHASE_CHEST_COMMAND)

    literalArgument("create") {
        stringArgument("name") {
            locationArgument("chestLocation", LocationType.BLOCK_POSITION) {
                doubleArgument("weight", 0.0001) {
                    anyExecutor { sender, args ->
                        val name: String by args
                        val chestLocation: Location by args
                        val weight: Double by args
                        create(sender, name, chestLocation, weight)
                    }
                }
            }
        }
    }

    literalArgument("addPhases") {
        stringArgument("name") {
            replaceSuggestions(ArgumentSuggestions.stringCollection { info ->
                phaseChests.chests.map { it.name }
            })

            argument(
                ListArgumentBuilder<String>("phases")
                    .withList { info ->
                        phaseConfig.phases.map { it.id }
                    }
                    .withStringMapper()
                    .buildGreedy()
            ) {
                anyExecutor { sender, args ->
                    val name: String by args
                    val phases: List<String> by args
                    addPhases(sender, name, phases)
                }
            }
        }
    }

    literalArgument("reload") {
        anyExecutor { sender, _ ->
            reload(sender)
        }
    }
}

private fun create(sender: CommandSender, name: String, chestLocation: Location, weight: Double) {
    val exists = phaseChests.chests.find { it.name == name }
    if (exists != null) {
        throw CommandAPI.failWithString("Chest with name '$name' already exists!")
    }

    plugin.launch(plugin.regionDispatcher(chestLocation)) {
        val chest = chestLocation.block.state
        if (chest !is Chest) {
            sender.sendText {
                error("The provided location is not a chest!")
            }
            return@launch
        }

        val inventory = chest.blockInventory
        if (inventory.isEmpty) {
            sender.sendText {
                error("The provided chest is empty!")
            }
            return@launch
        }

        val serializedContent = ItemStack.serializeItemsAsBytes(inventory.contents)
        phaseChests.chests += PhaseChests.ChestEntry(
            name = name,
            content = serializedContent,
            weight = weight,
        )
    }

    PhaseChests.saveToFile()
    sender.sendText {
        success("Created new phase chest '$name'!")
    }
}

private fun addPhases(sender: CommandSender, name: String, phases: List<String>) {
    val chest = phaseChests.chests.find { it.name == name }
    if (chest == null) {
        throw CommandAPI.failWithString("Chest with name '$name' does not exist!")
    }

    val toAdd = phases.filter { it !in chest.phases }
    if (toAdd.isEmpty()) {
        throw CommandAPI.failWithString("All provided phases are already assigned to chest '$name'!")
    }

    chest.phases += toAdd
    PhaseChests.saveToFile()
    sender.sendText {
        success("Added phases ${toAdd.joinToString(", ")} to chest '$name'!")
    }
}

private fun reload(sender: CommandSender) {
    PhaseChests.reloadFromFile()
    sender.sendText {
        success("Reloaded phase chests from file!")
    }
}