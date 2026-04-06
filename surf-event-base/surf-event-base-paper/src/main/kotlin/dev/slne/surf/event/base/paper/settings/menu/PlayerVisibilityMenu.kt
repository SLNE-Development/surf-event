package dev.slne.surf.event.base.paper.settings.menu

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.paper.builder.buildItem
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.inventory.framework.dsl.slot
import dev.slne.surf.api.paper.inventory.framework.view.onClose
import dev.slne.surf.api.paper.inventory.framework.view.onFirstRender
import dev.slne.surf.api.paper.inventory.framework.view.settings
import dev.slne.surf.api.paper.inventory.framework.view.state.get
import dev.slne.surf.api.paper.inventory.framework.view.state.mutableState
import dev.slne.surf.api.paper.inventory.framework.view.state.set
import dev.slne.surf.api.paper.inventory.framework.view.surfView
import dev.slne.surf.event.base.paper.plugin
import dev.slne.surf.event.base.paper.settings.PlayerVisibilityService
import dev.slne.surf.event.base.paper.settings.PlayerVisibilityState
import dev.slne.surf.event.base.paper.settings.settingsHook


fun playerVisibilityView() = surfView("Eventeinstellungen") {
    val stateHolder = mutableState(PlayerVisibilityState.ALL)
    val initialState = mutableState(PlayerVisibilityState.ALL)

    settings {
        rows(3)
    }

    onFirstRender {
        stateHolder[this] = settingsHook.getSelectedState(player.uniqueId)
        initialState[this] = stateHolder[this]

        slot(4, 1) {
            renderWith {
                createItem(stateHolder[this@onFirstRender])
            }
            updateOnClick()
        }
    }

    onClose {
        val selectedState = stateHolder[this]

        if (selectedState != initialState[this]) {
            plugin.launch {
                settingsHook.setState(player.uniqueId, selectedState)
                PlayerVisibilityService.refreshState(player)
            }
        }
    }
}

fun createItem(state: PlayerVisibilityState) = buildItem(state.material) {
    displayName {
        variableValue(state.displayName)
    }
}