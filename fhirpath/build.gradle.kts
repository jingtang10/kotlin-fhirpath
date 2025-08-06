import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.antlr.kotlin)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotest)
    id("fhirpath-codegen")
}

// Run `./gradlew r4` to generate FHIR models for R4 in `fhir-model/build/generated/r4`
val codegenTaskR4 = fhirPathCodegenExtension.newTask("r4") {
    description = "Generate FHIR models for R4"
    this.corePackageFiles.from(
        File(project.rootDir, "third_party/hl7.fhir.r4.core/package").listFiles()
    )
    this.packageName.set("com.google.fhir.model.r4")
}

val generateKotlinGrammarSource = tasks.register<AntlrKotlinTask>("generateKotlinGrammarSource") {
    dependsOn("cleanGenerateKotlinGrammarSource")
    source = fileTree(rootProject.file("third_party/fhirpath-2.0.0/grammar")) {
        include("**/*.g4")
    }
    packageName = "com.google.fhir.fhirpath.parsers.generated"

    // We want visitors alongside listeners.
    // The Kotlin target language is implicit, as is the file encoding (UTF-8)
    arguments = listOf("-visitor")

    // Generated files are outputted inside build/generatedAntlr/{package-name}
    val outDir = "generatedAntlr/${packageName!!.replace(".", "/")}"
    outputDirectory = layout.buildDirectory.dir(outDir).get().asFile
}


tasks.withType<Test>().configureEach {
    // Allow tests to access third_party
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
                srcDir(codegenTaskR4)
                srcDir(generateKotlinGrammarSource)
            }
            dependencies {
                api(libs.kotlin.fhir)
                api(libs.kotlinx.datetime)  // Remove when Kotlin FHIR exposes this properly
                implementation(libs.antlr.kotlin.runtime)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.framework.datatest)
            implementation(libs.kotlin.test)
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