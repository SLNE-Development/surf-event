include("surf-event-mythic-mobs")

include("surf-event-base")

include("surf-event-base:surf-event-base-api")
include("surf-event-base:surf-event-base-api:surf-event-base-api-common")
include("surf-event-base:surf-event-base-velocity")
include("surf-event-base:surf-event-base-paper")

val events = listOf(
    "surf-buildit",
    "surf-hardcore",
    "bmbf-real-event",
    "surf-random-drops-event",
    "surf-oneblock",
    "surf-collect-it-event"
).forEach { event ->
    include("surf-event-events:$event")
}
