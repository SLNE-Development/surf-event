package dev.slne.surf.event.anarchy.finale

import com.github.shynixn.mccoroutine.folia.scope
import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.event.anarchy.finale.tasks.FinaleTask
import dev.slne.surf.event.anarchy.plugin
import kotlinx.coroutines.Job
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object FinaleManager {
    private lateinit var job: Job
    private val tasks = listOf<FinaleTask>()

    suspend fun startFinale() {
        val started = ZonedDateTime.now()

        job = plugin.scope.runAtFixedRate(1.seconds) {
            tasks.filter { shouldRun(it, started) }.forEach {
                it.run()
            }
        }
    }

    suspend fun resetTasks() {
        tasks.forEach { it.ranAt = null }
    }

    private fun shouldRun(task: FinaleTask, started: ZonedDateTime): Boolean {
        if (task.ranAt != null) {
            return false
        }

        val start = started.plus(task.runAfterFinaleStart.toJavaDuration())
        val now = ZonedDateTime.now()

        return now.isAfter(start)
    }
}