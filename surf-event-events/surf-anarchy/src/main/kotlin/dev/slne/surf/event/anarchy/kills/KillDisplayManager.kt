package dev.slne.surf.event.anarchy.kills

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.paper.util.getPrefixedName
import dev.slne.surf.api.paper.util.namespacedKey
import dev.slne.surf.nametag.api.SurfNametagApi
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

object KillDisplayManager {
    private val killsKey = namespacedKey("kills")

    fun getKills(player: Player) =
        player.persistentDataContainer.get(killsKey, PersistentDataType.INTEGER) ?: 0

    fun setKills(player: Player, kills: Int) =
        player.persistentDataContainer.set(killsKey, PersistentDataType.INTEGER, kills)

    fun incrementKills(player: Player) {
        setKills(player, getKills(player) + 1)
    }

    fun updateNewPlayerForAll(player: Player) {
        Bukkit.getOnlinePlayers().filter { it.canSee(player) }.forEach {
            SurfNametagApi.setNametag(player.uniqueId, it.uniqueId, buildText {
                append(it.getPrefixedName())
                appendNewline()
                white(getKills(it))
                info(" Getötete Spieler")
            })
        }
    }

    fun updateDisplay(player: Player) {
        Bukkit.getOnlinePlayers().filter { it.canSee(player) }.forEach {
            SurfNametagApi.setNametag(player.uniqueId, it.uniqueId, buildText {
                append(player.getPrefixedName())
                appendNewline()
                white(getKills(player))
                info(" Getötete Spieler")
            })
        }
    }

    fun updateDisplayForNewPlayer(player: Player) {
        Bukkit.getOnlinePlayers().filter { player.canSee(it) }.forEach {
            SurfNametagApi.setNametag(it.uniqueId, player.uniqueId, buildText {
                append(it.getPrefixedName())
                appendNewline()
                white(getKills(it))
                info(" Getötete Spieler")
            })
        }
    }
}