package dev.slne.surf.event.base

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import javax.annotation.OverridingMethodsMustInvokeSuper

open class EventPlugin : SuspendingJavaPlugin() {

    @OverridingMethodsMustInvokeSuper
    override suspend fun onLoadAsync() {
        super.onLoadAsync()
    }

    @OverridingMethodsMustInvokeSuper
    override suspend fun onEnableAsync() {
        super.onEnableAsync()
    }

    @OverridingMethodsMustInvokeSuper
    override suspend fun onDisableAsync() {
        super.onDisableAsync()
    }

}