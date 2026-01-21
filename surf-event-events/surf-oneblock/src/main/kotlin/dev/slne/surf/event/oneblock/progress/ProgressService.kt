package dev.slne.surf.event.oneblock.progress

import dev.slne.surf.event.oneblock.db.IslandService
import dev.slne.surf.event.oneblock.global.GlobalGoals
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

object ProgressService {

    fun onBlockMined(player: Player) {
        val island = IslandService.incrementMined(player.uniqueId) ?: return
        val newPhase = phaseConfig.currentPhase(island.totalMined)
        val oldPhase = phaseConfig.currentPhase(island.totalMined - 1)

        if (oldPhase.id != newPhase.id) {
            player.sendText {
                appendPrefix()
                success("Du hast jetzt ")
                variableValue(newPhase.displayName)
                success(" erreicht!")
            }
        }

        GlobalGoals.onBlockMined()
    }

}