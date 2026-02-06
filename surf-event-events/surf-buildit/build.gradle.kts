import dev.slne.surf.surfapi.gradle.util.withSurfApiBukkit

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.buildit.BuildItEvent")
    generateLibraryLoader(false)

    runServer {
        withSurfApiBukkit()
    }

    serverDependencies {
        register("CommandAPI")
        register("WorldEdit")
        register("PlotSquared")
        register("PlaceholderAPI")
    }

}

dependencies {
    implementation(platform("com.intellectualsites.bom:bom-newest:1.55"))
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-bukkit") { isTransitive = false }
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly("me.clip:placeholderapi:2.11.7")
}