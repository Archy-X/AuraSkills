# Aurelium Skills

AureliumSkills is an advanced, feature-rich skills, stats, and abilities plugin, great for a variety of server gamemodes.

Learn more and download here: https://www.spigotmc.org/resources/81069/

### Github Disclaimer:
Versions here may be unreleased/in development, some may be very unstable.

### Support Discord:

https://discord.gg/Bh2EZfB (Join for support/suggestions/discussion)

### Wiki
The AureliumSkills wiki can be viewed [here](https://github.com/Archy-X/AureliumSkills/wiki).
The wiki contains documentation on how to use and configure the plugin. The wiki may not be always up to date.

## Developer API

Aurelium Skills provides a developer API to interact with the plugin.

### Maven
**Repository:**
```
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.Archy-X</groupId>
    <artifactId>AureliumSkills</artifactId>
    <version>{version}</version>
</dependency>
```

### Gradle
```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.Archy-X:AureliumSkills:{version}'
}
```
**Replace `{version}` with the version of the plugin you are targeting**, see [releases](https://github.com/Archy-X/AureliumSkills/releases) for the most updated list of version names.

The main class to use for the API is AureliumAPI, which has many static methods for skills, xp, stats, and mana.
There are also a few events, including ManaRegenerateEvent, SkillLevelUpEvent, XpGainEvent, PlayerLootDropEvent, and ManaAbilityActivateEvent

## Contributing
Contributions are welcome! Open a pull request, and I will review it.