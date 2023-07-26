plugins {
    java
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {

}

allprojects {
    group = "dev.aurelium.auraskills"
    version = project.property("projectVersion") as String
    description = "Advanced skills, stats, and abilties plugin"
}

java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
