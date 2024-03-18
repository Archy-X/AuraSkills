import java.net.URI

plugins {
    `java-library`
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.spongepowered:configurate-yaml:4.1.2")
    implementation("org.jetbrains:annotations:24.0.1")
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks {
    javadoc {
        title = "AuraSkills API (${project.version})"
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
            artifactId = "auraskills-api"
            version = project.version.toString()

            pom {
                name.set("AuraSkills API")
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
                        id.set("archy")
                        name.set("Archy")
                        email.set("archydevelopment@gmail.com")
                        url.set("https://github.com/Archy-X")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Archy-X/AureliumSkills.git")
                    developerConnection.set("scm:git:git://github.com/Archy-X/AureliumSkills.git");
                    url.set("https://github.com/Archy-X/AureliumSkills/tree/master")
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