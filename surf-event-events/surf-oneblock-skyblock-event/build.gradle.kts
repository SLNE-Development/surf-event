plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnlyApi(libs.fawe.core)
    compileOnlyApi(libs.fawe.bukkit)
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.oneblock.OneblockSkyblock")

    generateLibraryLoader(false)
    foliaSupported(false)

    authors.add("Ammo")
}