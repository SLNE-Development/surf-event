package dev.slne.surf.event.hardcore.message

import dev.slne.surf.event.hardcore.HardcorePermissions
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Location
import org.bukkit.entity.Player

object MessageManager {
    val nearbyPlayersWhenEmptyingLavaBucket = buildText {
        appendPrefix()
        error("Du kannst den Lava-Eimer nicht leeren, wenn sich andere Spieler in der Nähe befinden!")
    }

    val cannotChangeDimensionDuringFinalEvent = buildText {
        appendPrefix()
        error("Du kannst die Dimension während des finalen Events nicht wechseln!")
    }

    fun broadcastDeathMessage(original: Component?, deathLocation: Location) {
        if (original == null) return
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