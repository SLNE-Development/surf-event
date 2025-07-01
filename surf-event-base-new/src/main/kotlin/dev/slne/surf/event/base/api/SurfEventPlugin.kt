package dev.slne.surf.event.base.api

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.event.base.api.lifecycle.EventLifecycle
import dev.slne.surf.event.base.api.lifecycle.GlobalEventLifecycle
import dev.slne.surf.event.base.util.instantiateWithArgs
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import io.github.classgraph.ClassGraph

abstract class SurfEventPlugin(val eventName: String, scanPackages: List<String>? = null) :
    SuspendingJavaPlugin() {
    private val scanPackages: List<String> = scanPackages ?: listOf(this::class.java.packageName)
    internal val lifecycles = mutableObjectListOf<EventLifecycle>()
    internal val globalLifecycles = mutableObjectListOf<GlobalEventLifecycle>()

    init {
        require(eventName.isNotBlank()) { "Event name cannot be blank" }
        require(eventName.all { it.isLetterOrDigit() || it == '-' }) { "Event name can only contain letters, digits, and hyphens" }
    }

    final override suspend fun onLoadAsync() {
        ClassGraph()
            .enableClassInfo()
            .acceptPackages(*scanPackages.toTypedArray())
            .scan()
            .use { scanResult ->
                val lifecycleClasses = scanResult.getClassesImplementing(EventLifecycle::class.java)
                for (info in lifecycleClasses) {
                    val lifecycle =
                        instantiateWithArgs(info.loadClass().kotlin, this) as EventLifecycle
                    registerLifecycle(lifecycle)
                }
            }

        onLoadAsync0()
        for (lifecycle in globalLifecycles) {
            lifecycle.onLoad()
        }
    }

    final override suspend fun onEnableAsync() {
        for (lifecycle in globalLifecycles) {
            lifecycle.onEnable()
        }

        onEnableAsync0()
    }

    final override suspend fun onDisableAsync() {
        for (lifecycle in globalLifecycles) {
            lifecycle.onDisable()
        }

        onDisableAsync0()
    }

    open suspend fun onLoadAsync0() = Unit
    open suspend fun onEnableAsync0() = Unit
    open suspend fun onDisableAsync0() = Unit

    fun registerLifecycle(lifecycle: EventLifecycle) {
        if (lifecycle is GlobalEventLifecycle) {
            globalLifecycles.add(lifecycle)
        } else {
            lifecycles.add(lifecycle)
        }
    }

    fun unregisterLifecycle(lifecycle: EventLifecycle) {
        if (lifecycle is GlobalEventLifecycle) {
            globalLifecycles.remove(lifecycle)
        } else {
            lifecycles.remove(lifecycle)
        }
    }
}