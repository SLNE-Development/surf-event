package dev.slne.surf.event.randomdrops.config

import org.bukkit.World
import org.bukkit.entity.Player
import java.util.UUID

val GLOBAL_UUID: UUID = UUID(0L, 0L)

val isGlobalScope: Boolean get() = config.randomizationScope == RandomizationScope.GLOBAL
val isWorldScope:  Boolean get() = config.randomizationScope == RandomizationScope.WORLD
val isPlayerScope: Boolean get() = config.randomizationScope == RandomizationScope.PLAYER

fun effectiveUuid(playerUuid: UUID? = null, worldUid: UUID? = null): UUID =
    when (config.randomizationScope) {
        RandomizationScope.PLAYER -> requireNotNull(playerUuid) { "PLAYER scope needs playerUuid" }
        RandomizationScope.WORLD  -> requireNotNull(worldUid)   { "WORLD scope needs worldUid" }
        RandomizationScope.GLOBAL -> GLOBAL_UUID
    }

fun effectiveUuid(player: Player, world: World = player.world): UUID =
    effectiveUuid(player.uniqueId, world.uid)

// Legacy (nur für alte Call-Sites; besser obige Overloads verwenden)
@Deprecated("Use effectiveUuid(playerUuid, worldUid) so WORLD works")
fun effectiveUuidLegacy(uuid: UUID): UUID = if (isGlobalScope) GLOBAL_UUID else uuid
