plugins {
    id("net.fabricmc.fabric-loom")
    alias(libs.plugins.lwjgl)
}

loom {
    customMinecraftMetadata.set("https://downloads.betterthanadventure.net/bta-client/${libs.versions.btaChannel.get()}/v${libs.versions.bta.get()}/manifest.json")
    runs["client"].runDir = "../run"
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
    maven("https://maven.thesignalumproject.net/infrastructure") { name = "SignalumMavenInfrastructure" }
    maven("https://maven.thesignalumproject.net/releases") { name = "SignalumMavenReleases" }
    ivy("https://github.com/Better-than-Adventure") {
        patternLayout { artifact("[organisation]/releases/download/[revision]/[module]-bta-[revision].jar") }
        metadataSources { artifact() }
    }
    ivy("https://downloads.betterthanadventure.net/bta-client/${libs.versions.btaChannel.get()}/") {
        patternLayout { artifact("/v[revision]/client.jar") }
        metadataSources { artifact() }
    }
    ivy("https://downloads.betterthanadventure.net/bta-server/${libs.versions.btaChannel.get()}/") {
        patternLayout { artifact("/v[revision]/server.jar") }
        metadataSources { artifact() }
    }
    ivy("https://piston-data.mojang.com") {
        patternLayout { artifact("v1/[organisation]/[revision]/[module].jar") }
        metadataSources { artifact() }
    }
}

lwjgl {
    version = libs.versions.lwjgl
    implementation(com.smushytaco.lwjgl_gradle.Preset.MINIMAL_OPENGL)
}

val projectDependencies = listOf(
    project(":core")
)

dependencies {
    minecraft("::${libs.versions.bta.get()}")

    projectDependencies.forEach {
        implementation(it)
    }

    runtimeOnly(libs.clientJar)
    implementation(libs.loader)
    implementation(libs.halplibe)
    implementation(libs.modMenu)
    implementation(libs.legacyLwjgl)

    implementation(libs.slf4jApi)
    implementation(libs.guava)
    implementation(libs.log4j.slf4j2.impl)
    implementation(libs.log4j.core)
    implementation(libs.log4j.api)
    implementation(libs.log4j.api12)
    implementation(libs.gson)
}

configurations.configureEach {
    exclude(group = "org.lwjgl.lwjgl")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
}

tasks {
    withType<JavaCompile> {
        configureEach {
            options.encoding = "UTF-8"
            options.compilerArgs.add("-Xlint:-options")
        }
    }

    processResources {
        projectDependencies.forEach {
            from(it.sourceSets.main.get().resources)
        }

        val properties = mapOf(
            "version" to version,
            "fabricloader" to libs.versions.loader.get(),
            "halplibe" to libs.versions.halplibe.get(),
            "java" to libs.versions.java.get(),
            "modmenu" to libs.versions.modMenu.get()
        )

        inputs.properties(properties)

        filesMatching("fabric.mod.json") {
            expand(properties)
        }
        filesMatching("**/*.mixins.json") {
            expand(properties.filterKeys { it == "java" })
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