package dev.slne.surf.event.hardcore.message

import dev.slne.surf.event.hardcore.HardcorePermissions
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Location
import org.bukkit.entity.Player
import org.gradle.internal.impldep.org.bouncycastle.oer.its.ieee1609dot2dot1.AdditionalParams.original

object MessageManager {
    val nearbyPlayersWhenEmptyingLavaBucket = buildText {
        appendPrefix()
        error("Du kannst den Lava-Eimer nicht leeren, wenn sich andere Spieler in der Nähe befinden!")
    }

    val cannotChangeDimensionDuringFinalEvent = buildText {
        appendPrefix()
        error("Du kannst die Dimension während des finalen Events nicht wechseln!")
    }

    val cannotPlaceEndCrystalBeforeEnd = buildText {
        appendPrefix()
        error("Du kannst den Endkristall nicht platzieren, bevor das Finale begonnen hat!")
    }

    val cannotPlaceBedsInOtherWorldsBeforeEnd = buildText {
        appendPrefix()
        error("Du kannst Betten in anderen Dimensionen nicht platzieren, bevor das Finale begonnen hat!")
    }

    val cannotPlaceRespawnAnchorBeforeEnd = buildText {
        appendPrefix()
        error("Du kannst den Respawn-Anker nicht platzieren, bevor das Finale begonnen hat!")
    }

    fun broadcastDeathMessage(original: Component?, deathLocation: Location) {
        if (original == null) return
        val original = buildText {
            darkSpacer("[")
            error("☠")
            darkSpacer("] ")
            append(original)
        }

        val withTp = buildWithTp(original, deathLocation)

        forEachPlayer { player ->
            if (player.hasPermission(HardcorePermissions.HARDCORE_TP)) {
                player.sendMessage(withTp)
            } else {
                player.sendMessage(original)
            }
        }
    }

    private fun buildWithTp(original: Component, deathLocation: Location) = buildText {
        append {
            spacer("[")
            info("TP")
            spacer("] ")
            hoverEvent(buildText {
                info("Klicke, um zu teleportieren")
            })
            clickEvent(ClickEvent.callback({ clicker ->
                if (clicker !is Player) return@callback
                clicker.teleportAsync(deathLocation)
            }, { builder ->
                builder.uses(ClickCallback.UNLIMITED_USES)
            }))
        }
        append(original)
    }
}