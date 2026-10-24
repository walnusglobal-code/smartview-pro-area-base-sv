// core:model — build configuration.
// Pure Kotlin (JVM) module: no Android dependencies, so the domain is unit-testable
// on any JVM and reusable by future platform targets.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit)
}
