plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

group = "dev.slne.surf.event"
version = "1.0.0"

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.hardcore.PaperMain")
    generateLibraryLoader(false)
}