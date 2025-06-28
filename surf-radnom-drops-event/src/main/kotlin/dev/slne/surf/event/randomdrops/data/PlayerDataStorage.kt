package dev.slne.surf.event.randomdrops.data

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.event.randomdrops.db.entities.BlockDropEntity
import dev.slne.surf.event.randomdrops.db.entities.MobDropEntity
import dev.slne.surf.event.randomdrops.db.tables.PlayerBlockDropsTable
import dev.slne.surf.event.randomdrops.db.tables.PlayerMobDropsTable
import dev.slne.surf.event.randomdrops.plugin
import dev.slne.surf.event.randomdrops.random.RandomDropSelector
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.key.Key
import org.bukkit.entity.EntityType
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

object PlayerDataStorage {
    private const val FLUSH_THRESHOLD = 1000

    private val blockDrops = mutableObject2ObjectMapOf<UUID, Object2ObjectMap<Key, Key>>()
    private val mobDrops =
        mutableObject2ObjectMapOf<UUID, Object2ObjectMap<EntityType, EntityType>>()

    private val flushLock = ReentrantLock()
    private val flushScheduled = AtomicBoolean(false)

    private val pendingBlockChanges = ConcurrentHashMap.newKeySet<Triple<UUID, Key, Key>>()
    private val pendingMobChanges =
        ConcurrentHashMap.newKeySet<Triple<UUID, EntityType, EntityType>>()
    val dirtyCounter = AtomicInteger(0)

    fun getOrCreateReplacedBlockDrop(uuid: UUID, original: Key): Key {
        val replacedDrops = blockDrops.computeIfAbsent(uuid) { mutableObject2ObjectMapOf() }
        return replacedDrops.computeIfAbsent(original) {
            RandomDropSelector.selectRandomBlockDrop().key().also {
                bufferBlockChange(uuid, original, it)
            }
        }
    }

    fun getOrCreateReplacedMobType(uuid: UUID, original: EntityType): EntityType {
        val replacements = mobDrops.computeIfAbsent(uuid) { mutableObject2ObjectMapOf() }
        return replacements[original] ?: run {
            repeat(10) {
                val candidate = RandomDropSelector.selectRandomEntityType()
                if (replacements.containsValue(candidate)) {
                    return@repeat
                }
                replacements[original] = candidate
                bufferMobChange(uuid, original, candidate)
                return candidate
            }
            RandomDropSelector.selectRandomEntityType().also {
                replacements[original] = it
                bufferMobChange(uuid, original, it)
            }
        }
    }

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
        flush(playerFilter = uuid)
        blockDrops.remove(uuid)
        mobDrops.remove(uuid)
    }

    suspend fun createTables() = newSuspendedTransaction {
        SchemaUtils.create(PlayerBlockDropsTable, PlayerMobDropsTable)
    }


    private fun bufferBlockChange(uuid: UUID, original: Key, replaced: Key) {
        pendingBlockChanges.add(Triple(uuid, original, replaced))
        markDirty()
    }

    private fun bufferMobChange(uuid: UUID, original: EntityType, replaced: EntityType) {
        pendingMobChanges.add(Triple(uuid, original, replaced))
        markDirty()
    }


    private fun markDirty() {
        if (dirtyCounter.incrementAndGet() > FLUSH_THRESHOLD) {
            scheduleFlush()
        }
    }

    private fun scheduleFlush() {
        if (flushScheduled.compareAndSet(false, true)) {
            plugin.launch {
                try {
                    flush()
                } finally {
                    flushScheduled.set(false)
                }
            }
        }
    }

    suspend fun flush(playerFilter: UUID? = null) {
        if (!flushLock.tryLock()) return
        try {
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
            dirtyCounter.addAndGet(-(blocksToSave.size + mobsToSave.size))
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
        } finally {
            flushLock.unlock()
        }

        if (dirtyCounter.get() >= FLUSH_THRESHOLD) scheduleFlush()
    }
}