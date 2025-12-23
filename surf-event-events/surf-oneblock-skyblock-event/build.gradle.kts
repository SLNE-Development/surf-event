import dev.slne.surf.surfapi.gradle.util.registerRequired

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

repositories {
    mavenCentral()
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.oneblock.OneblockSkyblock")
//    bootstrapper("dev.slne.surf.event.oneblock.BukkitBootstrap")

    generateLibraryLoader(false)
    foliaSupported(false)

    authors.add("Ammo")

    serverDependencies {
        registerRequired("surf-event-base")
    }

    runServer {
        minecraftVersion("1.21.4")

        downloadPlugins {
        }
    }
}