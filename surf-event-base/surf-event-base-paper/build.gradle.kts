import dev.slne.surf.api.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.base.paper.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)

    authors.add("red")

    withSurfRedis()
    withCorePaper()

    serverDependencies {
        registerSoft("surf-settings-paper")
    }
}

dependencies {
    api(projects.surfEventBase.surfEventBaseCore)
    compileOnly("dev.slne.surf.settings:surf-settings-api:+")
}