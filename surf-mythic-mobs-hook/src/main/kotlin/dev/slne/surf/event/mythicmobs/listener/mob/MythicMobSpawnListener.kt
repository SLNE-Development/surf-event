package dev.slne.surf.event.mythicmobs.listener.mob

import dev.slne.surf.event.mythicmobs.mythic
import io.lumine.mythic.bukkit.BukkitAdapter
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Enderman
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.TrialSpawnerSpawnEvent
import kotlin.jvm.optionals.getOrNull

object MythicMobSpawnListener : Listener {
    @EventHandler
    fun onSpawnerSpawn(event: TrialSpawnerSpawnEvent) {
        val entity = event.entity
        if (entity !is Enderman) return
        val customName = entity.customName() ?: return
        val plainName = PlainTextComponentSerializer.plainText().serialize(customName)

        val mob = mythic.mobManager.getMythicMob(plainName).getOrNull() ?: return
        event.isCancelled = true

        val creature = mob.spawn(BukkitAdapter.adapt(event.location), 1.0)
        val bukkitEntity = creature.entity.bukkitEntity
        event.trialSpawner.startTrackingEntity(bukkitEntity)
    }
}