package dev.slne.surf.event.anarchy.papi.placeholder

import dev.slne.surf.api.paper.hook.papi.expansion.PapiPlaceholder
import dev.slne.surf.event.anarchy.finale.FinaleLifecycle
import dev.slne.surf.event.anarchy.util.formatCountdownTime
import org.bukkit.OfflinePlayer

object TimeUntilFinalePlaceholder : PapiPlaceholder("time-until-finale") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ) = FinaleLifecycle.secondsUntilStart()
        ?.let { formatCountdownTime(it) }
        ?: "-"
}
