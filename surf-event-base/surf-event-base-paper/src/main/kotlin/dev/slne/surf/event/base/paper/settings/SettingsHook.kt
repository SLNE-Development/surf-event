package dev.slne.surf.event.base.paper.settings

import dev.slne.surf.settings.api.SurfSettingsApi
import java.util.*

val settingsHook = SettingsHook()

class SettingsHook {
    suspend fun createSettings() {
        SurfSettingsApi.createSetting("player-visibility", PlayerVisibilityState.ALL.toString())
    }

    fun getSelectedState(playerUuid: UUID) =
        SurfSettingsApi.getPlayerSetting(playerUuid, "player-visibility")
            ?.let { PlayerVisibilityState.valueOf(it.settingValue) }
            ?: PlayerVisibilityState.ALL

    suspend fun setState(playerUuid: UUID, state: PlayerVisibilityState) {
        SurfSettingsApi.saveSetting(playerUuid, "player-visibility", state.toString())
    }
}