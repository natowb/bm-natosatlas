plugins {
    id("maven-publish")
    id("net.fabricmc.fabric-loom-remap")
    id("babric-loom-extension")
}

loom {
    clientOnlyMinecraftJar()
    runs["client"].runDir = "../run"
    productionNamespace = "clientOfficial"
}

dependencies {
    minecraft("com.mojang:minecraft:${project.properties["minecraft_version"]}")
    mappings("net.glasslauncher:biny:${project.properties["yarn_mappings"]}:v2")

    implementation(project(":core"))

    implementation(files("modloader/modloader.jar"))
}

configurations.all {
    exclude("babric")
    // Gambac causes unfixable conflicts because it gets picked up as a fabric mod which loom tries to remap using
    // intermediary mappings, which we currently do not use.
    exclude("net.danygames2014")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    withType<JavaCompile> {
        configureEach {
            options.encoding = "UTF-8"
            options.compilerArgs.add("-Xlint:-options")
        }
    }

    jar {
        from(rootProject.layout.projectDirectory.file("LICENSE")) {
            rename { "${it}_${base.archivesName.get()}" }
        }

        from(project(":core").sourceSets.main.map { it.output })

        manifest {
            attributes("Implementation-Version" to version)
        }
    }

    // Hide/Disable the runClient task because it doesn't work yet™
    runClient {
        group = null
        enabled = false
    }
}

// The section below handles remapping modloader from net.minecraft.src to the base package.
//
// There is probably a less hacky way to do this, e.g. shadowJar, but that causes modloader
// to crash when trying to load the mod, once the cause for that is found, this can and will
// be replaced.

tasks {
    remapJar {
        archiveFileName = archiveFile.get().asFile.nameWithoutExtension + "-unprocessed.jar"
    }

    register("remapJarForModLoader", net.fabricmc.loom.task.RemapJarTask::class) {
        inputFile = remapJar.flatMap { it.archiveFile }

        customMappings.from("modloader/mappings.tiny")
        sourceNamespace = "source"
        targetNamespace = "runtime"

        archiveBaseName = base.archivesName
        archiveVersion = version as String
    }

    assemble {
        finalizedBy("remapJarForModLoader")
    }

    build {
        dependsOn("remapJarForModLoader")
    }
}