plugins {
    java
    idea
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

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}