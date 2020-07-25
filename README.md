# **Aurelium Skills**
AureliumSkills is an advanced, feature-rich skills, stats, and abilities plugin currently in Pre-Alpha development.

AureliumSkills heavily utilizes inventory GUIs to make the player experience more interactive and convenient. Current features are limited, updates coming in the future.

## **Requirements:**
**This plugin requires SmartInvs-1.2.7 or higher in your server plugins** (Make sure you are running AureliumSkills vPre-Alpha1.0.1 or higher, if not, download again).
Download SmartInvs here: https://www.spigotmc.org/resources/smartinvs-advanced-inventory-api.42835/

## **Skills**
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

## **Stats**
Stats are player specific buffs that directly link into Skills in a very organized and logical way! There are a total of 6 unique stats:
- Strength (Increases base attack damage)
- Health (Increases max health)
- Regeneration (Increases health regen speed)
- Luck (Increase luck and gives chance of double drops)
- Wisdom (Increases xp gain and reduces anvil costs)
- Toughness (Decreases incoming damage)
Every Skill has a unique combination of 2 stats that it levels up! These are categorized into primary and secondary. Primary stats gain one level for every skill level. Secondary stats gain one level for every other skill Level.

## **Commands**
- /skills or /skill or /sk - Opens Skills Menu
- /stats - Opens Stats Menu
- /sk ability setlevel <player> <ability> <level> - Sets ability level (aureliumskills.ability.setlevel)
- /sk skill setlevel <player> <skill> <level> - Sets skill level (aureliumskills.skill.setlevel)
- /sk xp add <player> <skill> <amount> - Adds skill xp to player (aureliumskills.xp.add)
- /sk top [skill] - Shows skill leaderboard (aureliumskills.top)
- /sk lang <lang> - Changes language (aureliumskills.lang)
- /sk reload - Reloads config files (aureliumskill.reload)

## **More info**
The plugin uses the action bar to display Health and Mana, as well as whenever Skill XP is gained.
The plugin has sound effects and title texts when skills are leveled up.
Every Skill has a level progression menu that displays the rewards for all levels, and your current progress.

_This is in Alpha development, features are incomplete and there may be many bugs._
