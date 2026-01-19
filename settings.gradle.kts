pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven ("https://maven.glass-launcher.net/babric")
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "Natos Atlas"

listOf("core", "modloader", "stationapi").forEach {
    include(it)
    project(":$it").projectDir = file("natosatlas-$it")
}