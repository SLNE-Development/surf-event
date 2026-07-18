package dev.slne.surf.event.anarchy.finale

import com.github.shynixn.mccoroutine.folia.scope
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.api.paper.util.forEachPlayer
import dev.slne.surf.event.anarchy.plugin
import dev.slne.surf.event.anarchy.util.geilesRot
import kotlinx.coroutines.Job
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.World
import kotlin.time.Duration.Companion.milliseconds

object FinaleActionbarTask {
    private lateinit var job: Job

    fun start() {
        job = plugin.scope.runAtFixedRate(1500.milliseconds) {
            if (FinaleLifecycle.isRunning()) {
                val remainingPlayerCount =
                    Bukkit.getOnlinePlayers().count { it.gameMode == GameMode.SURVIVAL }

                forEachPlayer {
                    if ((it.gameMode == GameMode.SURVIVAL || it.gameMode == GameMode.ADVENTURE) && (it.world.environment == World.Environment.NETHER)) {
                        it.sendActionBar(buildText {
                            error("⚠")
                            appendSpace()
                            darkSpacer("|")
                            appendSpace()
                            geilesRot("Verlasse den Nether, sonst stirbst du!")
                        })
                    } else {
                        it.sendActionBar(buildText {
                            variableValue(remainingPlayerCount, TextDecoration.BOLD)
                            info(" Spieler verbleibend".toSmallCaps())
                        })
                    }
                }
            }
        }
    }

    fun stop() {
        if (::job.isInitialized && job.isActive) {
            job.cancel()
        }
    }
}