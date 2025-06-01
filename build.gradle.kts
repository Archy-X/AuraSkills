plugins {
    java
    idea
    checkstyle
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

    apply(plugin = "checkstyle")

    checkstyle {
        toolVersion = "10.24.0"
        isIgnoreFailures = false
        sourceSets = emptyList()
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
