<h1 style="text-align:center;">AuraSkills</h1>

<p style="text-align:center;">
The ultra-versatile Minecraft RPG skills plugin
</p>

[![GitHub Release](https://img.shields.io/github/v/release/Archy-X/AuraSkills?style=flat-square)](https://github.com/Archy-X/AuraSkills/releases/latest)
[![Maven Central Version](https://img.shields.io/maven-central/v/dev.aurelium/auraskills-api-bukkit?style=flat-square&color=%238529F5)](https://central.sonatype.com/artifact/dev.aurelium/auraskills-api-bukkit)
[![Spiget Downloads](https://img.shields.io/spiget/downloads/81069?style=flat-square)](https://www.spigotmc.org/resources/81069/)

<p style="text-align: center;font-weight: bold;">
  <a href="https://aurelium.dev/auraskills/download">Downloads</a>
  &nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
  <a href="https://wiki.aurelium.dev/auraskills">Wiki</a>
  &nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
  <a href="https://discord.gg/Bh2EZfB">Discord</a>
</p>

## About

**AuraSkills** (formerly **Aurelium Skills**) is a Minecraft plugin that adds skills, stats, abilities, and other RPG-related features. The plugin is fully configurable and customizable, enabling usage on a wide range of server types.

Features include:
- **Skills** - Gain skill XP to level skills through general Minecraft tasks, such as Farming or Mining.
- **Stats** - Get player buffs like increased health and damage by leveling skills, which can be as independent modifiers and on items.
- **Abilities** - Skills have passive and active abilities that add gameplay mechanics, plus a full mana system.
- **Menus** - Players can see everything related to skills in fully-configurable inventory GUIs.
- **Rewards** - Customize rewards given for leveling skills, such as running commands or giving items.
- **Loot** - Create custom loot tables for fishing, blocks, and mobs.

See the [official website](https://aurelium.dev/auraskills) and [wiki](https://wiki.aurelium.dev/auraskills) for a more complete list of features. The wiki also contains the list of [server requirements](https://wiki.aurelium.dev/auraskills/server-requirements) to run the plugin.

## Building

AuraSkills uses Gradle for dependencies and building.

#### Compiling from source

First, clone the project (requires Git to be installed):

```
git clone https://github.com/Archy-X/AuraSkills.git
cd AuraSkills/
```

Then build depending on your operating system:

Linux / macOS

```
./gradlew clean build
```

Windows

```
.\gradlew.bat clean build
```

The output jar can be found in the `build/libs` directory.

## API

AuraSkills has an extensive developer API.

Read the full API documentation on the [wiki](https://wiki.aurelium.dev/auraskills/api), or view the [Javadocs](https://docs.aurelium.dev/auraskills-api-bukkit/).

Release versions are published to the Maven central repository.

### Maven

```xml
<dependency>
    <groupId>dev.aurelium</groupId>
    <artifactId>auraskills-api-bukkit</artifactId>
    <version>2.2.0</version>
    <scope>provided</scope>
</dependency>
```
### Gradle

**Groovy DSL:**
```gradle
repositories {
    mavenCentral()
}

dependencies {
    compileOnly 'dev.aurelium:auraskills-api-bukkit:2.2.0'
}
```
**Kotlin DSL:**
```Gradle Kotlin DSL
repositories { 
    mavenCentral()
}

dependencies { 
    compileOnly("dev.aurelium:auraskills-api-bukkit:2.2.0")
}
```

## Contributing
Contributions are welcome, just open a pull request.