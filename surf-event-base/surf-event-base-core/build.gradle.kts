plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

dependencies {
    api(project(":surf-event-base:surf-event-base-api:surf-event-base-api-common"))
}

surfCoreApi {
    withSurfRedis()
    withCoreCommon()
}