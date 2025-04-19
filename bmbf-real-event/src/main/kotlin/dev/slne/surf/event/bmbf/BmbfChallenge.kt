package dev.slne.surf.event.bmbf

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

enum class BmbfChallenge(val eventDuration: Duration, val plotAreaSuffix: String) {
    MINUTE_1(1.minutes, "-1"),
    MINUTE_10(10.minutes, "-10"),
    MINUTE_30(30.minutes, "-30");

    fun getNext(): BmbfChallenge? = when (this) {
        MINUTE_1 -> MINUTE_10
        MINUTE_10 -> MINUTE_30
        MINUTE_30 -> null
    }
}