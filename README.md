# Aurelium Skills

AureliumSkills is an advanced, feature-rich skills, stats, and abilities plugin, great for a variety of server gamemodes.

AureliumSkills heavily utilizes inventory GUIs to make the player experience more interactive and convenient. Current features are limited, updates coming in the future.

#### Github Disclaimer:
Versions here may be unreleased/in development, some may be very unstable.

### Discord:

https://discord.gg/Bh2EZfB (Join for support/suggestions/discussion)

### Requirements:

Requires server running Spigot, Paper, or fork of those. CraftBukkit will not work.

### Skills

There are a total of 15 custom Skills that players can level up! Words in parenthesis indicate the actions used to level up that skill. Brackets indicate the stats the skill levels in order of primary then secondary.
- Farming (Harvesting crops) [H, S]
- Foraging (Chopping wood) [S, T]
- Mining (Mining ores) [T, L]
- Fishing (Fishing) [L, H]
- Excavation (Digging) [R, L]
- Archery (Killing mobs with a bow) [L, S]
- Defense (Taking damage) [T, H]
- Fighting (Killing mobs with melee) [S, R]
- Endurance (Running and walking) [R, T]
- Agility (Jumping and fall damage) [W, R]
- Alchemy (Brewing potions) [H, W]
- Enchanting (Enchanting items) [W, L]
- Sorcery (Not yet implemented) [S, W]
- Healing (Drinking and splashing potions) [R, H]
- Forging (Combining items in an anvil) [T, W]

### Stats

Stats are player specific buffs that directly link into Skills in a very organized and logical way! There are a total of 6 unique stats:
- Strength (Increases base attack damage)
- Health (Increases max health)
- Regeneration (Increases health and mana regen speed)
- Luck (Increase luck attribute and has double drop chance)
- Wisdom (Increases experience gain, max mana, and decrease anvil costs)
- Toughness (Reduces incoming damage)

Every Skill has a unique combination of 2 stats that it levels up! These are categorized into primary and secondary. Primary stats gain one level for every skill level. Secondary stats gain one level for every other skill Level.

### Commands

- /skills or /skill or /sk - Opens Skills Menu
- /stats - Opens Stats Menu
- /sk skill setlevel <player> <skill> <level> - Sets skill level (aureliumskills.skill.setlevel)
- /sk xp add <player> <skill> <amount> - Adds skill xp to player (aureliumskills.xp.add)
- /sk top [skill] - Shows skill leaderboard (aureliumskills.top)
- /sk lang <lang> - Changes language (aureliumskills.lang)
- /sk reload - Reloads config files (aureliumskill.reload)

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
   	<version>Alpha1.5.1</version>
</dependency>
```

The main class to use for the API is AureliumAPI, which has many static methods for skills, xp, stats, and mana.
There are also a few events, including ManaRegenerateEvent, SkillLevelUpEvent, and XpGainEvent.

