dependencies {
    // TODO: Factor out rendering & input handling so this direct dependency can be removed
    compileOnly("org.lwjgl.lwjgl:lwjgl:2.9.4+legacyfabric.15") {
        isTransitive = false
    }
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
}