package dev.slne.surf.event.anarchy.papi.placeholder

import dev.slne.surf.api.paper.hook.papi.expansion.PapiPlaceholder
import dev.slne.surf.event.anarchy.finale.FinaleLifecycle
import dev.slne.surf.event.anarchy.util.formatCountdownTime
import org.bukkit.OfflinePlayer

object TimeUntilFinaleBorderPlaceholder : PapiPlaceholder("time-until-finale-border") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ) = FinaleLifecycle.secondsUntilFinalBorder()
        ?.let { formatCountdownTime(it) }
        ?: "-"
}
