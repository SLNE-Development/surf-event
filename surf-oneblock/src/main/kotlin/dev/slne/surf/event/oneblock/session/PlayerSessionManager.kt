package dev.slne.surf.event.oneblock.session

import com.github.benmanes.caffeine.cache.Caffeine
import java.util.*

object PlayerSessionManager {
    private val sessions = Caffeine.newBuilder()
        .build<UUID, PlayerSession>()

    fun createSession(uuid: UUID): PlayerSession {
        val session = PlayerSession(uuid)
        sessions.put(uuid, session)
        return session
    }

    fun getSession(uuid: UUID) =
        sessions.getIfPresent(uuid) ?: error("Session for player $uuid does not exist!")

    fun clearSession(uuid: UUID) {
        sessions.invalidate(uuid)
    }
}