@file:Suppress("UnstableApiUsage")

package dev.slne.surf.event.collectit.dialogs.category.create

import dev.slne.surf.event.collectit.dialogs.category.list.CategoryListDialog
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import io.papermc.paper.dialog.Dialog
import net.kyori.adventure.text.Component

object CreateCategorySuccessNoticeDialog {
    fun createDialog(name: String): Dialog = dialog {
        base {
            title(Component.text("Erstellen - Erfolg"))

            body {
                plainMessage {
                    success("Die Kategorie ")
                    variableValue(name)
                    success(" wurde erfolgreich erstellt.")
                }
            }
        }
        type {
            notice(listButton())
        }
    }

    private fun listButton() = actionButton {
        label(Component.text("Zur Kategorie-Liste"))
        action {
            playerCallback { player ->
                player.showDialog(CategoryListDialog.createDialog())
            }
        }
    }
}