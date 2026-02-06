import dev.slne.surf.surfapi.gradle.util.registerSoft
import dev.slne.surf.surfapi.gradle.util.withSurfApiBukkit

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.randomdrops.PaperMain")
    generateLibraryLoader(false)
    authors.add("twisti")

    runServer {
        withSurfApiBukkit()
    }

    serverDependencies {
        registerSoft("ChestProtect")
    }

}

dependencies {
    implementation("dev.slne.surf:surf-database:1.0.6-SNAPSHOT")
    compileOnly("com.github.angeschossen:ChestProtectAPI:5.19.16")
}