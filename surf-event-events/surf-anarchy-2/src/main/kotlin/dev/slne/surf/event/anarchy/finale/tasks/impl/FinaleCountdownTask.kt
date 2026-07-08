package dev.slne.surf.event.anarchy.finale.tasks.impl

import dev.slne.surf.api.core.messages.adventure.key
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.messages.adventure.showTitle
import dev.slne.surf.api.paper.util.forEachPlayer
import dev.slne.surf.event.anarchy.finale.tasks.FinaleTask
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import dev.slne.surf.event.anarchy.util.geilesRot
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.seconds

object FinaleCountdownTask3 : FinaleTask {
    override val runAfterFinaleStart = 1.seconds
    override var ranAt: ZonedDateTime? = null

    override suspend fun run() {
        forEachPlayer {
            it.showTitle {
                title {
                    geilesRot("3")
                }

                subtitle {
                    spacer("Das Finale startet in 3 Sekunden...")
                }
            }

            it.sendText {
                appendAnarchyPrefix()
                info("Das Finale startet in ")
                variableValue("3 Sekunden")
                info("!")
            }

            it.playSound(true) {
                type(key("nexo", "countdown"))
            }
        }
    }
}

object FinaleCountdownTask2 : FinaleTask {
    override val runAfterFinaleStart = 2.seconds
    override var ranAt: ZonedDateTime? = null

    override suspend fun run() {
        forEachPlayer {
            it.showTitle {
                title {
                    geilesRot("2")
                }

                subtitle {
                    spacer("Das Finale startet in 2 Sekunden...")
                }
            }

            it.sendText {
                appendAnarchyPrefix()
                info("Das Finale startet in ")
                variableValue("2 Sekunden")
                info("!")
            }

            it.playSound(true) {
                type(key("nexo", "countdown"))
            }
        }
    }
}

object FinaleCountdownTask1 : FinaleTask {
    override val runAfterFinaleStart = 3.seconds
    override var ranAt: ZonedDateTime? = null

    override suspend fun run() {
        forEachPlayer {
            it.showTitle {
                title {
                    geilesRot("1")
                }

                subtitle {
                    spacer("Das Finale startet in 1 Sekunde...")
                }
            }

            it.sendText {
                appendAnarchyPrefix()
                info("Das Finale startet in ")
                variableValue("1 Sekunde")
                info("!")
            }

            it.playSound(true) {
                type(key("nexo", "countdown"))
            }
        }
    }
}

object FinaleCountdownTask0 : FinaleTask {
    override val runAfterFinaleStart = 4.seconds
    override var ranAt: ZonedDateTime? = null

    override suspend fun run() {
        forEachPlayer {
            it.showTitle {
                title {
                    geilesRot("")
                }

                subtitle {
                    spacer("Das Finale startet in 3 Sekunden...")
                }
            }

            it.sendText {
                appendAnarchyPrefix()
                info("Das Finale startet in ")
                variableValue("3 Sekunden")
                info("!")
            }

            it.playSound(true) {
                type(key("nexo", "start"))
            }
        }
    }
}