plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":api"))
    implementation("net.kyori:event-api:3.0.0")
    implementation("net.kyori:adventure-api:4.13.0")
    compileOnly("net.luckperms:api:5.4")
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

java {
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
}