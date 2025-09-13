import dev.slne.surf.surfapi.gradle.util.registerRequired
import dev.slne.surf.surfapi.gradle.util.slnePublic

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.oneblock.PaperMain")
    generateLibraryLoader(false)

    serverDependencies {
        registerRequired("FastAsyncWorldEdit")
        registerRequired("FancyHolograms")
    }
}

repositories {
    slnePublic()
    maven("https://repo.fancyinnovations.com/releases")
}

dependencies {
    implementation("dev.slne.surf:surf-database:3.0.0-SNAPSHOT")

    implementation(platform("com.intellectualsites.bom:bom-newest:1.55")) // Ref: https://github.com/IntellectualSites/bom
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }

    compileOnly("de.oliver:FancyHolograms:2.7.0")
}