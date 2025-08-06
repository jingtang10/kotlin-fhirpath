import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.google.fhir.fhirpath.codegen.R4HelperGenerationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.antlr.kotlin)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotest)
}

// Run `./gradlew generateR4Helpers` to generate helper functions for R4 in `fhirpath/build/generated`
val generateR4Helpers = tasks.register<R4HelperGenerationTask>("generateR4Helpers") {
    description = "Generate FHIR model extensions for R4"
    this.corePackageFiles.from(
        File(project.rootDir, "third_party/hl7.fhir.r4.core/package").listFiles()
    )
    this.modelPackageName.set("com.google.fhir.model.r4")
    this.fhirPathPackageName.set("com.google.fhir.fhirpath")
    outputDirectory.set(layout.buildDirectory.dir("generated/source/fhirpath/main"))
}

// Run `./gradlew generateKotlinGrammarSource` to generate parser in `fhirpath/build/generatedAntlr`
val generateKotlinGrammarSource = tasks.register<AntlrKotlinTask>("generateKotlinGrammarSource") {
    dependsOn("cleanGenerateKotlinGrammarSource")
    source = fileTree(rootProject.file("third_party/fhirpath-2.0.0")) {
        include("**/*.g4")
    }
    packageName = "com.google.fhir.fhirpath.parsers.generated"
    arguments = listOf("-visitor")  // Generate visitors alongside listeners

    val outDir = "generatedAntlr/${packageName!!.replace(".", "/")}"
    outputDirectory = layout.buildDirectory.dir(outDir).get().asFile
}


tasks.withType<Test>().configureEach {
    // Provide root directory reference for test code to access third_party
    systemProperty("projectRootDir", project.rootDir.absolutePath)
}

kotlin {
    jvm()

    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            kotlin {
                srcDir(generateR4Helpers)
                srcDir(generateKotlinGrammarSource)
            }
            dependencies {
                api(libs.kotlin.fhir)
                implementation(libs.antlr.kotlin.runtime)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.framework.datatest)
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.xmlutil.serialization)
            implementation(libs.xmlutil.core)
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.runner.junit5)
            }
        }
    }
}

android {
    namespace = "com.google.fhir.fhirpath"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn(generateR4Helpers)
    dependsOn(generateKotlinGrammarSource)
}