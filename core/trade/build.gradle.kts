// core:trade — build configuration.
// Holds the trade capability contract that the application shell uses to enumerate
// trades. Trade implementations (painting, plastering, screeding) live in their own
// modules and depend on this contract, never on each other.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":core:entitlements"))

    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit)
}
