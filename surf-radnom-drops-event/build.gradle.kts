import dev.slne.surf.surfapi.gradle.util.withSurfApiBukkit

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

group = "dev.slne.surf.event"
version = "1.0.0"

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.randomdrops.PaperMain")
    generateLibraryLoader(false)
    authors.add("twisti")

    runServer {
        withSurfApiBukkit()
    }
}

dependencies {
    implementation("dev.slne.surf:surf-database:1.0.6-SNAPSHOT")
}