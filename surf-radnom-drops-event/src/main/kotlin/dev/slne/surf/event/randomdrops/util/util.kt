package dev.slne.surf.event.randomdrops.util

import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.entity.EntityType
import org.bukkit.loot.LootTables

fun EntityType.lootTableKey() = NamespacedKey.minecraft("entities/${key().value()}")
fun EntityType.lootTable() = Registry.LOOT_TABLES.get(lootTableKey())
fun EntityType.lootTableOrThrow(): LootTables = Registry.LOOT_TABLES.getOrThrow(lootTableKey())