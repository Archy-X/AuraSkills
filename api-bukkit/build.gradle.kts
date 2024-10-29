import java.net.URI

plugins {
    `java-library`
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    api(project(":api"))
    api("dev.aurelium:slate:1.1.9-SNAPSHOT") {
        exclude("org.yaml", "snakeyaml")
        exclude("org.spongepowered", "configurate-yaml")
    }
    // api(files("../../Slate/build/libs/Slate-1.1.8-all.jar"))
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
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
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
}

if (project.hasProperty("sonatypeUsername") && project.hasProperty("sonatypePassword")) {
    publishing {
        repositories {
            maven {
                val releasesRepoUrl = URI.create("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = URI.create("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                credentials {
                    username = project.property("sonatypeUsername").toString()
                    password = project.property("sonatypePassword").toString()
                }
            }
        }

        publications.create<MavenPublication>("mavenJava") {
            groupId = "dev.aurelium"
            artifactId = "auraskills-api-bukkit"
            version = project.version.toString()

            pom {
                name.set("AuraSkills API Bukkit")
                description.set("API for AuraSkills, the ultra-versatile RPG skills plugin for Minecraft")
                url.set("https://wiki.aurelium.dev/auraskills")
                licenses {
                    license {
                        name.set("The GNU General Public License, Version 3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    }
                }
                developers {
                    developer {
                        id.set(project.property("developerId").toString())
                        name.set(project.property("developerUsername").toString())
                        email.set(project.property("developerEmail").toString())
                        url.set(project.property("developerUrl").toString())
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Archy-X/AuraSkills.git")
                    developerConnection.set("scm:git:git://github.com/Archy-X/AuraSkills.git")
                    url.set("https://github.com/Archy-X/AuraSkills/tree/master")
                }
            }

            from(components["java"])
        }
    }

    signing {
        useGpgCmd()
        sign(publishing.publications.getByName("mavenJava"))
        isRequired = true
    }
}
