package dev.slne.surf.event.anarchy.finale

import com.github.shynixn.mccoroutine.folia.scope
import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.event.anarchy.plugin
import kotlinx.coroutines.Job
import org.bukkit.command.CommandSender
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.seconds

object FinaleLifecycle {
    private var startAt: ZonedDateTime? = null
    private var startedAt: ZonedDateTime? = null

    private lateinit var checkJob: Job

    fun scheduleFinale(zonedDateTime: ZonedDateTime, commandSender: CommandSender) {
        startAt = zonedDateTime
        startedAt = null
    }

    fun isRunning() = startedAt != null
    fun isScheduled() = startAt != null

    fun cancelFinale(commandSender: CommandSender) {
        startAt = null
        startedAt = null
    }

    fun create() {
        checkJob = plugin.scope.runAtFixedRate(1.seconds) {
            if (startedAt == null && startAt != null && ZonedDateTime.now().isAfter(startAt)) {
                startedAt = ZonedDateTime.now()
                FinaleManager.startFinale()
            }
        }
    }

    fun cancel() {
        if (::checkJob.isInitialized && checkJob.isActive) {
            checkJob.cancel()
        }
    }
}