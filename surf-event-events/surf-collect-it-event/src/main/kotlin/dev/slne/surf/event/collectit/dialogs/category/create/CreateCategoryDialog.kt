@file:Suppress("UnstableApiUsage")

package dev.slne.surf.event.collectit.dialogs.category.create

import dev.slne.surf.event.collectit.dialogs.category.CategoryDialog
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import io.papermc.paper.dialog.Dialog
import net.kyori.adventure.text.Component

object CreateCategoryDialog {
    fun createDialog(defaultName: String? = null): Dialog = dialog {
        base {
            title(Component.text("Erstellen"))

            input {
                text("name") {
                    label(Component.text("Name der Kategorie"))
                    initial(defaultName ?: "")
                }
            }
        }
        type {
            confirmation(yesButton(), noButton())
        }
    }

    private fun yesButton() = actionButton {
        label(Component.text("Erstellen"))
        action {
            customPlayerClick { response, player ->
                val name = response.getText("name") ?: ""
                val (success, error) = Pair<Boolean, CategoryCreateError?>(true, null)

                if (success) {
                    player.showDialog(CreateCategorySuccessNoticeDialog.createDialog(name))
                } else {
                    player.showDialog(CreateCategoryErrorNoticeDialog.createDialog(name, error!!))
                }
            }
        }
    }

    private fun noButton() = actionButton {
        label(Component.text("Abbrechen"))
        action {
            playerCallback { player ->
                player.showDialog(CategoryDialog.createDialog())
            }
        }
    }
}