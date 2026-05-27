import dev.slne.surf.api.gradle.util.registerRequired

plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.anarchy.PaperMain")
    generateLibraryLoader(false)

    version = "2.0.0"

    authors.addAll("red")

    serverDependencies {
        registerRequired("FancyHolograms")
        registerRequired("surf-nametag-paper")
    }
}

dependencies {
    compileOnly("de.oliver:FancyHolograms:2.7.0")
    compileOnly("dev.slne.surf.nametag:surf-nametag-api:1.0.8-SNAPSHOT")
}