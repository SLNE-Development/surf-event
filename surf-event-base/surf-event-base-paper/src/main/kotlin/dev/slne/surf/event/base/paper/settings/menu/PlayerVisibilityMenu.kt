package dev.slne.surf.event.base.paper.settings.menu

import com.github.shynixn.mccoroutine.folia.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.event.base.paper.plugin
import dev.slne.surf.event.base.paper.settings.PlayerVisibilityState
import dev.slne.surf.event.base.paper.settings.playerVisibilityService
import dev.slne.surf.event.base.paper.settings.settingsHook
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

fun showPlayerVisibilityMenu(player: HumanEntity) {
    if (!plugin.hasSettingsHook()) {
        player.sendText {
            appendErrorPrefix()
            error("Das Einstellungen-Feature ist auf diesem Server nicht verfügbar.")
        }
        return
    }

    PlayerVisibilityMenu(player, settingsHook.getSelectedState(player.uniqueId)).show(player)
}

private const val height = 5
private const val width = 9

class PlayerVisibilityMenu(player: HumanEntity, initialState: PlayerVisibilityState) :
    ChestGui(height, ComponentHolder.of(buildText { spacer("Spielersichtbarkeit") })) {

    val pane = StaticPane(1, 1, width - 2, height - 2)
    var selectedState = initialState

    init {
        withOutClicks()
        withOutline(width, height)
        withBackButton(height)
        update()

        setOnClose {
            if (selectedState != initialState) {
                plugin.launch {
                    settingsHook.setState(player.uniqueId, selectedState)
                    playerVisibilityService.refreshState(
                        player as? Player
                            ?: error("Only players can have a player visibility state.")
                    )
                }
            }
        }
    }

    override fun update() {
        pane.addItem(GuiItem(buildItem(Material.GREEN_DYE) {
            displayName {
                note("Alle Spieler anzeigen")

                if (selectedState == PlayerVisibilityState.ALL) {
                    note(" (Aktuell ausgewählt)")
                    decorate(TextDecoration.BOLD)
                }
            }

            if (selectedState == PlayerVisibilityState.ALL) {
                editMeta {
                    it.setEnchantmentGlintOverride(true)
                }

                buildLore {
                    emptyLine()
                    line {
                        spacer("Gerade ausgewählt")
                    }
                }
            } else {
                buildLore {
                    emptyLine()
                    line {
                        spacer("Klicke, zum auswählen")
                    }
                }
            }
        }) {
            selectedState = PlayerVisibilityState.ALL
            it.whoClicked.playClickSound()
            it.whoClicked.sendText {
                appendSuccessPrefix()
                success("Du siehst jetzt alle Spieler.")
            }
            update()
        }, 0, 1)

        pane.addItem(GuiItem(buildItem(Material.YELLOW_DYE) {
            displayName {
                note("Nur Freunde anzeigen")

                if (selectedState == PlayerVisibilityState.FRIENDS) {
                    note(" (Aktuell ausgewählt)")
                    decorate(TextDecoration.BOLD)
                }
            }

            if (selectedState == PlayerVisibilityState.FRIENDS) {
                editMeta {
                    it.setEnchantmentGlintOverride(true)
                }

                buildLore {
                    emptyLine()
                    line {
                        spacer("Gerade ausgewählt")
                    }
                }
            } else {
                buildLore {
                    emptyLine()
                    line {
                        spacer("Klicke, zum auswählen")
                    }
                }
            }
        }) {
            selectedState = PlayerVisibilityState.FRIENDS
            it.whoClicked.playClickSound()
            it.whoClicked.sendText {
                appendSuccessPrefix()
                success("Du siehst jetzt nur Freunde.")
            }
            update()
        }, 2, 1)

        pane.addItem(GuiItem(buildItem(Material.ORANGE_DYE) {
            displayName {
                note("Nur VIPs anzeigen")

                if (selectedState == PlayerVisibilityState.VIP) {
                    note(" (Aktuell ausgewählt)")
                    decorate(TextDecoration.BOLD)
                }
            }

            if (selectedState == PlayerVisibilityState.VIP) {
                editMeta {
                    it.setEnchantmentGlintOverride(true)
                }

                buildLore {
                    emptyLine()
                    line {
                        spacer("Gerade ausgewählt")
                    }
                }
            } else {
                buildLore {
                    emptyLine()
                    line {
                        spacer("Klicke, zum auswählen")
                    }
                }
            }
        }) {
            selectedState = PlayerVisibilityState.VIP
            it.whoClicked.playClickSound()
            it.whoClicked.sendText {
                appendSuccessPrefix()
                success("Du siehst jetzt nur VIPs.")
            }
            update()
        }, 4, 1)

        pane.addItem(GuiItem(buildItem(Material.RED_DYE) {
            displayName {
                note("Keine Spieler anzeigen")

                if (selectedState == PlayerVisibilityState.NONE) {
                    note(" (Aktuell ausgewählt)")
                    decorate(TextDecoration.BOLD)
                }
            }

            if (selectedState == PlayerVisibilityState.NONE) {
                editMeta {
                    it.setEnchantmentGlintOverride(true)
                }

                buildLore {
                    emptyLine()
                    line {
                        spacer("Gerade ausgewählt")
                    }
                }
            } else {
                buildLore {
                    emptyLine()
                    line {
                        spacer("Klicke, zum auswählen")
                    }
                }
            }
        }) {
            selectedState = PlayerVisibilityState.NONE
            it.whoClicked.playClickSound()
            it.whoClicked.sendText {
                appendSuccessPrefix()
                success("Du siehst jetzt keine Spieler mehr.")
            }
            update()
        }, 6, 1)

        addPane(pane)

        super.update()
    }
}