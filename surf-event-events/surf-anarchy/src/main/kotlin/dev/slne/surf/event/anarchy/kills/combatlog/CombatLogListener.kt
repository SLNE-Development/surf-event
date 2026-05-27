package dev.slne.surf.event.anarchy.kills.combatlog

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.event.anarchy.plugin
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import org.bukkit.Bukkit
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

object CombatLogListener : Listener {
    private val damageCache = Caffeine.newBuilder()
        .expireAfterWrite(30.seconds)
        .build<UUID, DamageInfo>()

    private data class DamageInfo(
        val damager: UUID,
        val time: OffsetDateTime
    )

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        val damager = event.damager as? Player ?: return
        val damaged = event.entity as? Player ?: return

        damageCache.put(damaged.uniqueId, DamageInfo(damager.uniqueId, OffsetDateTime.now()))
        damageCache.put(damager.uniqueId, DamageInfo(damaged.uniqueId, OffsetDateTime.now()))
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.player
        val damageInfo = damageCache.getIfPresent(player.uniqueId) ?: return

        damageCache.invalidate(damageInfo.damager)
        damageCache.invalidate(player.uniqueId)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val damageInfo = damageCache.getIfPresent(player.uniqueId) ?: return
        val damager = Bukkit.getPlayer(damageInfo.damager)

        if (damager != null) {
            damager.sendText {
                appendAnarchyPrefix()
                error("Dein Gegner hat das Spiel verlassen! Du erhältst den Kill.")
            }

            player.kill(
                DamageSource
                    .builder(DamageType.PLAYER_ATTACK)
                    .withCausingEntity(damager)
                    .withDirectEntity(player)
                    .build()
            )
            damageCache.invalidate(player.uniqueId)
            damageCache.invalidate(damager.uniqueId)
            return
        }

        player.kill(
            DamageSource
                .builder(DamageType.PLAYER_ATTACK)
                .build()
        )

        damageCache.invalidate(player.uniqueId)
    }


    fun createActionbar() {
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
            damageCache.asMap().mapNotNull { Bukkit.getPlayer(it.key) to it.value }.forEach {
                val player = it.first ?: return@forEach
                val damageInfo = it.second
                val remainingSeconds = 30 - OffsetDateTime.now().second + damageInfo.time.second

                if (remainingSeconds > 0) {
                    player.sendActionBar(buildText {
                        error("Du bist im Kampf! Logge dich nicht aus.")
                        variableValue(" ($remainingSeconds Sekunden)")
                    })
                }
            }
        }, 1, 1, TimeUnit.SECONDS)
    }
}