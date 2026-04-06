plugins {
    id("dev.slne.surf.api.gradle.core")
}

dependencies {
    api(projects.surfEventBase.surfEventBaseApi.surfEventBaseApiCommon)
}

surfCoreApi {
    withSurfRedis()
    withCoreCommon()
}