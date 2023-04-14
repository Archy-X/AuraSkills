plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.1")
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

java {
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
}