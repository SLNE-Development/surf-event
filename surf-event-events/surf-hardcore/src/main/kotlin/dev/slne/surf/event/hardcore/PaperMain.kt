package dev.slne.surf.event.hardcore

import com.destroystokyo.paper.profile.PlayerProfile
import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.jorel.commandapi.kotlindsl.timeArgument
import dev.slne.surf.event.hardcore.config.HardcoreConfigHolder
import dev.slne.surf.event.hardcore.papi.HardcorePapiHook
import dev.slne.surf.event.hardcore.sound.SoundManager
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.bukkit.api.hook.papi.papiHook
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import io.papermc.paper.util.Tick
import kotlinx.coroutines.withContext
import org.bukkit.BanEntry
import org.bukkit.GameRule
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

class PaperMain : SuspendingJavaPlugin() {

    companion object {
        const val HARDCORE_BAN_SOURCE = "Hardcore"
    }

    override suspend fun onEnableAsync() {
        HardcoreConfigHolder
        withContext(globalRegionDispatcher) {
            for (world in server.worlds) {
                world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false)
            }
        }
        HardcoreListener.register()
        papiHook.register(HardcorePapiHook)

        commandAPICommand("deathsound") {
            withPermission(HardcorePermissions.HARDCORE_COMMAND)
            playerExecutor { sender, _ ->
                launch {
                    val newState = !SoundManager.getSoundState(sender)
                    SoundManager.setSoundState(sender, newState)
                    sender.sendText {
                        appendPrefix()
                        info("Du hast den Zustand des Hardcore-Todesgeräuschs auf ")
                        if (newState) {
                            success("aktiviert")
                        } else {
                            error("deaktiviert")
                        }
                        info(" gesetzt.")
                    }
                }
            }
        }

        commandAPICommand("hardcore") {
            withPermission(HardcorePermissions.HARDCORE_COMMAND)

            subcommand("reload") {
                anyExecutor { sender, _ ->
                    HardcoreConfigHolder.reload()
                    sender.sendText {
                        appendPrefix()
                        success("Die Hardcore-Konfiguration wurde neu geladen.")
                    }
                }
            }

            subcommand("cancelPreEnd") {
                anyExecutor { sender, _ ->
                    if (EndManager.isEnding && !EndManager.isEnd) {
                        EndManager.isEnding = false
                        sender.sendMessage("Pre-end event has been cancelled.")
                    } else {
                        sender.sendMessage("No pre-end event is currently active.")
                    }
                }
            }

            subcommand("end") {
                timeArgument("preEndDuration", optional = true)
                timeArgument("shrinkDuration", optional = true)

                anyExecutor { sender, args ->
                    launch {
                        val preEndDuration = args.getOrDefaultUnchecked(
                            "preEndDuration",
                            Tick.tick().fromDuration(1.hours.toJavaDuration())
                        ).toLong()
                        val shrinkDuration = args.getOrDefaultUnchecked(
                            "shrinkDuration",
                            Tick.tick().fromDuration(30.minutes.toJavaDuration())
                        ).toLong()

                        EndManager.startEnd(
                            preEndDuration = Tick.of(preEndDuration).toKotlinDuration(),
                            shrinkDuration = Tick.of(shrinkDuration).toKotlinDuration()
                        )
                    }
                }
            }
        }
    }

    fun tryBanPlayer(player: Player) {
        if (!player.hasPermission(HardcorePermissions.HARDCORE_BYPASS)) {
            player.ban<BanEntry<PlayerProfile>>(
                "Game Over!",
                null as? Duration,
                PaperMain.HARDCORE_BAN_SOURCE,
                true
            )
        }
    }
}

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)