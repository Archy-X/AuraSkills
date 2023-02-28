import org.gradle.api.tasks.Copy
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://github.com/deanveloper/SkullCreator/raw/mvn-repo")
    }
    maven {
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }
    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven {
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("dev.dbassett:skullcreator:3.0.1")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("de.tr7zw:item-nbt-api:2.11.0")
    implementation("com.github.Archy-X:XSeries:887fe61174")
    implementation("org.bstats:bstats-bukkit:3.0.0")
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("com.udojava:EvalEx:2.7")
    implementation("com.github.Archy-X:Slate:7a9fa2f588")
    implementation("com.github.Archy-X:LootManager:60d109fdde")
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.5")
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.5.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.dmulloy2:ProtocolLib:43145bd478")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.TownyAdvanced:Towny:0.98.3.6")
    compileOnly("com.github.Slimefun:Slimefun4:RC-32")
}

tasks.withType<ShadowJar> {
    val projectVersion: String by project
    archiveFileName.set("AureliumSkills-${projectVersion}.jar")

    relocate("co.aikar.commands", "com.archyx.aureliumskills.acf")
    relocate("co.aikar.locales", "com.archyx.aureliumskills.locales")
    relocate("fr.minuskube.inv", "com.archyx.aureliumskills.inv")
    relocate("de.tr7zw.changeme.nbtapi", "com.archyx.aureliumskills.nbtapi")
    relocate("com.cryptomorin.xseries", "com.archyx.aureliumskills.xseries")
    relocate("org.bstats", "com.archyx.aureliumskills.bstats")
    relocate("com.udojava.evalex", "com.archyx.aureliumskills.evalex")
    relocate("com.archyx.slate", "com.archyx.aureliumskills.slate")
    relocate("net.kyori.adventure", "com.archyx.aureliumskills.adventure")
    relocate("net.kyori.examination", "com.archyx.aureliumskills.examination")

    finalizedBy("copyJar")
}

tasks.register<Copy>("copyJar") {
    val projectVersion : String by project
    from("build/libs/AureliumSkills-${projectVersion}.jar")
    into("../build/libs")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}