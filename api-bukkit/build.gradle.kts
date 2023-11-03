plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    api(project(":api"))
    implementation("org.jetbrains:annotations:24.0.1")
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks {
    javadoc {
        title = "AuraSkills API Bukkit (${project.version})"
        source = sourceSets.main.get().allSource + project(":api").sourceSets.main.get().allSource
        classpath = files(sourceSets.main.get().compileClasspath, project(":api").sourceSets.main.get().output)
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
            overview("javadoc/overview.html")
            encoding("UTF-8")
            charset("UTF-8")
        }
    }
}

java {
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
}
