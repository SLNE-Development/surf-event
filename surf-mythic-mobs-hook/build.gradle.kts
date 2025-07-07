import dev.slne.surf.surfapi.gradle.util.registerRequired

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

repositories {
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.lumine:Mythic-Dist:5.9.0")
}

version = "1.0.0"
group = "dev.slne.surf.event.mythicmobs"

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.mythicmobs.PaperMain")
    generateLibraryLoader(false)
    authors.addAll("twisti", "kevin")

    serverDependencies {
        registerRequired("MythicMobs")
    }
}