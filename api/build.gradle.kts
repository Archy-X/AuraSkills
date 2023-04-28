plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.guava:guava:31.1-jre")
    implementation("org.jetbrains:annotations:24.0.1")
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

java {
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
}