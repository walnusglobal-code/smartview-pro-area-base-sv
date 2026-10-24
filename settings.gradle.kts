pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SmartViewPro"

// ":app" (Android application module) is introduced in SV-002 together with the
// UI shell. It is not declared yet so the project configures without a missing module.
include(":core:model")
include(":core:entitlements")
include(":core:trade")
include(":core:estimation")
include(":core:quotation")
