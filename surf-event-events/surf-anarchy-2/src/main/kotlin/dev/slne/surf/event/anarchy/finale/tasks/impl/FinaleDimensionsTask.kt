package dev.slne.surf.event.anarchy.finale.tasks.impl

import com.github.shynixn.mccoroutine.folia.scope
import dev.slne.surf.event.anarchy.finale.tasks.FinaleTask
import dev.slne.surf.event.anarchy.plugin
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.seconds

object FinaleDimensionsTask : FinaleTask {
    override val runAfterFinaleStart = 5.seconds
    override var ranAt: ZonedDateTime? = null

    override suspend fun run() {
        plugin.scope.
    }
}