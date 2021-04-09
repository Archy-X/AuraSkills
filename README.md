# Aurelium Skills

AureliumSkills is an advanced, feature-rich skills, stats, and abilities plugin, great for a variety of server gamemodes.
This Fork wants to add bungeecord synchronisation between servers with MySQL Database, which save data when player quit
the first server, and load data immediatly when playe rejoin the second server

Learn more and download here: https://www.spigotmc.org/resources/81069/

### Github Disclaimer:
Versions here may be unreleased/in development, some may be very unstable.

### Discord:

https://discord.gg/Bh2EZfB (Join for support/suggestions/discussion)

## Developer API

Aurelium Skills provides a developer API to interact with the plugin.

**Repository:**
```
<repository>
 	<id>jitpack.io</id>
 	<url>https://jitpack.io</url>
</repository>
```

**Dependency:**
```
<dependency>
   	<groupId>com.github.Archy-x</groupId>
   	<artifactId>AureliumSkills</artifactId>
   	<version>Alpha1.6.8</version>
</dependency>
```

The main class to use for the API is AureliumAPI, which has many static methods for skills, xp, stats, and mana.
There are also a few events, including ManaRegenerateEvent, SkillLevelUpEvent, XpGainEvent, PlayerLootDropEvent, and ManaAbilityActivateEvent

## Contributing
Contributions are welcome! Open a pull request, and I will review it.
