plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    `java-gradle-plugin`
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlin.poet)
}

configure<GradlePluginDevelopmentExtension> {
    plugins {
        create("fhirPathCodegenPlugin") {
            id = "fhirpath-codegen"
            implementationClass = "com.google.fhir.fhirpath.codegen.FhirPathCodegenPlugin"
        }
    }
}
