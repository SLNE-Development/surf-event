package dev.slne.surf.event.oneblock.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.permission.OneBlockPermissions
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.event.oneblock.session.PlayerSession
import dev.slne.surf.surfapi.core.api.messages.adventure.clickCallback
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes

private val lastTimeRelocated = ConcurrentHashMap<UUID, Long>()
private val relocatingMillis = 5.minutes.inWholeMilliseconds

fun relocateCommand() = commandTree("relocate") {
    withPermission(OneBlockPermissions.RELOCATE_COMMAND)

    literalArgument("here") {
        playerExecutor { player, _ ->
            val location = player.location

            if (IslandService.anyNearIslands(location)) {
                player.sendText {
                    appendErrorPrefix()
                    error("In der Nähe gibt es bereits einen OneBlock. Bitte wähle einen anderen Ort.")
                }
                return@playerExecutor
            }

            if (System.currentTimeMillis() - (lastTimeRelocated[player.uniqueId]
                    ?: 0) < relocatingMillis
            ) {
                player.sendText {
                    appendErrorPrefix()
                    error("Bitte warte noch ")
                    variableValue(
                        formatRemaining(
                            relocatingMillis - (System.currentTimeMillis() - (lastTimeRelocated[player.uniqueId]
                                ?: 0))
                        )
                    )
                    error(" bevor du deinen OneBlock erneut verschieben kannst.")
                }
                return@playerExecutor
            }

            player.sendText {
                appendInfoPrefix()
                info("Möchtest du deinen Oneblock hierhin verschieben? ")
                append {
                    darkSpacer("[")
                    success("Verschieben")
                    darkSpacer("]")
                    clickCallback {
                        plugin.launch {
                            val session = PlayerSession[player.uniqueId]
                            val result = session.relocate(location)

                            if (result.isSuccess()) {
                                lastTimeRelocated[player.uniqueId] = System.currentTimeMillis()
                            }

                            player.sendText {
                                append(result)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatRemaining(remainingMillis: Long) = buildString {
    val seconds = remainingMillis / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    if (minutes > 0) {
        append("$minutes Minuten")
    }
    if (remainingSeconds > 0) {
        if (minutes > 0) append(" und ")
        append("$remainingSeconds Sekunden")
    }
}