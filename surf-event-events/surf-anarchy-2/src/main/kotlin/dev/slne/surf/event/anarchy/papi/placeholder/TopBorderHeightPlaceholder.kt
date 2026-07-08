package dev.slne.surf.event.anarchy.papi.placeholder

import dev.slne.surf.api.paper.hook.papi.expansion.PapiPlaceholder
import dev.slne.surf.event.anarchy.vertborder.VertBorderManager
import dev.slne.surf.event.anarchy.vertborder.border.VerticalBorderAlignment
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

object TopBorderHeightPlaceholder : PapiPlaceholder("vert-border-top") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ) = VertBorderManager.currentHeight(Bukkit.getWorlds().first(), VerticalBorderAlignment.TOP)
        .toString()
}