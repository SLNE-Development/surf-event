import dev.slne.surf.surfapi.gradle.util.registerSoft
import dev.slne.surf.surfapi.gradle.util.withSurfApiBukkit

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

group = "dev.slne.surf.event"
version = "1.0.0"

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.buildit.BuildItEvent")
    generateLibraryLoader(false)

    runServer {
        withSurfApiBukkit()
    }

    serverDependencies  {
        register("CommandAPI")
        register("WorldEdit")
        register("PlotSquared")
        register("PlaceholderAPI")
    }

}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven { url = uri("https://repo.extendedclip.com/releases/") }
}

dependencies {
    implementation(platform("com.intellectualsites.bom:bom-newest:1.55"))
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-bukkit") { isTransitive = false }
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly("me.clip:placeholderapi:2.11.7")
}