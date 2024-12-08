import org.gradle.api.tasks.Copy
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("com.modrinth.minotaur") version "2.+"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.helpch.at/releases")
    maven("https://jitpack.io")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.oraxen.com/releases")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":api-bukkit"))
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("de.tr7zw:item-nbt-api:2.14.1-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("net.kyori:adventure-text-minimessage:4.16.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.3")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
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
    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("io.lumine:Mythic-Dist:5.6.1")
    compileOnly("io.th0rgal:oraxen:1.173.0")
}

val compiler = javaToolchains.compilerFor {
    languageVersion = JavaLanguageVersion.of(17)
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
        val projectVersion : String by project
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
        options.isFork = true
        options.forkOptions.executable = compiler.map { it.executablePath }.get().toString()
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("projectVersion" to project.version)
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
    val path = if (System.getProperty("user.dir").endsWith("bukkit")) "../Changelog.md" else "Changelog.md"

    val fullChangelog = File(path).readText()
    val headingPattern = Regex("## $heading\\R+([\\s\\S]*?)\\R+##\\s", RegexOption.DOT_MATCHES_ALL)
    val result = headingPattern.find(fullChangelog)

    return result?.groupValues?.get(1)?.trim()
        ?: throw IllegalArgumentException("Failed to extract changelog section for version $version")
}

