package dev.slne.surf.event.anarchy.finale.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.event.anarchy.util.PermissionList
import dev.slne.surf.event.anarchy.util.appendAnarchyPrefix
import dev.slne.surf.event.anarchy.vertborder.VertBorderManager
import dev.slne.surf.event.anarchy.vertborder.border.VerticalBorderAlignment
import org.bukkit.Bukkit
import kotlin.time.Duration.Companion.minutes

fun vertBorderCommand() = commandTree("vertborder") {
    withPermission(PermissionList.VERTBORDER_COMMAND)

    literalArgument("move") {
        multiLiteralArgument("type", "top", "bottom") {
            doubleArgument("height") {
                integerArgument("minutes") {
                    anyExecutor { sender, arguments ->
                        val type: String by arguments
                        val height: Double by arguments
                        val minutes: Int by arguments

                        val world = Bukkit.getWorlds().first()
                        val alignment =
                            if (type == "top") VerticalBorderAlignment.TOP else VerticalBorderAlignment.BOTTOM

                        VertBorderManager.moveBorderTo(world, alignment, height, minutes.minutes)

                        sender.sendText {
                            appendAnarchyPrefix()
                            success("Die vertikale Grenze wurde erfolgreich auf $height gesetzt und wird in $minutes Minuten erreicht. ($alignment)")
                        }
                    }
                }
            }
        }
    }

    literalArgument("get") {
        multiLiteralArgument("type", "top", "bottom") {
            anyExecutor { sender, arguments ->
                val type: String by arguments

                val world = Bukkit.getWorlds().first()
                val alignment =
                    if (type == "top") VerticalBorderAlignment.TOP else VerticalBorderAlignment.BOTTOM

                val currentHeight = VertBorderManager.currentHeight(world, alignment)

                sender.sendText {
                    appendAnarchyPrefix()
                    info("Die aktuelle Höhe der vertikalen Grenze ($alignment) beträgt: $currentHeight")
                }
            }
        }
    }

    literalArgument("stop") {
        multiLiteralArgument("type", "top", "bottom") {
            anyExecutor { sender, arguments ->
                val type: String by arguments

                val world = Bukkit.getWorlds().first()
                val alignment =
                    if (type == "top") VerticalBorderAlignment.TOP else VerticalBorderAlignment.BOTTOM

                VertBorderManager.stopAnimation(world, alignment)

                sender.sendText {
                    appendAnarchyPrefix()
                    success("Die Bewegung der vertikalen Grenze ($alignment) wurde erfolgreich gestoppt.")
                }
            }
        }
    }
}