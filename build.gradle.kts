// Root build file.
//
// SV-001 contains only the Android-free core modules, so the root build applies no
// Android or Compose plugins yet. Those plugin aliases are already pinned in
// gradle/libs.versions.toml and are wired up by the ":app" module in SV-002.
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}
