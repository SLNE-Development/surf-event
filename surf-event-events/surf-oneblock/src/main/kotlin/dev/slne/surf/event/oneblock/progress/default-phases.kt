package dev.slne.surf.event.oneblock.progress

import dev.slne.surf.event.oneblock.progress.PhaseConfig.*
import org.bukkit.entity.EntityType

val defaultPhases = listOf(
    Phase(
        id = "start_plains",
        startsAt = 0,
        weight = 1,
        parents = emptyList(),
        blocks = listOf(
            BlockEntry(data = "minecraft:grass_block", weight = 40),
            BlockEntry(data = "minecraft:dirt", weight = 35),
            BlockEntry(data = "minecraft:stone", weight = 5),
            BlockEntry(data = "minecraft:oak_log", weight = 15),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.CHICKEN, weight = 5.0),
            EntityEntry(type = EntityType.COW, weight = 3.0),
        )
    ),
    Phase(
        id = "early_mining",
        startsAt = 150,
        weight = 2,
        parents = listOf(ParentPhase("start_plains")),
        blocks = listOf(
            BlockEntry(data = "minecraft:stone", weight = 50),
            BlockEntry(data = "minecraft:coal_ore", weight = 20),
            BlockEntry(data = "minecraft:copper_ore", weight = 15),
            BlockEntry(data = "minecraft:dirt", weight = 15),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.BAT, weight = 5.0),
            EntityEntry(type = EntityType.ZOMBIE, weight = 2.0),
        )
    ),
    Phase(
        id = "early_caves",
        startsAt = 350,
        weight = 2,
        parents = listOf(ParentPhase("early_mining")),
        blocks = listOf(
            BlockEntry(data = "minecraft:stone", weight = 40),
            BlockEntry(data = "minecraft:coal_ore", weight = 25),
            BlockEntry(data = "minecraft:iron_ore", weight = 15),
            BlockEntry(data = "minecraft:gravel", weight = 10),
            BlockEntry(data = "minecraft:cobblestone", weight = 10),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.ZOMBIE, weight = 4.0),
            EntityEntry(type = EntityType.SPIDER, weight = 3.0),
        )
    ),
    Phase(
        id = "iron_age",
        startsAt = 700,
        weight = 3,
        parents = listOf(ParentPhase("early_caves")),
        blocks = listOf(
            BlockEntry(data = "minecraft:stone", weight = 35),
            BlockEntry(data = "minecraft:iron_ore", weight = 30),
            BlockEntry(data = "minecraft:coal_ore", weight = 20),
            BlockEntry(data = "minecraft:copper_ore", weight = 10),
            BlockEntry(data = "minecraft:gravel", weight = 5),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.ZOMBIE, weight = 5.0),
            EntityEntry(type = EntityType.SKELETON, weight = 3.0),
        )
    ),
    Phase(
        id = "abandoned_mineshaft",
        startsAt = 1100,
        weight = 3,
        parents = listOf(ParentPhase("iron_age")),
        blocks = listOf(
            BlockEntry(data = "minecraft:oak_planks", weight = 20),
            BlockEntry(data = "minecraft:oak_log", weight = 15),
            BlockEntry(data = "minecraft:cobweb", weight = 10),
            BlockEntry(data = "minecraft:rail", weight = 5),
            BlockEntry(data = "minecraft:stone", weight = 50),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.CAVE_SPIDER, weight = 4.0),
            EntityEntry(type = EntityType.SPIDER, weight = 3.0),
        )
    ),
    Phase(
        id = "deep_caves",
        startsAt = 1600,
        weight = 4,
        parents = listOf(ParentPhase("iron_age")),
        blocks = listOf(
            BlockEntry(data = "minecraft:deepslate", weight = 40),
            BlockEntry(data = "minecraft:iron_ore", weight = 20),
            BlockEntry(data = "minecraft:gold_ore", weight = 15),
            BlockEntry(data = "minecraft:redstone_ore", weight = 15),
            BlockEntry(data = "minecraft:gravel", weight = 10),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.SKELETON, weight = 5.0),
            EntityEntry(type = EntityType.ZOMBIE, weight = 5.0),
        )
    ),
    Phase(
        id = "redstone_labs",
        startsAt = 2200,
        weight = 4,
        parents = listOf(ParentPhase("deep_caves")),
        blocks = listOf(
            BlockEntry(data = "minecraft:redstone_ore", weight = 35),
            BlockEntry(data = "minecraft:deepslate", weight = 30),
            BlockEntry(data = "minecraft:piston", weight = 5),
            BlockEntry(data = "minecraft:observer", weight = 5),
            BlockEntry(data = "minecraft:stone", weight = 25),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.WITCH, weight = 2.0),
            EntityEntry(type = EntityType.ZOMBIE, weight = 4.0),
        )
    ),
    Phase(
        id = "lava_depths",
        startsAt = 3000,
        weight = 5,
        parents = listOf(ParentPhase("deep_caves")),
        blocks = listOf(
            BlockEntry(data = "minecraft:basalt", weight = 30),
            BlockEntry(data = "minecraft:magma_block", weight = 15),
            BlockEntry(data = "minecraft:obsidian", weight = 10),
            BlockEntry(data = "minecraft:deepslate", weight = 45),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.MAGMA_CUBE, weight = 4.0),
            EntityEntry(type = EntityType.ZOMBIE, weight = 4.0),
        )
    ),
    Phase(
        id = "dripstone_caves",
        startsAt = 3600,
        weight = 5,
        parents = listOf(ParentPhase("deep_caves")),
        blocks = listOf(
            BlockEntry(data = "minecraft:dripstone_block", weight = 40),
            BlockEntry(data = "minecraft:pointed_dripstone", weight = 20),
            BlockEntry(data = "minecraft:calcite", weight = 20),
            BlockEntry(data = "minecraft:stone", weight = 20),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.GLOW_SQUID, weight = 4.0),
            EntityEntry(type = EntityType.ZOMBIE, weight = 3.0),
        )
    ),
    Phase(
        id = "lush_caves",
        startsAt = 4200,
        weight = 5,
        parents = listOf(ParentPhase("deep_caves")),
        blocks = listOf(
            BlockEntry(data = "minecraft:moss_block", weight = 30),
            BlockEntry(data = "minecraft:clay", weight = 25),
            BlockEntry(data = "minecraft:azalea", weight = 20),
            BlockEntry(data = "minecraft:stone", weight = 25),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.AXOLOTL, weight = 3.0),
            EntityEntry(type = EntityType.ZOMBIE, weight = 3.0),
        )
    ),
    Phase(
        id = "nether_entry",
        startsAt = 5000,
        weight = 6,
        parents = listOf(ParentPhase("lava_depths")),
        blocks = listOf(
            BlockEntry(data = "minecraft:netherrack", weight = 60),
            BlockEntry(data = "minecraft:nether_quartz_ore", weight = 20),
            BlockEntry(data = "minecraft:nether_gold_ore", weight = 15),
            BlockEntry(data = "minecraft:magma_block", weight = 5),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.ZOMBIFIED_PIGLIN, weight = 5.0),
            EntityEntry(type = EntityType.GHAST, weight = 1.0),
        )
    ),
    Phase(
        id = "nether_fortress",
        startsAt = 5800,
        weight = 7,
        parents = listOf(ParentPhase("nether_entry")),
        blocks = listOf(
            BlockEntry(data = "minecraft:nether_bricks", weight = 40),
            BlockEntry(data = "minecraft:soul_sand", weight = 20),
            BlockEntry(data = "minecraft:nether_wart_block", weight = 10),
            BlockEntry(data = "minecraft:netherrack", weight = 30),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.BLAZE, weight = 4.0),
            EntityEntry(type = EntityType.WITHER_SKELETON, weight = 3.0),
        )
    ),
    Phase(
        id = "crimson_forest",
        startsAt = 6500,
        weight = 7,
        parents = listOf(ParentPhase("nether_entry")),
        blocks = listOf(
            BlockEntry(data = "minecraft:crimson_nylium", weight = 30),
            BlockEntry(data = "minecraft:crimson_stem", weight = 25),
            BlockEntry(data = "minecraft:shroomlight", weight = 15),
            BlockEntry(data = "minecraft:netherrack", weight = 30),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.HOGLIN, weight = 4.0),
            EntityEntry(type = EntityType.PIGLIN, weight = 4.0),
        )
    ),
    Phase(
        id = "warped_forest",
        startsAt = 7200,
        weight = 7,
        parents = listOf(ParentPhase("nether_entry")),
        blocks = listOf(
            BlockEntry(data = "minecraft:warped_nylium", weight = 30),
            BlockEntry(data = "minecraft:warped_stem", weight = 25),
            BlockEntry(data = "minecraft:shroomlight", weight = 15),
            BlockEntry(data = "minecraft:netherrack", weight = 30),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.ENDERMAN, weight = 6.0),
            EntityEntry(type = EntityType.PIGLIN, weight = 2.0),
        )
    ),
    Phase(
        id = "basalt_deltas",
        startsAt = 7800,
        weight = 8,
        parents = listOf(ParentPhase("nether_entry")),
        blocks = listOf(
            BlockEntry(data = "minecraft:basalt", weight = 50),
            BlockEntry(data = "minecraft:blackstone", weight = 30),
            BlockEntry(data = "minecraft:magma_block", weight = 20),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.MAGMA_CUBE, weight = 5.0),
            EntityEntry(type = EntityType.GHAST, weight = 2.0),
        )
    ),
    Phase(
        id = "diamond_depths",
        startsAt = 9000,
        weight = 8,
        parents = listOf(ParentPhase("deep_caves")),
        blocks = listOf(
            BlockEntry(data = "minecraft:deepslate", weight = 50),
            BlockEntry(data = "minecraft:diamond_ore", weight = 15),
            BlockEntry(data = "minecraft:redstone_ore", weight = 15),
            BlockEntry(data = "minecraft:lapis_ore", weight = 10),
            BlockEntry(data = "minecraft:emerald_ore", weight = 10),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.ENDERMAN, weight = 4.0),
            EntityEntry(type = EntityType.SKELETON, weight = 4.0),
        )
    ),
    Phase(
        id = "ancient_city",
        startsAt = 10000,
        weight = 9,
        parents = listOf(ParentPhase("diamond_depths")),
        blocks = listOf(
            BlockEntry(data = "minecraft:sculk", weight = 40),
            BlockEntry(data = "minecraft:sculk_catalyst", weight = 10),
            BlockEntry(data = "minecraft:deepslate_bricks", weight = 30),
            BlockEntry(data = "minecraft:reinforced_deepslate", weight = 1),
            BlockEntry(data = "minecraft:deepslate", weight = 19),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.WARDEN, weight = 0.5),
            EntityEntry(type = EntityType.SKELETON, weight = 4.0),
        )
    ),
    Phase(
        id = "stronghold",
        startsAt = 11000,
        weight = 9,
        parents = listOf(ParentPhase("diamond_depths")),
        blocks = listOf(
            BlockEntry(data = "minecraft:stone_bricks", weight = 40),
            BlockEntry(data = "minecraft:cracked_stone_bricks", weight = 15),
            BlockEntry(data = "minecraft:bookshelf", weight = 10),
            BlockEntry(data = "minecraft:end_portal_frame", weight = 1),
            BlockEntry(data = "minecraft:stone", weight = 34),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.SILVERFISH, weight = 6.0),
            EntityEntry(type = EntityType.ENDERMAN, weight = 3.0),
        )
    ),
    Phase(
        id = "the_end",
        startsAt = 13000,
        weight = 10,
        parents = listOf(ParentPhase("stronghold")),
        blocks = listOf(
            BlockEntry(data = "minecraft:end_stone", weight = 70),
            BlockEntry(data = "minecraft:obsidian", weight = 20),
            BlockEntry(data = "minecraft:purpur_block", weight = 10),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.ENDERMAN, weight = 8.0),
            EntityEntry(type = EntityType.SHULKER, weight = 2.0),
        )
    ),
    Phase(
        id = "end_cities",
        startsAt = 15000,
        weight = 11,
        parents = listOf(ParentPhase("the_end")),
        blocks = listOf(
            BlockEntry(data = "minecraft:purpur_block", weight = 40),
            BlockEntry(data = "minecraft:end_stone", weight = 30),
            BlockEntry(data = "minecraft:end_rod", weight = 10),
            BlockEntry(data = "minecraft:shulker_box", weight = 5),
            BlockEntry(data = "minecraft:obsidian", weight = 15),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.SHULKER, weight = 6.0),
            EntityEntry(type = EntityType.ENDERMAN, weight = 4.0),
        )
    ),
    Phase(
        id = "overworld_chaos",
        startsAt = 17000,
        weight = 12,
        parents = listOf(ParentPhase("the_end")),
        blocks = listOf(
            BlockEntry(data = "minecraft:stone", weight = 30),
            BlockEntry(data = "minecraft:deepslate", weight = 30),
            BlockEntry(data = "minecraft:obsidian", weight = 20),
            BlockEntry(data = "minecraft:diamond_block", weight = 5),
            BlockEntry(data = "minecraft:ancient_debris", weight = 1),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.WITHER, weight = 0.5),
            EntityEntry(type = EntityType.ENDERMAN, weight = 6.0),
        )
    ),
    Phase(
        id = "event_finale",
        startsAt = 20000,
        weight = 13,
        parents = listOf(ParentPhase("overworld_chaos")),
        blocks = listOf(
            BlockEntry(data = "minecraft:ancient_debris", weight = 10),
            BlockEntry(data = "minecraft:netherite_block", weight = 1),
            BlockEntry(data = "minecraft:diamond_block", weight = 10),
            BlockEntry(data = "minecraft:obsidian", weight = 40),
            BlockEntry(data = "minecraft:stone", weight = 39),
        ),
        entities = listOf(
            EntityEntry(type = EntityType.ENDER_DRAGON, weight = 0.2),
            EntityEntry(type = EntityType.WITHER, weight = 0.5),
            EntityEntry(type = EntityType.ENDERMAN, weight = 6.0),
        )
    )
)