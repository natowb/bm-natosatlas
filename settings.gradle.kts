pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven ("https://maven.glass-launcher.net/babric")
        maven("https://maven.thesignalumproject.net/infrastructure") { name = "SignalumMavenInfrastructure" }
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}

rootProject.name = "Natos Atlas"

listOf("core", "modloader", "stationapi", "bta").forEach {
    include(it)
    project(":$it").projectDir = file("natosatlas-$it")
}