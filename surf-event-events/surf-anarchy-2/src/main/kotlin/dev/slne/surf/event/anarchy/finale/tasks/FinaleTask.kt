package dev.slne.surf.event.anarchy.finale.tasks

import java.time.ZonedDateTime
import kotlin.time.Duration

interface FinaleTask {
    val runAfterFinaleStart: Duration
    var ranAt: ZonedDateTime?

    suspend fun run()
}