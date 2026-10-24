// core:estimation — build configuration.
// Painting is the first trade. Estimation depends on the shared model, the trade
// contract and entitlement keys, but on no UI or persistence code.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":core:model"))
    api(project(":core:trade"))

    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit)
}
