package dev.slne.surf.event.randomdrops.data

import dev.slne.surf.event.randomdrops.db.entities.BlockDropEntity
import dev.slne.surf.event.randomdrops.db.entities.MobDropEntity
import dev.slne.surf.event.randomdrops.db.tables.PlayerBlockDropsTable
import dev.slne.surf.event.randomdrops.db.tables.PlayerMobDropsTable
import dev.slne.surf.event.randomdrops.random.RandomDropSelector
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.actor
import net.kyori.adventure.key.Key
import org.bukkit.entity.EntityType
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object PlayerDataStorage {
    private const val FLUSH_THRESHOLD = 5000

    private val blockDrops = mutableObject2ObjectMapOf<UUID, Object2ObjectMap<Key, Key>>()
    private val mobDrops =
        mutableObject2ObjectMapOf<UUID, Object2ObjectMap<EntityType, EntityType>>()

    private val pendingBlockChanges = ConcurrentHashMap.newKeySet<Triple<UUID, Key, Key>>()
    private val pendingMobChanges =
        ConcurrentHashMap.newKeySet<Triple<UUID, EntityType, EntityType>>()

    private val flusher = CoroutineScope(Dispatchers.IO).actor<Unit>(capacity = 1) {
        for (m in channel) flush()
    }

    fun getOrCreateReplacedBlockDrop(uuid: UUID, original: Key): Key = blockDrops
        .computeIfAbsent(uuid) { mutableObject2ObjectMapOf() }
        .getOrCreateUnique(
            original,
            { RandomDropSelector.selectRandomBlockDrop().key() },
            uuid,
            ::bufferBlockChange
        )


    fun getOrCreateReplacedMobType(uuid: UUID, original: EntityType): EntityType = mobDrops
        .computeIfAbsent(uuid) { mutableObject2ObjectMapOf() }
        .getOrCreateUnique(
            original,
            { RandomDropSelector.selectRandomEntityType() },
            uuid,
            ::bufferMobChange
        )

    suspend fun loadCache(uuid: UUID) {
        newSuspendedTransaction(Dispatchers.IO) {
            BlockDropEntity.find { PlayerBlockDropsTable.uuid eq uuid }.forEach { entity ->
                val map = blockDrops.computeIfAbsent(uuid) { mutableObject2ObjectMapOf() }
                map[entity.originalKey] = entity.replacedKey
            }
            MobDropEntity.find { PlayerMobDropsTable.uuid eq uuid }.forEach { entity ->
                val map = mobDrops.computeIfAbsent(uuid) { mutableObject2ObjectMapOf() }
                map[entity.originalType] = entity.replacedType
            }
        }
    }

    suspend fun destroyCache(uuid: UUID) {
        flush(uuid)
        blockDrops.remove(uuid)
        mobDrops.remove(uuid)
    }

    suspend fun createTables() = newSuspendedTransaction {
        SchemaUtils.create(PlayerBlockDropsTable, PlayerMobDropsTable)
    }


    private fun bufferBlockChange(uuid: UUID, original: Key, replaced: Key) {
        pendingBlockChanges += Triple(uuid, original, replaced)
        scheduleFlushIfNeeded()
    }

    private fun bufferMobChange(uuid: UUID, original: EntityType, replaced: EntityType) {
        pendingMobChanges += Triple(uuid, original, replaced)
        scheduleFlushIfNeeded()
    }

    private fun scheduleFlushIfNeeded() {
        if (pendingBlockChanges.size + pendingMobChanges.size >= FLUSH_THRESHOLD) {
            flusher.trySend(Unit)
        }
    }

    suspend fun flush(playerFilter: UUID? = null) {
        val blocksToSave = mutableObjectListOf<Triple<UUID, Key, Key>>()
        val mobsToSave = mutableObjectListOf<Triple<UUID, EntityType, EntityType>>()

        pendingBlockChanges.removeIf { triple ->
            if (playerFilter == null || triple.first == playerFilter) {
                blocksToSave += triple
                true
            } else false
        }
        pendingMobChanges.removeIf { triple ->
            if (playerFilter == null || triple.first == playerFilter) {
                mobsToSave += triple
                true
            } else false
        }
        if (blocksToSave.isEmpty() && mobsToSave.isEmpty()) return

        newSuspendedTransaction(Dispatchers.IO) {
            PlayerBlockDropsTable.batchUpsert(
                blocksToSave,
                shouldReturnGeneratedValues = false
            ) { (uuid, original, replaced) ->
                this[PlayerBlockDropsTable.uuid] = uuid
                this[PlayerBlockDropsTable.originalKey] = original
                this[PlayerBlockDropsTable.replacedKey] = replaced
            }
            PlayerMobDropsTable.batchUpsert(
                mobsToSave,
                shouldReturnGeneratedValues = false
            ) { (uuid, original, replaced) ->
                this[PlayerMobDropsTable.uuid] = uuid
                this[PlayerMobDropsTable.originalType] = original
                this[PlayerMobDropsTable.replacedType] = replaced
            }
        }

        scheduleFlushIfNeeded()
    }

    private inline fun <K, V> Object2ObjectMap<K, V>.getOrCreateUnique(
        key: K,
        provider: () -> V,
        uuid: UUID,
        bufferChange: (UUID, K, V) -> Unit,
        tries: Int = 10
    ): V = getOrPut(key) {
        var attempts = 0
        var candidate = provider()
        while (containsValue(candidate) && attempts++ < tries) {
            candidate = provider()
        }
        bufferChange(uuid, key, candidate)
        candidate
    }
}