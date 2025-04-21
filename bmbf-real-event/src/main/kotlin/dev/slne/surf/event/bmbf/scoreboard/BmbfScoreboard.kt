package dev.slne.surf.event.bmbf.scoreboard

import dev.slne.surf.event.bmbf.BmbfManager
import dev.slne.surf.surfapi.bukkit.api.scoreboard.ObsoleteScoreboardApi
import dev.slne.surf.surfapi.bukkit.api.surfBukkitApi
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

private val seperatorColor = TextColor.color(0x97B3F7)
private val seperatorText = text("|".toSmallCaps(), seperatorColor).decorate(TextDecoration.BOLD)

@OptIn(ObsoleteScoreboardApi::class)
fun createBmbfScoreboard(player: Player) =
    surfBukkitApi.createScoreboard(text("Event".toSmallCaps(), Colors.PRIMARY))
        .addEmptyLine()
        .addEmptyLine()
        .addLine(
            text(
                "Grundstück-Info".toSmallCaps(),
                seperatorColor
            ).decorate(TextDecoration.BOLD)
        )
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("Besitzer: ".toSmallCaps())
                text(
                    PlaceholderAPI.setPlaceholders(
                        player,
                        "%plotsquared_currentplot_owner%"
                    ).ifBlank { "/" }.toSmallCaps(),
                    Colors.WHITE
                )
            }
        }
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("Kategorie: ".toSmallCaps())
                text(
                    BmbfManager.currentCategory.displayName.toSmallCaps(),
                    Colors.WHITE
                )
            }
        }
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("Zeit für Aufgabe: ".toSmallCaps())
                text(
                    BmbfManager.currentChallenge.eventDuration.toString().toSmallCaps(),
                    Colors.WHITE
                )
            }
        }
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("Bewertung: ".toSmallCaps())
                text(
                    PlaceholderAPI.setPlaceholders(
                        player,
                        "%plotsquared_currentplot_rating%"
                    ).ifBlank { "/" }.toSmallCaps(),
                    Colors.WHITE
                )
            }
        }
        .addEmptyLine()
        .buildAutoUpdatable()

private fun SurfComponentBuilder.seperator() = append(seperatorText.appendSpace())