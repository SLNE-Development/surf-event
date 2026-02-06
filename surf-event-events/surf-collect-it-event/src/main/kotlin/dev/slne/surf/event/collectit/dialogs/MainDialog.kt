@file:Suppress("UnstableApiUsage")
@file:OptIn(NmsUseWithCaution::class)

package dev.slne.surf.event.collectit.dialogs

import dev.slne.surf.event.collectit.dialogs.category.CategoryDialog
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.clearDialogs
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.bukkit.api.nms.NmsUseWithCaution
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import io.papermc.paper.dialog.Dialog

object MainDialog {
    fun createDialog(): Dialog = dialog {
        base {
            title(text("Hauptmenü"))
        }
        type {
            multiAction {
                columns(1)
                
                action(categoriesButton())
                action(uncategorizedItemsButton())

                exitAction {
                    label {
                        text("Schließen")
                    }
                    action {
                        playerCallback { player ->
                            player.clearDialogs()
                        }
                    }
                }
            }
        }
    }

    private fun categoriesButton() = actionButton {
        label {
            text("Kategorien")
        }
        action {
            playerCallback { player ->
                player.showDialog(CategoryDialog.createDialog())
            }
        }
    }

    private fun uncategorizedItemsButton() = actionButton {
        label {
            text("Unkategorisierte Items")
        }
        action {
            playerCallback { player ->

            }
        }
    }
}