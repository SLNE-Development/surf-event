import dev.slne.surf.surfapi.gradle.util.registerRequired

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

dependencies {
    compileOnlyApi(libs.fawe.core)
    compileOnlyApi(libs.fawe.bukkit)
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.base.EventPlugin")
//    bootstrapper("dev.slne.surf.event.base.BukkitBootstrap")

    generateLibraryLoader(false)
    foliaSupported(false)

    authors.add("Ammo")

    serverDependencies {
        registerRequired("FastAsyncWorldEdit")
    }

    runServer {
        minecraftVersion("1.21.4")

        downloadPlugins {
            modrinth("fastasyncworldedit", "cf5QSDJ7")
        }
    }
}