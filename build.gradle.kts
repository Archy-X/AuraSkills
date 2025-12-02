plugins {
    java
    idea
    checkstyle
    id("org.jreleaser") version "1.19.0"
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

val gradleProject = project

jreleaser {
    files {
        setActive("ALWAYS")

        artifact {
            setPath("build/libs/AuraSkills-${gradleProject.property("projectVersion")}.jar")
        }
    }

    signing {
        setActive("ALWAYS")
        armored = true
        setMode("MEMORY")
    }

    deploy {
        maven {
            mavenCentral {
                create("sonatype", Action {
                    setActive("NEVER")
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepositories = listOf("api/build/staging-deploy", "api-bukkit/build/staging-deploy")
                    username = (gradleProject.findProperty("sonatypeUsername") ?: "").toString()
                    password = (gradleProject.findProperty("sonatypePassword") ?: "").toString()
                })
            }
            nexus2 {
                create("snapshot-deploy", Action {
                    setActive("SNAPSHOT")
                    snapshotUrl = "https://central.sonatype.com/repository/maven-snapshots/"
                    applyMavenCentralRules = true
                    snapshotSupported = true
                    closeRepository = true
                    releaseRepository = true
                    stagingRepositories = listOf("api/build/staging-deploy", "api-bukkit/build/staging-deploy")
                    username = (gradleProject.findProperty("sonatypeUsername") ?: "").toString()
                    password = (gradleProject.findProperty("sonatypePassword") ?: "").toString()
                })
            }
        }
    }

    release {
        github {
            skipRelease = false
            repoOwner = "Archy-X"
            name = "AuraSkills"
            tagName = gradleProject.property("projectVersion").toString()
            releaseName = gradleProject.property("projectVersion").toString()
            token = (gradleProject.findProperty("jreleaserGithubToken") ?: "").toString()
            draft = true
            checksums = false
            signatures = false
            catalogs = false
            branch = "master"

            changelog {
                enabled = true
                setFormatted("ALWAYS")

                contributors {
                    enabled = false
                }

                val releaseTitle = gradleProject.property("releaseTitle").toString()
                val repoBaseUrl = gradleProject.property("repoBaseUrl").toString()
                val changelogLink = "${repoBaseUrl}/blob/master/Changelog.md#${gradleProject.property("projectVersion").toString().replace(".", "")}"
                content = "${releaseTitle}\n\nSee the [changelog](${changelogLink}) for a full list of changes in this release."
            }
        }
    }
}

tasks.named("jreleaserRelease") {
    outputs.upToDateWhen { false }
}
