package dev.slne.surf.event.oneblock.papi

import dev.slne.surf.event.oneblock.papi.leaderboard.LeaderboardIsTopTenPlaceholder
import dev.slne.surf.event.oneblock.papi.leaderboard.LeaderboardOwnPlacePlaceholder
import dev.slne.surf.event.oneblock.papi.leaderboard.LeaderboardPlacePlaceholder
import dev.slne.surf.event.oneblock.plugin
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiExpansion

class OneBlockPapiExpansion : PapiExpansion(
    "oneblock",
    listOf(
        LevelPlaceholder(),
        TotalBlocksGlobalPlaceholder(),
        PlayerNamePlaceholder(),
        TotalBlocksPlaceholder(),
        LeaderboardPlacePlaceholder,
        LeaderboardOwnPlacePlaceholder,
        LeaderboardIsTopTenPlaceholder
    ),
    "twisti, Ammo, red",
    plugin.pluginMeta.version
)