plugins {
    id("maven-publish")
    id("net.fabricmc.fabric-loom-remap")
    id("babric-loom-extension")
}

loom {
    clientOnlyMinecraftJar()
    runs["client"].runDir = "../run"
}

val projectDependencies = listOf(
    project(":core")
)

dependencies {
    minecraft("com.mojang:minecraft:${project.properties["minecraft_version"]}")
    mappings("net.glasslauncher:biny:${project.properties["yarn_mappings"]}:v2")

    projectDependencies.forEach {
        implementation(it)
    }

    implementation("org.slf4j:slf4j-api:1.8.0-beta4")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.17.1")

    modImplementation("net.fabricmc:fabric-loader:${project.properties["loader_version"]}")
    modImplementation("net.modificationstation:StationAPI:${project.properties["stationapi_version"]}")

    modImplementation("net.danygames2014:modmenu:${project.properties["modmenu_version"]}")
    modImplementation("net.glasslauncher.mods:GlassConfigAPI:${project.properties["gcapi_version"]}")
    modImplementation("net.glasslauncher.mods:AlwaysMoreItems:${project.properties["alwaysmoreitems_version"]}")
}

configurations.all {
    exclude("babric")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    withType<JavaCompile> {
        configureEach {
            options.encoding = "UTF-8"
        }
    }

    processResources {
        projectDependencies.forEach {
            from(it.sourceSets.main.get().resources)
        }

        inputs.property("version", version)

        filesMatching("fabric.mod.json") {
            expand("version" to version)
        }
    }

    jar {
        projectDependencies.forEach {
            from(it.sourceSets.main.get().output)
        }

        from(rootProject.layout.projectDirectory.file("LICENSE")) {
            rename { "${it}_${base.archivesName.get()}" }
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}