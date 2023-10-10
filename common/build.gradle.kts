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
    api("net.kyori:adventure-api:4.13.0")
    api("co.aikar:acf-core:0.5.1-SNAPSHOT")
    api("net.kyori:adventure-text-serializer-legacy:4.13.1")
    api("net.kyori:adventure-text-serializer-gson:4.13.1")
    api("com.github.Archy-X:Polyglot:1.1.11")
    api("org.spongepowered:configurate-yaml:4.1.2")
    implementation("net.kyori:event-api:3.0.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.ezylang:EvalEx:3.0.4")
    implementation("org.atteo:evo-inflector:1.3")
    compileOnly("net.luckperms:api:5.4")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.9.3"))
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks {
    javadoc {
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
        }
    }
    test {
        useJUnitPlatform()
    }
}

java {
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_17
}