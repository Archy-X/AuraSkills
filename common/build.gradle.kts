plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://www.jitpack.io")
}

dependencies {
    api(project(":api"))
    api("net.kyori:adventure-api:4.17.0")
    api("co.aikar:acf-core:0.5.1-SNAPSHOT")
    api("net.kyori:adventure-text-serializer-legacy:4.17.0")
    api("dev.aurelium:polyglot:1.2.4")
    api("com.ezylang:EvalEx:3.3.0")
    api("org.spongepowered:configurate-yaml:4.2.0")
    implementation("com.zaxxer:HikariCP:5.1.0") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("org.atteo:evo-inflector:1.3")
    implementation("com.github.Querz:NBT:6.1")
    compileOnly("com.google.guava:guava:33.2.1-jre")
    compileOnly("com.google.code.gson:gson:2.10")
    compileOnly("net.luckperms:api:5.4")
    testImplementation(platform("org.junit:junit-bom:5.13.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.yaml:snakeyaml:2.2")
}

val compiler = javaToolchains.compilerFor {
    languageVersion = JavaLanguageVersion.of(21)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
    options.isFork = true
    options.forkOptions.executable = compiler.map { it.executablePath }.get().toString()
}

tasks {
    test {
        useJUnitPlatform()
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}