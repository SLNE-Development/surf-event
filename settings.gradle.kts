include("surf-event-mythic-mobs")

include("surf-event-base")
include("surf-event-base:surf-event-base-api")
include("surf-event-base:surf-event-base-api:surf-event-base-api-common")
include("surf-event-base:surf-event-base-core")
include("surf-event-base:surf-event-base-velocity")
include("surf-event-base:surf-event-base-paper")

val events = listOf<String>(
//    "surf-buildit",
//    "surf-hardcore",
//    "bmbf-real-event",
//    "surf-random-drops-event",
//    "surf-oneblock",
//    "surf-collect-it-event"
).forEach { event ->
    include("surf-event-events:$event")
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://reposilite.slne.dev/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.api.gradle.settings") version "+"
}



include("surf-event-events:surf-anarchy")