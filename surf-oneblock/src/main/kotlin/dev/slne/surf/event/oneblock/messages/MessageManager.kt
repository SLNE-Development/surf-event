package dev.slne.surf.event.oneblock.messages

import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder

object MessageManager {
    val unableToCreateIslandDisconnect = CommonComponents.renderDisconnectMessage(
        SurfComponentBuilder(),
        "DEINE INSEL KONNTE NICHT ERSTELLT WERDEN",
        {
            error("Beim Erstellen deiner Insel ist ein unerwarteter Fehler aufgetreten.")
        },
        issue = true
    )
}