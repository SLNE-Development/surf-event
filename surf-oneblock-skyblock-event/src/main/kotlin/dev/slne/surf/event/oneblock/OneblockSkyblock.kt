package dev.slne.surf.event.oneblock

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.registerSuspendingEvents
import dev.slne.surf.event.base.EventPlugin
import dev.slne.surf.event.base.schematic.SchematicPaster
import dev.slne.surf.event.base.world.SpawnpointUtils
import dev.slne.surf.event.base.world.SpawnpointUtils.hasJoinedBefore
import dev.slne.surf.event.oneblock.world.VoidSpawnSchematic
import dev.slne.surf.event.oneblock.world.VoidWorldGenerator
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext

val oneblockPlugin: OneblockSkyblock
    get() = JavaPlugin.getPlugin(OneblockSkyblock::class.java)

class OneblockSkyblock : EventPlugin(), Listener {

    lateinit var world: World
    lateinit var schematicPaster: SchematicPaster

    override suspend fun onLoadAsync() {
        super.onLoadAsync()

        schematicPaster = SchematicPaster(this)
    }

    override suspend fun onEnableAsync() {
        super.onEnableAsync()

        val manager = Bukkit.getPluginManager()
        manager.registerSuspendingEvents(this, this, eventDispatcher)

        world = VoidWorldGenerator.generateVoidWorld("oneblock")
    }

    @EventHandler
    suspend fun PlayerJoinEvent.onJoin() {
        if (player.hasJoinedBefore()) {
            return
        }

        SpawnpointUtils.generateSpawnpoint(
            player,
            player.world,
            true,
            schematicPaster,
            VoidSpawnSchematic.SPAWN_SCHEMATIC,
            this@OneblockSkyblock
        )
    }

    private val eventDispatcher: Map<Class<out Event>, (event: Event) -> CoroutineContext> = mapOf(
        PlayerJoinEvent::class.java to {
            require(it is PlayerJoinEvent)
            oneblockPlugin.entityDispatcher(it.player)
        }
    )

}