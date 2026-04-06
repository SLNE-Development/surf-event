import dev.slne.surf.api.gradle.util.registerRequired

plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

dependencies {
    compileOnly("io.lumine:Mythic-Dist:5.9.0")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.mythicmobs.PaperMain")
    generateLibraryLoader(false)
    authors.addAll("twisti", "kevin")

    serverDependencies {
        registerRequired("MythicMobs")
    }
}