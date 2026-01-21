pluginManagement {
    repositories {
        maven("https://maven.glass-launcher.net/babric/")
        maven("https://maven.fabricmc.net/")
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "Natos Atlas"

listOf("core", "bta", "modloader", "stationapi").forEach {
    include(it)
    project(":$it").projectDir = file("natosatlas-$it")
}