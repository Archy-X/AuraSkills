plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://www.jitpack.io")
}

dependencies {
    api(project(":api"))
    api("net.kyori:adventure-api:4.17.0")
    api("co.aikar:acf-core:0.5.1-SNAPSHOT")
    api("net.kyori:adventure-text-serializer-legacy:4.17.0")
    api("com.github.Archy-X:Polyglot:1.2.1") {
        exclude("org.spongepowered", "configurate-yaml")
    }
    api("com.ezylang:EvalEx:3.3.0")
    api("org.spongepowered:configurate-yaml:4.1.2") {
        exclude("org.yaml", "snakeyaml")
    }
    implementation("com.zaxxer:HikariCP:5.1.0") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("org.atteo:evo-inflector:1.3")
    implementation("com.github.Querz:NBT:6.1")
    compileOnly("com.google.guava:guava:33.2.1-jre")
    compileOnly("com.google.code.gson:gson:2.10")
    compileOnly("net.luckperms:api:5.4")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.11.0-M1"))
    testImplementation("org.yaml:snakeyaml:2.2")
}

val compiler = javaToolchains.compilerFor {
    languageVersion = JavaLanguageVersion.of(17)
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
        languageVersion = JavaLanguageVersion.of(17)
    }
}