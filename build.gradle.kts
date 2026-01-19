subprojects {
    apply(plugin = "java-library")

    val archivesName = "${project.properties["mod_id"]}-${project.name}"

    (project.extensions.getByName("base") as BasePluginExtension).archivesName = archivesName
    version = project.properties["mod_version"] as String
    group = project.properties["maven_group"] as String

    repositories {
        maven("https://maven.glass-launcher.net/snapshots/")
        maven("https://maven.glass-launcher.net/releases/")
        maven("https://maven.glass-launcher.net/babric")
        maven("https://maven.minecraftforge.net/")
        maven("https://jitpack.io/")
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven("https://api.modrinth.com/maven")
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