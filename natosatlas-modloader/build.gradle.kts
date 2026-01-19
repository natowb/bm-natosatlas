plugins {
    id("maven-publish")
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
    id("babric-loom-extension") version "1.14-SNAPSHOT"
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
    // TODO: Gambac causes unfixable conflicts because it gets picked up as a fabric mod which loom tries to remap
    exclude("net.danygames2014")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }

    from(project(":core").sourceSets.main.get().output)
}

tasks.remapJar {
    archiveFileName = "unprocessed.jar"
}

tasks.register("remapJarForModLoader", net.fabricmc.loom.task.RemapJarTask::class) {
    inputFile = tasks.remapJar.get().archiveFile

    customMappings.from("modloader/mappings.tiny")
    sourceNamespace = "source"
    targetNamespace = "runtime"

    archiveBaseName = base.archivesName
    archiveVersion = version as String
}

tasks.assemble {
    finalizedBy("remapJarForModLoader")
}

tasks.build {
    dependsOn("remapJarForModLoader")
}