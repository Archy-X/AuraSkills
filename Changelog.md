# Changelog

Changelog for versions since 2.0.0.

## 2.0.6

### New Features
- Add support for custom menus
  - Create a new file in the menus folder to create a custom menu
  - Follows the same format as existing menus, though templates and components will not work
  - Supports PlaceholderAPI and click actions like existing menus
- Add openmenu command
  - Syntax: `/skills openmenu <menuName> [player] [properties] [page]`
  - If player is not specified, the menu opens for the sender
  - The properties argument is a JSON string required to open some menus
    - For example, `{"skill":"Skill:mining"}` should be passed when opening the level_progression, abilities, sources, and leaderboard menu
  - The page argument starts at 0 for the first page
  - Requires the auraskills.command.openmenu permission (defaults to op)
- Add ability placeholders
  - `%auraskills_[ability]%` gets the ability level
  - `%auraskills_[ability]_value%` gets the ability value
  - `%auraskills_[ability]_value_2%` gets the secondary value of the ability if there is one
  - Replace `[ability]` with the default English name of an ability in lowercase
  - AuraSkills placeholders will now show in the tab completion of /papi parse

### Bug Fixes
- Disable level up chat message for empty string
- Fix Minecraft version parsing error on initial releases like 1.20
- Placed saplings that grow into logs will now give XP
- Fix track option not working in level_progression menu
- Fix LootDropEvent Cause being unknown for some loot tables
- Fix ProtocolLib error with Geyser players
- Fix Terraform crash with AdvancedEnchantments

### API Changes
- Add MenuManager API to extend existing and create custom menus
  - Accessible with AuraSkillsBukkit#getMenuManager
  - Can be used to define behavior for custom items and templates in default or custom menus
  - Add NamespacedRegistry#setMenuDirectory for loading external menu files, which are automatically merged with default menu files before being loaded
- Make TerraformBlockBreakEvent accessible in the API

## 2.0.5

### New Features
- Add track and use_track options to sources menu
  - Used to rearrange the source items in any way rather than just a rectangle
- Add custom_model_data option to source item filter meta
  - To use, add it to a custom XP source item filter section for filtering a specific model data

### Changes
- Update messages and move identical messages across languages to global.yml

### Bug Fixes
- Fix Regions API errors
- Fix hex colors not working in titles and boss bar
- Fix trailing color codes not working in stat symbols
- Fix user skill levels being below start_level when increased
- Fix multiplier command displaying incorrect values
- Fix newlines not working in reward chat messages
- Fix Retrieval detecting tridents as arrows
- Fix skill reset command not using config start_level
- Fix Lightning Blade being activated by thorns
- Fix Alchemist replacing lore and not working for custom effects
- Fix hex colors not working in skill messages
- Fix errors with WorldGuard 7.1

## 2.0.4

### New Features
- Add xp_format and percent_format boss bar options
- Add options to sources menu to change source template area

### Changes
- Reduce jar size by removing unnecessary libraries
- Update it and ru messages (regen required to get changes)

### Bug Fixes
- Fix mana ability cooldowns not being saved when re-logging
- Fix placeholders not working after PlaceholderAPI reload
- Fix NoSuchMethodError on older versions
- Fix long delay in leaderboard updating on startup
- Fix acf section of messages file not applying
- Fix abilities showing in menus and messages above max_level
- Fix trait max_mana is not loaded error
- Fix mana ability is not loaded error
- Fix formatting not working in menu titles
- Fix stack overflow when opening skills menu with too much XP
- Fix mana command help not showing parameter names

## 2.0.3

### New Features
- Add formula option to damage_reduction trait
- Add scale_xp_with_health entity source option
  - This is true by default for entity sources with damage trigger
- Add mana.enabled option to more easily disable mana

### Bug Fixes
- Fix skills menu migration breaking
- Fix permission-blocked skills increasing stat levels
- Fix entity sources with damage trigger not scaling XP with damage
- Fix Bleed not giving XP on entity death/damage
- Fix disabled traits still showing in stats menu
- Fix skill name commands not working

### API Changes
- Add DamageXpGainEvent

## 2.0.2

### New Features
- Add excluded_enchantments option for Enchanted Strength in abilities.yml
- Add lore_wrapping_width option in config.yml

### Bug Fixes
- Fix NPCs being unable to attack mobs
- Fix Fleeting not being removed after heal commands
- Fix console error when using Vault without an economy plugin

## 2.0.1

### New Features
- Add formula support in money rewards

### Bug Fixes
- Fix item modifier NBT conversion breaking items
- Fix action_bar.enabled option not fully working
- Fix message file headers
- Fix rank command error

## 2.0.0

### New Features
- Customizable XP sources
  - New config files for configuring sources of each skill in the sources folder.
  - Each source now has a specific type and options that fully define when XP is gained. For example, all sources for breaking blocks have type block.
  - New sources can be added just like default ones, with the type options allowing much more customization and control.
  - Examples of what is possible:
    - Gaining XP when a player breaks a block matching any specific block state (allows custom blocks).
    - Gaining XP for increasing any Minecraft statistic, like the existing running and walking sources.
    - Gaining XP for consuming a specific item.
    - Gaining XP for enchanting or brewing a custom item.
    - Gaining XP for fishing up a custom item.
  - Sources are no longer tied to their default skill, they can be switched around to any skill.
- Modular skill system
  - The new skills.yml file is where individual skills are now configured.
  - Abilities and mana abilities are no longer tied to their default skill, they can be moved between skills.
  - Custom skills, stats, abilities, and mana abilities can be added using the new API.
- New abilities
  - New Farming ability: Growth Aura
    - Crops with growth stages within 30 blocks of you grow {value}% faster.
    - Unlocks at farming 5 by default.
  - New Archery ability: Retrieval
    - Arrows you miss will be instantly retrieved after 3s up to {value} blocks away.
    - Unlocks at Archery 1 by default.
  - New Fighting ability: Parry
    - Missing a sword swing within 0.25s before being hit reduces damage by {value}% and cancels knockback.
    - Unlocks at Fighting 1 by default.
- New stats
  - Crit Damage, Crit Chance, and Speed have been added as individual stats.
  - The old Crit Chance Archery ability and Crit Chance Fighting ability have been replaced with normal stat rewards, allowing a new ability to be added for each.
  - The Speed stat always shows the player's accurate speed, where 100 = 100% (normal) speed. While skills don't directly increase speed by default, you can use it in rewards or modifiers.
- Traits are a new system that makes stats more modular and configurable.
  - A trait represents a single mechanic that a stat changes/improves. For example, the Wisdom stat increases experience gain, reduces anvil costs, and increases max mana.
  - These aspects are now represented as the individual traits experience_bonus, anvil_discount, and max_mana.
  - Traits can be moved between stats.
  - Both stats and traits can be easily disabled with one option, instead of having to remove every stat reward.
  - When linking a trait to a stat, the modifier option determines how much of the trait you gain for each stat level.
  - When a trait has a modifier of 1 (meaning it's the same number of the stat), it can be displayed instead of the stat value.
  - This allows the health stat to always display the accurate HP amount you have in menus, including the default 20 HP and HP from other plugins.
- Luck Rework
  - Instead of increasing the Minecraft Luck attribute, which most players don't understand, Luck now increases the new traits Farming Luck, Foraging Luck, Mining Luck, Fishing Luck, and Excavation Luck.
  - These traits increase the chance for extra item drops of any XP source of those skills.
  - When a player's luck trait is 150 for example, they have a guaranteed +1 drop and a 50% chance for +2 drops.
  - Luck traits are also increased by the specific ability in each gathering skill, such as Bountiful Harvest.
  - The Triple Harvest Farming ability has been removed, since Bountiful Harvest just increases Farming Luck, doing the same thing.
- Mob and Farming loot tables
  - Custom loot tables can now be created for killing mobs and harvesting farming crops.
  - Create the loot/mob.yml file to add mob loot tables and specify specific mob types with the mobs pool option.
  - Any skill with block sources can now have their own custom loot tables, including farming. Create a file named after the skill, such as loot/farming.yml.

### Changes
- The plugin has been renamed from AureliumSkills to AuraSkills. This means the data folder is now plugins/AuraSkills.
  - Why? The new name is shorter, simpler, and more meaningful. The original plugin name didn't have a meaning when first made, and was pretty long to say and type.
  - For plugins that hook into AureliumSkills, the renaming means compatibility with Beta versions won't break, and a new hook system can be implemented by developers with the brand new and improved API.
  - Plugins will be able to support both Beta and 2.0 at the same time easily.
  - The new data folder allows migration to be implemented more easily, without overriding existing files. This means any problems from migration can be reverted.
  - PlaceholderAPI placeholders now use the prefix %auraskills_. Old placeholders using %aureliumskills_ will still work, but you should gradually convert your placeholders over to the new prefix.
  - All permissions have been renamed and reorganized. Old ones will not work. See the permissions list for the up-to-date list.
- Skill merging and removal
  - Because some skills were too slow to level up and had abilities that weren't very useful, some skills have been merged/removed by default. This brings the total skill count from 15 to 11.
  - The existing 15 skills can still be used by applying the legacy config preset using /skills preset load legacy.zip.
  - If you migrated from Beta, the legacy preset will be automatically applied and you will still have 15 skills. The points below will not apply.
  - Endurance has been merged into Agility.
  - Healing has been merged into Alchemy.
  - Forging has been merged into Enchanting.
  - The XP sources from the removed skills (Endurance, Healing, and Forging) have been added to the skills they merged with, so you can still gain XP in the same way.
  - The abilities deemed most useful out of the two skills have been kept, the rest have been not included in default configs.
  - No abilities have been deleted, you can still add them back in skills.yml.
  - Sorcery has been removed from defaults since it never had any abilities.
- Config format
  - The format and location of config files has signifcantly changed.
  - All keys in config.yml (Main config) now use underscores instead of hyphens.
  - Skill configuration has been moved to skills.yml.
  - Stat configuration has been moved to stats.yml.
  - Ability configuration has been moved to abilities.yml.
  - Mana ability configuration has been moved to mana_abilities.yml.
  - Sources configuration has been moved to the sources folder.
- Message format
  - Messages in the menus section, as well as skill, stat, and ability descriptions no longer contain formatting. Formatting is now fully done in the menus configuration.
  - Legacy color codes with & have been replaced with MiniMessage tags. Legacy codes will still work if you want to use them.
  - Newlines (\n) have been removed from most messages since menus will insert them automatically through lore wrapping.
  - Messages that were the same for every language have been moved to a new messages/global.yml file.
  - The messages for block and mob names have been removed, as these messages are now replaced on the client-side depending on the language the client is using.
- Menu format
  - Legacy color codes with & have been replaced with MiniMessage tags. Legacy codes will still work if you want to use them.
  - Added menu components, which are lines for certain templates that are inserted into the lore.
  - Added a formats section that defines formats for some items. Most of these were moved from the messages file.
  - Added different lore line types, with options for automatically wrapping to new lines if the current line is too long.
  - Added groups, which allow contexts to be defined by an order within an area of the menu, instead of a static position.
  - Placeholders that replace directly from the messages file are now in double curly braces like {{this}}.
  - Placeholders in single curly braces are data placeholders replaced by the plugin directly, usually numbers.
  - Context sections for templates are now under a contexts section instead of directly under the template.
  - The unlocked, locked, and in_progress templates in the level_progression menu now have their context from 1-max_level of the skill instead of 1-items_per_page.
- Default config changes
  - Added a start_level option to the main config to define what skill levels players start at by default. Players now start at skill level 0, instead of 1.
  - boss_bar.mode has been changed to single.
  - enable_roman_numerals is now false.
  - Added options to configure the critical hologram colors; the default damage_holograms.colors.critical.digits now start at white instead of gray.
  - auto_save.enabled is now true.
  - The max level of all skills is now 100 instead of 97.
  - Abilities of some skills have been changed due to the skill merging.
  - The modifier for health and hp is now 1, so the health stat matches the Minecraft HP value.
  - The health stat reward values in the rewards files have been changed to 0.4 to account for this. This means players will see +0.4 Health when leveling, which is accurate to the action bar.
  - The hearts option in the hp trait has been changed to show more visual hearts earlier to account for less overall possible HP.
  - Mana regeneration has been heavily reduced to make mana an actual relevant cost (0.1 to 0.02).
  - The modifier for Strength has been decreased from 0.5 to 0.4
  - Modifiers for some traits have been scaled differently from the existing stat modifier values.
  - Base mana regen is now 0.1 per second instead of 1.
  - Overflow mana (over max mana) is no longer allowed.
- Item NBT format
  - The way item/armor modifiers, requirements, and multipliers are stored on items has changed.
  - The new format uses the built-in PersistentDataContainer API instead of an external library dependent on NMS code.
  - Old items should be automatically converted to the new format when held.
- Damage
  - Most damage modifiers, except Strength, are now additive instead of multiplicative.
  - This means the percent increases for Crit Damage, tool master abilities, and First Strike will be added together before multiplying the damage.

### Technical Changes
- Codebase structure
  - The codebase has been split into multiple modules to allow the api to be independent from implementation.
  - The package for the plugin classes is now dev.aurelium.auraskills instead of com.archyx.aureliumskills.
- API
  - The 2.0 releases adds a brand-new API that is much more capable and extensive than before. It also supports adding custom content (skills, stats, abilities).
- The plugin is now licensed under GPL3. For one, this means that forks or modifications to the plugin that you distribute must also have their source-code available.