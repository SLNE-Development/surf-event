package dev.slne.surf.event.anarchy.papi

import dev.slne.surf.api.paper.hook.papi.expansion.PapiExpansion
import dev.slne.surf.event.anarchy.papi.placeholder.BottomBorderHeightPlaceholder
import dev.slne.surf.event.anarchy.papi.placeholder.TimeUntilFinaleBorderPlaceholder
import dev.slne.surf.event.anarchy.papi.placeholder.TimeUntilFinalePlaceholder
import dev.slne.surf.event.anarchy.papi.placeholder.TopBorderHeightPlaceholder

object AnarchyPlaceholderExpansion : PapiExpansion(
    identifier = "anarchy",
    placeholder = listOf(
        TopBorderHeightPlaceholder,
        BottomBorderHeightPlaceholder,
        TimeUntilFinalePlaceholder,
        TimeUntilFinaleBorderPlaceholder
    ),
    author = "red"
)