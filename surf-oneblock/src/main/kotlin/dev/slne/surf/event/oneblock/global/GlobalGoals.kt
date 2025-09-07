package dev.slne.surf.event.oneblock.global

import dev.slne.surf.event.oneblock.db.IslandService
import java.util.concurrent.atomic.AtomicLong

object GlobalGoals {
    private val total = AtomicLong(0)

    @Volatile
    private var lastIndex: Int = -1

    fun totalBlocks() = IslandService.all()
        .sumOf { it.totalMined }

    fun flush() {
    }

    fun onBlockMined() {

    }
}