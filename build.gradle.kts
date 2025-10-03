plugins {
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.spotless)
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    ratchetFrom = "origin/main"
    kotlin {
        target("**/*.kt")
        ktfmt().googleStyle()
        licenseHeaderFile(
            "license-header.txt",
        )
    }
    flexmark {
        target("**/*.md")
        flexmark()
    }
}