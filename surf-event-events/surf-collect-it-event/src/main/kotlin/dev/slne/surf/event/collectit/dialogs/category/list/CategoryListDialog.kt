@file:Suppress("UnstableApiUsage")

package dev.slne.surf.event.collectit.dialogs.category.list

import dev.slne.surf.event.collectit.dialogs.category.CategoryDialog
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import io.papermc.paper.dialog.Dialog

object CategoryListDialog {
    fun createDialog(): Dialog = dialog {
        base {
            title(text("Kategorien Liste"))
        }
        type {
            dialogList {
                columns(1)
                addAll(categoryDialogs())
                exitAction {
                    label {
                        text("Zurück")
                    }
                    action {
                        playerCallback { player ->
                            player.showDialog(CategoryDialog.createDialog())
                        }
                    }
                }
            }
        }
    }

    private fun categoryDialogs() = mutableObjectListOf<Dialog>()
}