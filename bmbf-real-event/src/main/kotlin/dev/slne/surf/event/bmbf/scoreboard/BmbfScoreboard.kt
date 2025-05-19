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
private val seperatorText = text("|", seperatorColor).decorate(TextDecoration.BOLD)

@OptIn(ObsoleteScoreboardApi::class)
fun createBmbfScoreboard(player: Player) =
    surfBukkitApi.createScoreboard(text("ꑎ"))
        .addEmptyLine()
        .addLine(
            text(
                "Grundstück-Info",
                seperatorColor
            ).decorate(TextDecoration.BOLD)
        )
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("Besitzer: ")
                text(
                    PlaceholderAPI.setPlaceholders(
                        player,
                        "%plotsquared_currentplot_owner%"
                    ).ifBlank { "/" },
                    Colors.WHITE
                )
            }
        }
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("Kategorie: ")
                text(
                    BmbfManager.currentCategory.displayName,
                    Colors.WHITE
                )
            }
        }
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("Zeit für Aufgabe: ")
                text(
                    BmbfManager.currentChallenge.eventDuration.toString(),
                    Colors.WHITE
                )
            }
        }
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("Bewertung: ")
                text(
                    PlaceholderAPI.setPlaceholders(
                        player,
                        "%plotsquared_currentplot_rating%"
                    ).ifBlank { "/" },
                    Colors.WHITE
                )
            }
        }
        .addEmptyLine()
        .addLine(
            text(
                "Socials",
                seperatorColor
            ).decorate(TextDecoration.BOLD)
        )
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("Discord: ")
                text("ꑏ/castcrafter", Colors.WHITE)
            }
        }
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("Twitch: ")
                text("ꑐ/castcrafter", Colors.WHITE)
            }
        }
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("YouTube: ")
                text("ꑑ/castcrafter", Colors.WHITE)
            }
        }
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("Instagram: ")
                text("ꑒ/castagram_", Colors.WHITE)
            }
        }
        .addEmptyLine()
        .addLine(
            text(
                "Server-Info",
                seperatorColor
            ).decorate(TextDecoration.BOLD)
        )
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("IP: ")
                text("castcrafter.de", Colors.WHITE)
            }
        }
        .addUpdatableLine {
            buildText {
                seperator()
                spacer("Docs: ")
                text("castcrafter.de/server", Colors.WHITE)
            }
        }
        .buildAutoUpdatable()

private fun SurfComponentBuilder.seperator() = append(seperatorText.appendSpace())