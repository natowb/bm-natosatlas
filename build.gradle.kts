plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT" apply false
    id("babric-loom-extension") version "1.14-SNAPSHOT" apply false
}

subprojects {
    apply(plugin = "java")

    (project.extensions["base"] as BasePluginExtension).archivesName = "${project.properties["mod_id"]}-${project.name}"
    version = project.properties["mod_version"] as String
    group = project.properties["maven_group"] as String

    repositories {
        maven("https://maven.glass-launcher.net/snapshots/")
        maven("https://maven.glass-launcher.net/releases/")
        maven("https://maven.glass-launcher.net/babric/")
        maven("https://maven.minecraftforge.net/")
        maven("https://jitpack.io/")
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven("https://api.modrinth.com/maven/")
            }
            filter {
                includeGroup("maven.modrinth")
            }
        }
        exclusiveContent {
            forRepository {
                maven("https://maven.legacyfabric.net/")
            }
            filter {
                includeGroup("org.lwjgl.lwjgl")
            }
        }
    }
}

val build by tasks.registering(Sync::class) {
    group = "build"

    subprojects {
        if (name != "core") {
            dependsOn(tasks["build"])
            from(project.layout.buildDirectory.dir("libs").map {
                it.file("${project.properties["mod_id"]}-${project.name}-$version.jar")
            })
        }
    }

    into(layout.buildDirectory.dir("libs"))
}