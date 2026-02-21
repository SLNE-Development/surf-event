import dev.slne.surf.surfapi.gradle.util.registerRequired

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.oneblock.PaperMain")
    generateLibraryLoader(false)
    withSurfDatabaseR2dbc("1.3.0", "dev.slne.surf.event.oneblock.libs.db")

    authors.addAll("twisti", "Ammo", "red")

    serverDependencies {
        registerRequired("FastAsyncWorldEdit")
        registerRequired("FancyHolograms")
        registerRequired("LuckPerms")
        registerRequired("surf-stats-paper")
    }
}

dependencies {
    compileOnly(files("libs/surf-stats-api.jar"))

    implementation(platform("com.intellectualsites.bom:bom-newest:1.55")) // Ref: https://github.com/IntellectualSites/bom
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }

    compileOnly("de.oliver:FancyHolograms:2.7.0")
    compileOnly("net.luckperms:api:5.4")
}