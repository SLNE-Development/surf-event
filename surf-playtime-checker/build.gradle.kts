import dev.slne.surf.surfapi.gradle.util.slnePrivate

plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

group = "dev.slne.surf.event"
version = "1.0.0"

velocityPluginFile {
    main = "dev.slne.surf.event.playtimechecker.VelocityMain"

    pluginDependencies {
        register("surf-proxy-velocity")
    }
}

repositories {
    slnePrivate()
}

dependencies {
    compileOnly("dev.slne.surf.proxy:surf-proxy-api:1.21.4+")
}