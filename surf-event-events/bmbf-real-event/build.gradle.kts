import dev.slne.surf.surfapi.gradle.util.registerRequired

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

version = "1.21.4-1.0.0"

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.bmbf.PaperMain")
    authors.add("twisti")
    generateLibraryLoader(false)
    serverDependencies {
        registerRequired("PlotSquared")
        registerRequired("PlaceholderAPI")
    }
}

repositories {
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    implementation(enforcedPlatform(libs.intellectualsites.bom))
    compileOnly(libs.plotsquared.core)
    compileOnly(libs.plotsquared.bukkit) { isTransitive = false }
    compileOnly(libs.fawe.core)
    compileOnly(libs.placeholderapi)
}