package dev.slne.surf.event.anarchy.finale.listener

import dev.slne.surf.api.core.luckperms.LuckPermsAccess
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.event.anarchy.finale.FinaleLifecycle
import dev.slne.surf.event.anarchy.util.PermissionList
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

object FinaleConnectionListener : Listener {
    @EventHandler
    fun onConnect(event: AsyncPlayerPreLoginEvent) {
        if (!FinaleLifecycle.isRunning()) {
            return
        }

        runBlocking {
            val luckpermsUser =
                LuckPermsAccess.getUser(event.uniqueId) ?: LuckPermsAccess.loadUser(event.uniqueId)

            if (luckpermsUser.cachedData.permissionData.checkPermission(PermissionList.FINALE_JOIN)
                    .asBoolean()
            ) {
                return@runBlocking
            }

            event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                CommonComponents.renderDisconnectMessage(
                    SurfComponentBuilder(),
                    "DAS ANARCHY-FINALE LÄUFT BEREITS!",
                    {
                        spacer("Das Finale ist bereits gestartet und kannst nicht mehr beitreten.")
                    },
                    { appendDiscordLink() })
            )
        }
    }
}