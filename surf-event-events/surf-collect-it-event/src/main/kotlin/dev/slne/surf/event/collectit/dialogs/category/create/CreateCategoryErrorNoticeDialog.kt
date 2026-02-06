@file:Suppress("UnstableApiUsage")

package dev.slne.surf.event.collectit.dialogs.category.create

import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import io.papermc.paper.dialog.Dialog
import net.kyori.adventure.text.Component

object CreateCategoryErrorNoticeDialog {
    fun createDialog(name: String, error: CategoryCreateError): Dialog = dialog {
        base {
            title(Component.text("Erstellen - Fehler"))

            body {
                plainMessage {
                    error("Die Kategorie ")
                    variableValue(name)
                    error(" konnte nicht erstellt werden.")

                    appendNewline(2)
                    variableKey("Informationen:")
                    appendNewline()
                    variableValue(error.name)
                }
            }
        }
        type {
            notice(backButton())
        }
    }

    private fun backButton() = actionButton {
        label(Component.text("Zurück"))
        action {
            customPlayerClick { response, player ->
                val input = response.getText("name")

                player.showDialog(CreateCategoryDialog.createDialog(input))
            }
        }
    }
}