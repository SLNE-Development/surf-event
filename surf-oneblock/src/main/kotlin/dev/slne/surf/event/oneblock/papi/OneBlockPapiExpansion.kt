package dev.slne.surf.event.oneblock.papi

import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiExpansion

class OneBlockPapiExpansion : PapiExpansion(
    "oneblock",
    listOf(
        LevelPlaceholder(),
        TotalBlocksGlobalPlaceholder(),
        PlayerNamePlaceholder(),
        TotalBlocksPlaceholder()
    )
)