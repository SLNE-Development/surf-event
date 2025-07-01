plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

group = "dev.slne.surf.event"
version = "1.0.0"

surfPaperPluginApi {
    mainClass("dev.slne.surf.event.base.PaperMain")
    authors.add("twisti")
}

dependencies {
    implementation("io.github.classgraph:classgraph:4.8.180")
}