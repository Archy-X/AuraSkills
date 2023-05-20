plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    api(project(":api"))
    implementation("net.kyori:event-api:3.0.0")
    implementation("net.kyori:adventure-api:4.13.0")
    implementation("co.aikar:acf-core:0.5.1-SNAPSHOT")
    implementation("net.kyori:adventure-text-serializer-legacy:4.13.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.zaxxer:HikariCP:5.0.1")
    compileOnly("net.luckperms:api:5.4")
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
}

java {
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_17
}