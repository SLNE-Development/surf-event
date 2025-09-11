package dev.slne.surf.event.oneblock.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.arguments.LocationType
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.event.oneblock.permission.OneBlockPermissions
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.event.oneblock.session.PlayerSession
import org.bukkit.Location
import org.bukkit.entity.Player

fun relocateCommand() = commandTree("relocate") {
    withPermission(OneBlockPermissions.RELOCATE_COMMAND)

    playerExecutor { sender, args ->
        startRelocate(sender)
    }

    literalArgument("place") {
        withRequirement { sender ->
            if (sender is Player) {
                PlayerSession[sender.uniqueId].isRelocating
            } else {
                false
            }
        }

        locationArgument("location", LocationType.BLOCK_POSITION) {
            playerExecutor { sender, args ->
                val location: Location by args
                finishRelocate(sender, location)
            }
        }
    }
}

private fun startRelocate(player: Player) {
    plugin.launch {
        val session = PlayerSession[player.uniqueId]
        val result = session.startRelocate(player)
        player.sendMessage(result)

        if (result == PlayerSession.RelocateResult.START_RELOCATING) {
            player.updateCommands()
        }
    }
}

private fun finishRelocate(player: Player, loc: Location) {
    plugin.launch {
        val session = PlayerSession[player.uniqueId]
        val result = session.finishRelocate(loc)
        player.sendMessage(result)
        player.updateCommands()
    }
}