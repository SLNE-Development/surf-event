plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.anarchy.PaperMain")
    generateLibraryLoader(false)

    version = "2.2.0"

    authors.addAll("red")
}