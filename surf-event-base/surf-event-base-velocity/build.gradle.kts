plugins {
    kotlin("jvm")
}

group = "dev.slne.surf.event"
version = "1.21.11-1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}