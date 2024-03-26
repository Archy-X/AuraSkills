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
    api("net.kyori:adventure-api:4.15.0")
    api("co.aikar:acf-core:0.5.1-SNAPSHOT")
    api("net.kyori:adventure-text-serializer-legacy:4.16.0")
    api("com.github.Archy-X:Polyglot:7f56b6fd04")
    api("com.ezylang:EvalEx:3.1.1")
    implementation("com.zaxxer:HikariCP:5.1.0") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("org.atteo:evo-inflector:1.3")
    implementation("com.github.Querz:NBT:6.1")
    compileOnly("com.google.guava:guava:32.1.3-jre")
    compileOnly("com.google.code.gson:gson:2.10.1")
    compileOnly("net.luckperms:api:5.4")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.9.3"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks {
    test {
        useJUnitPlatform()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}