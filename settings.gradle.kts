plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "surf-event"

include("surf-event-base")
include("surf-buildit")
include("surf-oneblock-skyblock-event")
include("surf-radnom-drops-event")
include("surf-mythic-mobs-hook")
include("surf-hardcore")
include("bmbf-real-event")
include("surf-playtime-checker")
