package dev.slne.surf.event.oneblock.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.permission.OneBlockPermissions
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.future.await
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

fun locateOneBlockCommand() = commandTree("locateOneBlock") {
    withPermission(OneBlockPermissions.LOCATE_ONE_BLOCK_COMMAND)

    playerExecutor { sender, args ->
        locateSelf(sender)
    }

    asyncOfflinePlayerArgument("target") {
        withPermission(OneBlockPermissions.LOCATE_OTHERS_ONE_BLOCK_COMMAND)

        anyExecutor { sender, args ->
            val target: CompletableFuture<OfflinePlayer> by args
            locateOther(sender, target)
        }
    }
}

private fun locateSelf(player: Player) {
    val island = IslandService.getIsland(player.uniqueId)

    if (island == null) {
        return player.sendText {
            appendPrefix()
            error("Du hast keinen OneBlock.")
        }
    }

    player.sendText {
        appendPrefix()
        success("Dein OneBlock befindet sich bei ")
        variableValue("X: ${island.oneBlock.blockX} Y: ${island.oneBlock.blockY} Z: ${island.oneBlock.blockZ}")
    }
}

private fun locateOther(sender: CommandSender, targetFuture: CompletableFuture<OfflinePlayer>) {
    plugin.launch {
        try {
            val target = targetFuture.await()
            val island = IslandService.getIsland(target.uniqueId)

            if (island == null) {
                sender.sendText {
                    appendPrefix()
                    variableValue(target.name ?: "#Unbekannt")
                    appendSpace()
                    error("hat keinen OneBlock.")
                }
                return@launch
            }

            sender.sendText {
                appendPrefix()
                variableValue(target.name ?: "#Unbekannt")
                success("s OneBlock befindet sich bei ")
                variableValue("X: ${island.oneBlock.blockX} Y: ${island.oneBlock.blockY} Z: ${island.oneBlock.blockZ}")

                if (sender.hasPermission(OneBlockPermissions.LOCATE_OTHERS_ONE_BLOCK_TP)) {
                    hoverEvent(buildText {
                        info("Klicke, um dich zu diesem OneBlock zu teleportieren.")
                    })
                    clickEvent(ClickEvent.callback(ClickCallback.widen({ player ->
                        player.teleportAsync(island.oneBlock.clone().add(0.0, 1.0, 0.0))
                    }, Player::class.java)))
                }
            }


        } catch (throwable: Throwable) {
            val cause = throwable.cause
            val rootCause = if (cause is RuntimeException) cause.cause else cause

            sender.sendMessage(rootCause?.message ?: "An error occurred")
        }
    }
}