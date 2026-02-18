plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

velocityPluginFile {
    main = "dev.slne.surf.event.base.velocity.VelocityMain"
    authors = listOf("red")
}

dependencies {
    api(project(":surf-event-base:surf-event-base-core"))
}