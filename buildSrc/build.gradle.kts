plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    dependencies {
        implementation("com.google.fhir:fhir-model:1.0.0-alpha02")
        implementation("com.squareup:kotlinpoet:2.0.0")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    }
}