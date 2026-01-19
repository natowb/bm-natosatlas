plugins {
    id("maven-publish")
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
    id("babric-loom-extension") version "1.14-SNAPSHOT"
}

loom {
    clientOnlyMinecraftJar()
    runs["client"].runDir = "../run"
}

dependencies {
    minecraft("com.mojang:minecraft:${project.properties["minecraft_version"]}")
    mappings("net.glasslauncher:biny:${project.properties["yarn_mappings"]}:v2")

    implementation(project(":core"))

    modImplementation("net.fabricmc:fabric-loader:${project.properties["loader_version"]}")
    modImplementation("net.modificationstation:StationAPI:${project.properties["stationapi_version"]}")

    // logging
    implementation("org.slf4j:slf4j-api:1.8.0-beta4")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.17.1")

    // convenience stuff
    // adds some useful annotations for data classes. does not add any dependencies
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    // adds some useful annotations for miscellaneous uses. does not add any dependencies, though people without the lib will be missing some useful context hints.
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("com.google.guava:guava:33.2.1-jre")

    // Extra mods.
    // https://github.com/calmilamsy/glass-config-api
    modImplementation("net.glasslauncher.mods:GlassConfigAPI:${project.properties["gcapi_version"]}")
    // https://github.com/calmilamsy/modmenu
    modImplementation("net.danygames2014:modmenu:${project.properties["modmenu_version"]}")
    // https://github.com/Glass-Series/Always-More-Items
    modImplementation("net.glasslauncher.mods:AlwaysMoreItems:${project.properties["alwaysmoreitems_version"]}")
}

configurations.all {
    exclude("babric")
}

tasks.withType<ProcessResources> {
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to version))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }

    from(project(":core").sourceSets.main.get().output)
}