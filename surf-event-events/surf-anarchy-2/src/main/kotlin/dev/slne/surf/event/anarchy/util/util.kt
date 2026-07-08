package dev.slne.surf.event.anarchy.util

import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import java.time.Duration
import java.time.ZonedDateTime

fun SurfComponentBuilder.appendAnarchyPrefix() = append {
    spacer("»")
    appendSpace()
    note("CC")
    appendSpace()
    darkSpacer("|")
    appendSpace()
}


fun SurfComponentBuilder.geilesRot(text: String, vararg decoration: TextDecoration) =
    text(text, TextColor.color(227, 0, 58), *decoration)

fun formatDurationUntil(date: ZonedDateTime): String {
    val duration = Duration.between(ZonedDateTime.now(), date)

    val days = duration.toDays()
    val hours = duration.toHours() % 24
    val minutes = duration.toMinutes() % 60

    return when {
        days > 0 && hours > 0 -> "$days ${if (days == 1L) "Tag" else "Tage"} und $hours ${if (hours == 1L) "Stunde" else "Stunden"}"
        days > 0 -> "$days ${if (days == 1L) "Tag" else "Tage"}"
        hours > 0 && minutes > 0 -> "$hours ${if (hours == 1L) "Stunde" else "Stunden"} und $minutes ${if (minutes == 1L) "Minute" else "Minuten"}"
        hours > 0 -> "$hours ${if (hours == 1L) "Stunde" else "Stunden"}"
        minutes > 0 -> "$minutes ${if (minutes == 1L) "Minute" else "Minuten"}"
        else -> "wenigen Sekunden"
    }
}

/**
 * Formats a whole-unit countdown mark (e.g. 7 days, 12 hours, 30 minutes, 10 seconds) into a
 * single-unit German label. The countdown only ever broadcasts clean single-unit marks, so the
 * largest non-zero unit is always sufficient.
 */
fun formatCountdownTime(totalSeconds: Long): String {
    val days = totalSeconds / 86_400
    val hours = totalSeconds % 86_400 / 3_600
    val minutes = totalSeconds % 3_600 / 60
    val seconds = totalSeconds % 60

    return when {
        days > 0 -> "$days ${if (days == 1L) "Tag" else "Tage"}"
        hours > 0 -> "$hours ${if (hours == 1L) "Stunde" else "Stunden"}"
        minutes > 0 -> "$minutes ${if (minutes == 1L) "Minute" else "Minuten"}"
        else -> "$seconds ${if (seconds == 1L) "Sekunde" else "Sekunden"}"
    }
}