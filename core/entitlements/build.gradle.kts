// core:entitlements — build configuration.
// Pure Kotlin (JVM) module. Contains no billing-provider SDK types so the provider can
// be chosen later without touching feature code.
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
