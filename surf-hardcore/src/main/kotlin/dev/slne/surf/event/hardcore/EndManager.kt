package dev.slne.surf.event.hardcore

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.World
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object EndManager {
    var isEnd: Boolean = false
        private set

    var isEnding: Boolean = false

    suspend fun startEnd(preEndDuration: Duration, shrinkDuration: Duration) {
        if (isEnding) return
        var remaining = preEndDuration.inWholeSeconds
        isEnding = true

        while (remaining > 0) {
            if (!isEnding) return
            remaining--
            val remainingDuration = remaining.seconds

            server.sendActionBar(buildText {
                info("Das Ende beginnt in ")
                variableValue(remainingDuration.toString())
            })

            if (remainingDuration in 0.seconds..10.seconds
                || remainingDuration == preEndDuration - 5.seconds
                || remainingDuration == 45.minutes
                || remainingDuration == 30.minutes
                || remainingDuration == 15.minutes
                || remainingDuration == 5.minutes
                || remainingDuration == 1.minutes
            ) {
                server.sendText {
                    appendPrefix()
                    appendNewPrefixedLine()
                    appendNewPrefixedLine()
                    info("Das große Finale beginnt in ")
                    variableValue(remainingDuration.toString())
                    appendNewPrefixedLine()
                    appendNewPrefixedLine()
                    appendNewPrefixedLine()
                    append {
                        error("Verlasse ")
                        if (remainingDuration > 1.minutes) {
                            error("bald")
                        } else {
                            error("JETZT")
                        }
                        error(" den Nether und das End!")

                        decorate(TextDecoration.BOLD)
                    }
                    appendNewPrefixedLine()
                    error("Wer sich nach Ablauf der Zeit noch dort aufhält,")
                    appendNewPrefixedLine()
                    error("scheidet automatisch aus!")
                    appendNewPrefixedLine()
                }
                server.playSound {
                    type(Sound.BLOCK_NOTE_BLOCK_PLING)
                    pitch(2f)
                    volume(.5f)
                }
            }

            delay(1.seconds)
        }

        isEnd = true
        server.sendText {
            appendPrefix()
            appendNewPrefixedLine()
            info("Das große Finale beginnt jetzt!")
            appendNewPrefixedLine()
            error("Alle Spieler, die sich noch im Nether oder im End befinden, scheiden aus!")
        }
        server.playSound {
            type(Sound.UI_TOAST_CHALLENGE_COMPLETE)
            pitch(0f)
            volume(1f)
        }

        end(shrinkDuration)
    }

    suspend fun end(shrinkDuration: Duration) {
        withContext(plugin.globalRegionDispatcher) {
            server.setWhitelist(true)

            server.worlds.filterNot { it.environment == World.Environment.NORMAL }
                .forEach { world ->
                    world.players.forEach { player ->
                        plugin.tryBanPlayer(player)
                    }
                }

            val overworld = server.worlds.first()
            with(overworld.worldBorder) {
                setSize(10.0, shrinkDuration.inWholeSeconds)
            }
        }
    }
}