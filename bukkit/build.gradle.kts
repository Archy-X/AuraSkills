import org.gradle.api.tasks.Copy
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    idea
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://github.com/deanveloper/SkullCreator/raw/mvn-repo")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":api-bukkit"))
    implementation("dev.dbassett:skullcreator:3.0.1")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("de.tr7zw:item-nbt-api:2.12.2")
    implementation("com.github.Archy-X:XSeries:887fe61174")
    implementation("org.bstats:bstats-bukkit:3.0.0")
    implementation("com.github.Archy-X:Slate:59d515020e") {
        exclude("org.spigotmc", "spigot-api")
    }
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.5")
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.5.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.TownyAdvanced:Towny:0.98.3.6")
    compileOnly("com.github.Slimefun:Slimefun4:RC-32")
}

tasks.withType<ShadowJar> {
    val projectVersion: String by project
    archiveFileName.set("AuraSkills-${projectVersion}.jar")

    relocate("co.aikar.commands", "dev.aurelium.auraskills.acf")
    relocate("co.aikar.locales", "dev.aurelium.auraskills.locales")
    relocate("fr.minuskube.inv", "dev.aurelium.auraskills.inv")
    relocate("de.tr7zw.changeme.nbtapi", "dev.aurelium.auraskills.nbtapi")
    relocate("com.cryptomorin.xseries", "dev.aurelium.auraskills.xseries")
    relocate("org.bstats", "dev.aurelium.auraskills.bstats")
    relocate("com.udojava.evalex", "dev.aurelium.auraskills.evalex")
    relocate("com.archyx.slate", "dev.aurelium.auraskills.slate")
    relocate("net.kyori.adventure", "dev.aurelium.auraskills.adventure")
    relocate("net.kyori.examination", "dev.aurelium.auraskills.examination")

    finalizedBy("copyJar")
}

java.sourceCompatibility = JavaVersion.VERSION_17

tasks.register<Copy>("copyJar") {
    val projectVersion : String by project
    from("build/libs/AuraSkills-${projectVersion}.jar")
    into("../build/libs")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    jar {
        dependsOn(shadowJar)
    }
    javadoc {
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
    options.isFork = true
    options.forkOptions.executable = "javac"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}