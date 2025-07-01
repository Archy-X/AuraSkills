import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.gradleup.shadow") version "8.3.5"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("com.modrinth.minotaur") version "2.+"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.helpch.at/releases")
    maven("https://jitpack.io")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.nexomc.com/snapshots/")
    maven("https://repo.nexomc.com/releases/")
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenLocal()
}

dependencies {
    implementation(project(":common"))
    implementation(project(":api-bukkit"))
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("de.tr7zw:item-nbt-api:2.15.1-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("net.kyori:adventure-text-minimessage:4.16.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.3")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("org.spigotmc:spigot-api:1.21.5-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.5") {
        exclude("org.spigotmc", "spigot-api")
    }
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.5.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit", "bukkit")
    }
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.TownyAdvanced:Towny:0.98.3.6")
    compileOnly("com.github.Slimefun:Slimefun4:RC-37")
    compileOnly("io.lumine:Mythic-Dist:5.6.1")
    compileOnly("com.nexomc:nexo:1.6.0")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.59.0")
    testImplementation("org.slf4j:slf4j-simple:2.0.17")
    testImplementation(platform("org.junit:junit-bom:5.13.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val compiler = javaToolchains.compilerFor {
    languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    withType<ShadowJar> {
        val projectVersion: String by project
        archiveFileName.set("AuraSkills-${projectVersion}.jar")

        relocate("co.aikar.commands", "dev.aurelium.auraskills.acf")
        relocate("co.aikar.locales", "dev.aurelium.auraskills.locales")
        relocate("de.tr7zw.changeme.nbtapi", "dev.aurelium.auraskills.nbtapi")
        relocate("org.bstats", "dev.aurelium.auraskills.bstats")
        relocate("com.ezylang.evalex", "dev.aurelium.auraskills.evalex")
        relocate("net.kyori", "dev.aurelium.auraskills.kyori")
        relocate("com.zaxxer.hikari", "dev.aurelium.auraskills.hikari")
        relocate("dev.aurelium.slate", "dev.aurelium.auraskills.slate")
        relocate("org.spongepowered.configurate", "dev.aurelium.auraskills.configurate")
        relocate("io.leangen.geantyref", "dev.aurelium.auraskills.geantyref")
        relocate("net.querz", "dev.aurelium.auraskills.querz")
        relocate("com.archyx.polyglot", "dev.aurelium.auraskills.polyglot")
        relocate("org.atteo.evo.inflector", "dev.aurelium.auraskills.inflector")

        exclude("acf-*.properties")

        finalizedBy("copyJar")
    }

    register<Copy>("copyJar") {
        val projectVersion: String by project
        from("build/libs/AuraSkills-${projectVersion}.jar")
        into("../build/libs")
    }

    build {
        dependsOn(shadowJar)
    }

    javadoc {
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
        options.compilerArgs.add("-Xlint:deprecation")
        options.isFork = true
        options.forkOptions.executable = compiler.map { it.executablePath }.get().toString()
    }

    val projectVersion = project.version.toString()

    processResources {
        filesMatching("plugin.yml") {
            expand("projectVersion" to projectVersion)
        }
    }

    runServer {
        minecraftVersion("1.21.6")
    }

    test {
        useJUnitPlatform()

        dependsOn(shadowJar)

        doFirst {
            val mainOutputs = sourceSets["main"].output.files
            val shadedJarFile = shadowJar.get().archiveFile.get().asFile

            classpath = files(shadedJarFile) +
                    files(classpath.filter { it !in mainOutputs })
        }
    }
}

val supportedVersions = (project.property("supportedMCVersions") as String).split(",").map { it.trim() }

if (project.hasProperty("hangarApiKey")) {
    if (!(project.version as String).endsWith("-SNAPSHOT")) {
        hangarPublish {
            publications.register("AuraSkills") {
                val projectVersion = project.version as String

                version.set(projectVersion)
                id.set("AuraSkills")
                channel.set("Release")
                changelog.set(extractChangelog(projectVersion))

                apiKey.set(project.property("hangarApiKey") as String)

                platforms {
                    paper {
                        jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                        platformVersions.set(supportedVersions)
                    }
                }
            }
        }
    }
}

if (project.hasProperty("modrinthToken")) {
    if (!(project.version as String).endsWith("-SNAPSHOT")) {
        modrinth {
            val projectVersion = project.version as String

            token.set(project.property("modrinthToken") as String)

            projectId.set("auraskills")
            versionNumber.set(projectVersion)
            versionType.set("release")
            changelog.set(extractChangelog(projectVersion))
            uploadFile.set(tasks.shadowJar.flatMap { it.archiveFile }.get())
            gameVersions.set(supportedVersions)
            loaders.set(listOf("paper", "purpur", "spigot"))
        }
    }
}

fun extractChangelog(version: String): String {
    val heading = Regex.escape(version)
    val cwd = System.getProperty("user.dir")
    val isInSubmodule: Boolean = project.parent
        ?.childProjects
        ?.keys
        ?.any { cwd.endsWith(it) }
        ?: false
    val path = if (isInSubmodule) "../Changelog.md" else "Changelog.md"

    val fullChangelog = File(path).readText()
    val headingPattern = Regex("## $heading\\R+([\\s\\S]*?)\\R+##\\s", RegexOption.DOT_MATCHES_ALL)
    val result = headingPattern.find(fullChangelog)

    return result?.groupValues?.get(1)?.trim()
        ?: ""
}
