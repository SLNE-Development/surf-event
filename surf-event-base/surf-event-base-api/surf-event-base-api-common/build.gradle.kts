import dev.slne.surf.api.gradle.util.slnePublic

plugins {
    id("dev.slne.surf.api.gradle.core")
}

publishing {
    repositories {
        slnePublic()
    }
}