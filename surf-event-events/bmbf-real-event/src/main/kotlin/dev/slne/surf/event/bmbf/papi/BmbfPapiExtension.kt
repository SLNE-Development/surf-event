package dev.slne.surf.event.bmbf.papi

import dev.slne.surf.event.bmbf.papi.countdown.BmbfCountdownPlaceholder
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiExpansion
import dev.slne.surf.surfapi.core.api.util.objectListOf

object BmbfPapiExtension: PapiExpansion(
    "bmbf",
    objectListOf(BmbfCountdownPlaceholder)
)