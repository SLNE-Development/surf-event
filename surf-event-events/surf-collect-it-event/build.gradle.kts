import dev.slne.surf.surfapi.gradle.util.registerRequired

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.collectit.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)
    authors.add("ammo")

    serverDependencies {
        registerRequired("FancyHolograms")
    }
}

dependencies {
    compileOnly("de.oliver:FancyHolograms:2.9.1")
}