plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.base.paper.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)

    authors.add("red")

    withSurfRedis()
}

dependencies {
    api(project(":surf-event-base:surf-event-base-api:surf-event-base-api-common"))
    implementation("dev.slne.surf.tab:surf-tab-api:1.21.11-1.0.2-SNAPSHOT")
    compileOnly("dev.slne.surf.settings:surf-settings-api:1.21.11-2.0.1-SNAPSHOT")
}

tasks.shadowJar {
    relocate("dev.slne.surf.tab", "dev.slne.surf.event.base.libs")
}