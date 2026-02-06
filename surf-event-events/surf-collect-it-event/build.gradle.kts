plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.collectit.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)
    authors.add("ammo")
}