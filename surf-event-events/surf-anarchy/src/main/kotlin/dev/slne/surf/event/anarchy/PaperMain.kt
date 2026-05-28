package dev.slne.surf.event.anarchy

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.event.anarchy.corpse.CorpseDeathListener
import dev.slne.surf.event.anarchy.corpse.CorpseInteractionListener
import dev.slne.surf.event.anarchy.equipment.EquipmentListener
import dev.slne.surf.event.anarchy.equipment.equipmentCommand
import dev.slne.surf.event.anarchy.kills.KillDisplayListener
import dev.slne.surf.event.anarchy.kills.combatlog.CombatLogListener
import dev.slne.surf.event.anarchy.spectating.SpectatingListener
import dev.slne.surf.event.anarchy.spectating.spectatingCommand
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        equipmentCommand()
        spectatingCommand()
    }

    override suspend fun onEnableAsync() {
        EquipmentListener.register()
        KillDisplayListener.register()
        SpectatingListener.register()
        CombatLogListener.register()
        CorpseDeathListener.register()
        CorpseInteractionListener.register()
        CombatLogListener.createActionbar()
    }

    override suspend fun onDisableAsync() {
        super.onDisableAsync()
    }
}