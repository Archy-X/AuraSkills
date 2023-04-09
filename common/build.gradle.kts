plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":api"))
    implementation("org.jetbrains:annotations:23.0.0")
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

java {
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
}