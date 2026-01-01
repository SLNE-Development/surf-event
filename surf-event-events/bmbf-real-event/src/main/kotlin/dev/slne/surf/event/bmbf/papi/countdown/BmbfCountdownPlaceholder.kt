package dev.slne.surf.event.bmbf.papi.countdown

import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import org.bukkit.OfflinePlayer
import kotlin.time.Duration.Companion.seconds

object BmbfCountdownPlaceholder : PapiPlaceholder("countdown") {
    var current = 0.seconds

    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String? {
        return current.toComponents { hours, minutes, seconds, nanos ->
            "%1$02d:%2$02d:%3$02d".format(
                hours,
                minutes,
                seconds
            )
        }
    }
}