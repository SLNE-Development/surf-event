package dev.slne.surf.event.bmbf

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

enum class BmbfChallenge(val eventDuration: Duration, val plotAreaSuffix: String) {
    MINUTE_10(10.minutes, "-10"),
    MINUTE_60(60.minutes, "-60"),
    MINUTE_1440(1440.minutes, "-1440");

    fun getNext(): BmbfChallenge? = when (this) {
        MINUTE_10 -> MINUTE_60
        MINUTE_60 -> MINUTE_1440
        MINUTE_1440 -> null
    }
}