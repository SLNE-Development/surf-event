import dev.slne.surf.surfapi.gradle.util.slnePublic

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.oneblock.PaperMain")
    generateLibraryLoader(false)
}

repositories {
    slnePublic()
}

dependencies {
    implementation("dev.slne.surf:surf-database:3.0.0-SNAPSHOT")
}