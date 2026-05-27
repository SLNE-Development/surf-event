package dev.slne.surf.event.anarchy.util

import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder

fun SurfComponentBuilder.appendAnarchyPrefix() = append {
    darkSpacer(">>")
    appendSpace()
    primary("Anarchy")
    appendSpace()
    darkSpacer("|")
    appendSpace()
}