plugins {
    id("dev.slne.surf.api.gradle.velocity")
}

velocityPluginFile {
    main = "dev.slne.surf.event.base.velocity.VelocityMain"
    authors = listOf("red")
}

dependencies {
    api(projects.surfEventBase.surfEventBaseCore)
}