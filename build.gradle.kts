plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.wizzardr"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://nexus.funkemunky.cc/content/repositories/releases/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://repo.aikar.co/content/groups/aikar/") }
}

dependencies {
    // Spigot
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")

    // Class Index
    implementation("org.atteo.classindex:classindex:3.9")
    annotationProcessor("org.atteo.classindex:classindex:3.9")

    // PacketEvents 2.0
    implementation("com.github.retrooper:packetevents-spigot:2.9.5")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // ACF
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

    // Adventure
    implementation("net.kyori:adventure-api:4.14.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.2")
    implementation("net.kyori:adventure-text-serializer-gson:4.14.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    // Relocate PacketEvents to avoid conflicts
    relocate("io.github.retrooper.packetevents", "${project.group}.shade.packetevents")
    relocate("com.github.retrooper.packetevents", "${project.group}.shade.packetevents")

    // Relocate ACF
    relocate("co.aikar.commands", "${project.group}.acf")
    relocate("co.aikar.locales", "${project.group}.locales")

    // Configure the output JAR
    archiveFileName.set("${project.name}-${project.version}.jar")
    mergeServiceFiles()

    // Exclude unnecessary files
    minimize()

    var property = System.getProperty("hotswap")

    if (project.hasProperty("hotswap") && property == null) {
        System.setProperty("hotswap", "true")
        property = "true"
    }

    if (property != null && property.equals("true")) {
        project.properties.remove("hotswap")
        return@shadowJar
    }

    if (project.hasProperty("destinationDir")) {
        val destinationDirValue = project.property("destinationDir")?.toString()
        destinationDirectory.set(destinationDirValue?.let { file(it) })
    }
}

// Make the shadowJar task run when building the project
tasks.build {
    dependsOn(tasks.shadowJar)
}

// Set shadowJar as the default artifact
artifacts {
    archives(tasks.shadowJar)
}