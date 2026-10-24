// core:quotation — build configuration.
// Pure Kotlin (JVM). Independent of the model and estimation modules so quotation maths
// can be reused by every current and future trade.
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
