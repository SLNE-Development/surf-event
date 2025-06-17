package dev.slne.surf.event.hardcore.papi

import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiExpansion

class HardcorePapiHook: PapiExpansion(
    "hardcore",
    listOf(
        HardcoreDeathCountPlaceholder()
    )
)