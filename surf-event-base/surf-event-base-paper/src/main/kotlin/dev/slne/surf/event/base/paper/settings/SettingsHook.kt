package dev.slne.surf.event.base.paper.settings

import dev.slne.surf.settings.api.surfSettingsApi
import java.util.*

val settingsHook = SettingsHook()

class SettingsHook {
    suspend fun createSettings() {
        surfSettingsApi.createSetting("player-visibility", PlayerVisibilityState.ALL.toString())
    }

    fun getSelectedState(playerUuid: UUID) =
        surfSettingsApi.getPlayerSetting(playerUuid, "player-visibility")
            ?.let { PlayerVisibilityState.valueOf(it.settingValue) }
            ?: PlayerVisibilityState.ALL

    suspend fun setState(playerUuid: UUID, state: PlayerVisibilityState) {
        surfSettingsApi.saveSetting(playerUuid, "player-visibility", state.toString())
    }
}