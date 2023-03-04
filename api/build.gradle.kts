plugins {
    java
}

repositories {

}

dependencies {

}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

java {
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
}