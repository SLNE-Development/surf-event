@file:Suppress("UnstableApiUsage")

package dev.slne.surf.event.collectit.dialogs.category

import dev.slne.surf.event.collectit.dialogs.MainDialog
import dev.slne.surf.event.collectit.dialogs.category.create.CreateCategoryDialog
import dev.slne.surf.event.collectit.dialogs.category.list.CategoryListDialog
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import io.papermc.paper.dialog.Dialog
import net.kyori.adventure.text.Component.text

object CategoryDialog {
    fun createDialog(): Dialog = dialog {
        base {
            title(text("Kategorien"))
        }
        type {
            multiAction {
                columns(1)
                
                action(listButton())
                action(createButton())

                exitAction {
                    label {
                        text("Zurück")
                    }
                    action {
                        playerCallback { player ->
                            player.showDialog(MainDialog.createDialog())
                        }
                    }
                }
            }
        }
    }

    private fun createButton() = actionButton {
        label(text("Erstellen"))
        action {
            playerCallback { player ->
                player.showDialog(CreateCategoryDialog.createDialog())
            }
        }
    }

    private fun listButton() = actionButton {
        label(text("Liste"))
        action {
            playerCallback { player ->
                player.showDialog(CategoryListDialog.createDialog())
            }
        }
    }
}