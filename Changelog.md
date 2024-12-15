# Changelog

Changelog for versions since 2.0.0.

## 2.2.7

### New Features
- Add 1.21.4 support
- Add pale garden blocks to Foraging sources
- Add support for external items anywhere that accepts an item key

### Changes
- Change MythicDamageEvent priority from high to normal
- Support {color} placeholder in leveled_by component of stats menu
- Make anvil discount trait formula configurable in stats.yml
- Change update checker to use Modrinth with loader and version filters

### Bug Fixes
- Fix slimes and groups of mobs attacking rapidly with no cooldown
- Fix hex colors not working in loot entry message
- Fix xp-gain and custom-loot WorldGuard flags with Fishing loot
- Fix offline player skins not working in leaderboard menu
- Fix double plus in lore when adding some trait modifiers

## 2.2.6

### Bug Fixes
- Fix IncompatibleClassChangeError on 1.21.1 and below caused by 2.2.5 update

## 2.2.5

### New Features
- Add 1.21.3 support

### Changes
- The `{skill}` and `{skill_key}` placeholders can now be used anywhere in the level progression menu

### Bug Fixes
- Prevent errors from abnormal server version strings

## 2.2.4

### New Features
- Add more permission nodes with child permissions
  - auraskills.* contains permissions for all commands
  - auraskills.command.user contains permissions users have by default
  - auraskills.skill.* contains permissions to use all skills
  - auraskills.command.item.* contains permissions related to item modifier creation
  - auraskills.command.armor.* contains permissions related to armor modifier creation
  - auraskills.command.admin contains permissions for all admin/op commands

### Changes
- Respect cancelled drops when applying block luck
- Only show loaded skills in profile command
- Update Chinese Simplified, Chinese Traditional, and Indonesian messages

### Bug Fixes
- Fix health value must be between error when changing worlds
- Fix boss bar progress must be between error when max skill level
- Fix NPE when updating leaderboards on MySQL

### API Changes
- SkillsUser#setSkillLevel will now refresh stats, permissions, rewards, and item modifiers
- Add setSkillLevel method with boolean parameter to control whether to refresh stats
- Add CustomManaAbilityBuilder#infoFormats to set the list of menu format names to use in the menu description

## 2.2.3

### New Features
- Add new damage source options
  - Add excluded_damager/excluded_damagers to prevent XP gain from specific entity types
  - Add cooldown_ms to define a delay before XP can be gained again in milliseconds (200 by default)
  - Add damagers option to define a list of valid damagers
- Add job selection cooldown option (jobs.selection.cooldown_sec)

### Changes
- Update German, Polish, and Italian messages
- Make all region loading async

### Bug Fixes
- Fix error loading menu on Spigot 1.21
- Fix typos in excavation loot file
- Implement error handling for global requirement material names
- Fix trait_experience_bonus placeholder not working
- Fix boss bar progress must be between error

## 2.2.2

### New Features
- Add 1.21.1 support
- Add use_permission_cache option to disable LuckPerms multiplier permission cache

### Bug Fixes
- Fix anti-AFK being force enabled after reload
- Skip sending item give command message if empty

## 2.2.1

### Bug Fixes
- Fix cannot measure distance error in PositionHandler
- Fix logs table not being created

## 2.2.0

### New Features
- Add anti-AFK system for preventing AFK XP gain
  - This is an optional feature that detects and blocks repetitive AFK skill XP gain, encouraging active play
  - Must be manually enabled with the `anti_afk.enabled` option in the main config
  - There are 8 types of checks that can block AFK XP gain in the following skills: Farming, Foraging, Mining, Fishing, Excavation, Fighting, Archery, and Defense
  - The checks work in one of three ways: detecting identical player coordinates, facing direction (pitch/yaw), or the entity being involved
  - Checks are configured in the `anti_afk.checks` section
    - Specific checks can be toggled with the `enabled` option
    - The `min_count` option is the number of identical XP gain conditions in a row for the player to be blocked from gaining XP
    - The `max_distance` option for some checks is the threshold for which movement below that value is still considered identical
  - Logging for failing anti-AFK checks can be enabled with the `anti_afk.logging_enabled` option
    - The `log_threshold` is an expression determining the count of identical conditions required for logging an event. The min_count variable can be used in this expression to reference the min_count in the checks section for a check type.
    - Logs will be sent to any online player with the auraskills.antiafk.notify permission (op by default)
    - Logs are saved to storage can can be viewed for any player with the `/skills antiafk logs <player> [page] [perPage]` command (auraskills.command.antiafk.logs permission)
  - Important disclaimer: A player failing checks and being logged does not necessarily mean they are cheating or using a macro, as false positives can be common for legitmate players, especially if min_count is low. An example is a player using a mob farm manually can still trigger anti-AFK checks. Always check logged players manually before taking any action.
- Add entity loot for fishing loot tables
  - The new entity loot type adds the ability to catch entity with fishing
  - Defined by a loot entry with `type: entity`
  - The `entity` key defines the type of entity caught
    - Vanilla entities/mobs are defined by name (e.g. `entity: zombie`)
    - MythicMobs can be used with the `mythicmobs:` prefix (e.g. `entity: mythicmobs:SkeletonKing`)
  - The `health` key overrides the entity max health attribute
  - The `damage` key overrides the entity attack damage attribute
  - The `level` key can be used to define the AuraMobs or MythicMobs level
  - The default vertical and horizontal velocities when caught can be overridden with `velocity.horizontal` and `velocity.vertical`
  - The keys `hand`, `off_hand`, `feet`, `legs`, `chest`, and `head` are used to define equipment on the spawned entity
    - The value is a mapping in the same item format as regular item loot (Using keys `material`, `enchantments`, etc.)
- Add boss bar animation using delay when upgrading progress
  - This allows players to visually see the change in progress when a new boss bar is shown
  - Can be toggled with the `boss_bar.animate_progress` option in the main config
- Add support for multiple commands on one loot entry
  - Use a `commands` list of strings on a command loot entry instead of `command`

### Changes
- Optimize leaderboard updating for SQL
- Optimize SQL user loading and saving
  - A user is now loaded in a single statement with subqueries
  - Saving key_values will now utilize batched statements

### Bug Fixes
- Fix disabled abilities still showing in abilities menu
- Fix level up title not working in 1.20.5+
- Fix XP Convert giving extremely high experience values
- Fix boss bar income showing for jobs enabled without economy

### API Changes
- Relocate and create Configurate API wrapper (breaking change)
  - ConfigurationNode has been replaced with the ConfigNode wrapper class in api and api-bukkit
  - Configurate is no longer included in the api module as a dependency
  - Any plugins working with ConfigurationNode in the API will break and must be updated (such as using LootParser, creating custom source types, or ItemManager#parseItem)
  - Updating is done by simply renaming ConfigurationNode mentions to ConfigNode, as most methods are copied over exactly
  - This change was made to fix critical loading errors with plugins like Nova
- Add trait modifier methods to ItemManager
  - Deprecate addModifier, getModifiers, and removeModifier to be replaced by addStatModifier, getStatModifiers, and removeStatModifier
- Add action bar methods to SkillsUser for sending and pausing action bars

## 2.1.5

### New Features
- Add Oraxen support
  - Oraxen note blocks defined in sources will now give XP properly
  - Custom blocks can now be easily defined in sources by prefixing the `block` option of a block source with `oraxen:`
    - For example, `block: oraxen:mythril_ore` will automatically register the block state from Oraxen without needing to manually define a `state`
  - Oraxen items can be directly used in any source `menu_item` option using a string key prefixed with `oraxen:`
    - For example, `menu_item: oraxen:mythril` will use the exact item defined in Oraxen, including any NBT
    - This is defined as a string value directly on the `menu_item` key rather than the map section used for normal menu items
  - Custom loot from Oraxen blocks will drop extra items correctly with Luck traits like Mining Luck
- Add requirement.override_global option to main config
  - If true, global requirements will be ignored if item-specific requirements are defined
- Add mana.cooldown_timer_period option to main config
  - Increasing can help reduce lag caused by TimerCountdown for mana abilities
- Add sql.pool options to main config
  - The new options maximum_pool_size, minimum_idle, connection_timeout, max_lifetime, and keepalive_time are used to configure the Hikari connection pool
  - These should not be changed unless you have issues with connection stability and know what you are doing

### Bug Fixes
- Fix power leaderboard not adding custom skills
- Fix leaderboard still showing for disabled skills
- Fix global requirements and world options not being updated on skills reload
- Fix roman placeholders with value 0 not working
- Fix ManaAbilityRefreshEvent being called invalidly
- Fix hp keep_full_on_increase not working sometimes
- Fix snow golem and mooshroom not giving XP in 1.20.5+

## 2.1.4

### New Features
- Add jobs income batching options to reduce economy lag
  - If jobs.income.batching.enabled is true, accumulated income will be given in an interval instead of immediately
  - The interval_ms option controls the minimum delay between when money is given
  - If display_individual is true, the boss bar will display the original individual income instead of batched

### Changes
- Optimize checking for disabled sources using cache

### Bug Fixes
- Fix XP duplication exploit with land claiming plugins
- Fix resethealth command not working on 1.21
- Fix modifier is already applied error with stun
- Fix NoSuchMethodError on 1.19.4
- Fix PlaceholderAPI not working in level up message
- Fix negative XP gain after negative multipliers

## 2.1.3

### New Features
- Minecraft 1.21 support
  - Add bogged and breeze to Fighting and Archery sources
- Add new action_bar options to main config
  - format_last - If true, parsing of MiniMessage will happen after placeholders are replaced on each send. This makes MiniMessage gradients work at the cost of performance (false by default).
  - update_async - If true, the idle action bar will be updated and sent asynchronously. This is an experimental option (false by default).

### Changes
- The option modifier.auto_convert_from_legacy is now false by default

### Bug Fixes
- Fix Lightning Blade attribute modifier not being removed
- Fix backup debug message spam
- Fix disabled traits not being fully disabled sometimes
- Fix hex colors not working in level up title and chat message
- Fix hex colors not working in action bars

## 2.1.2

### New Features
- Add roman and int placeholders for abilities (thanks Erik)
  - `%auraskills_[ability]_roman%` - Gets ability level as a Roman numeral
  - `%auraskills_[ability]_value_int%` - Gets ability value rounded to an integer
  - `%auraskills_[ability]_value_2_int%` - Gets secondary ability value rounded to an integer if it exists
  - `%auraskills_mability_[ability]_roman%` - Gets mana ability level as a Roman numeral
  - `%auraskills_mability_[ability]_value_int%` - Gets mana ability value rounded to an integer
- Add correct_over_max_level option to main config to prevent skill levels being over max level on join (false by default)
- Add shape option to menu fill items
  - Available values are border, top_row, bottom_row, left_column, and right_column

### Changes
- Revert unnecessary AdvancedEnchantments event ignores, fixing compatibility
- Update German and Chinese Simplified messages

### Bug Fixes
- Fix split slimes and magma cubes not applying spawner_multiplier
- Fix double reloading on addStatModifier (thanks Erik)
  - This fixes extra health resets with plugins like MMOItems
- Fix Charged Shot not applying damage bonus
- Fix NoSuchFieldError on older versions when using mana abilities
- Fix RoseStacker spawner mobs not working with spawner_multiplier
- Fix armor add command not accepting decimal values
- Fix level progression total page count sometimes being wrong
- Fix food level decreasing at full health with use_custom_delay true

## 2.1.1

### New Features
- Add auraskills.jobs.block.[skill] permission to prevent skills from being selected as jobs if set to true (ex: auraskills.jobs.block.mining)
- Add `%auraskils_jobs_active_[skill]%` placeholder that returns true/false whether the job is active (ex: %auraskills_jobs_active_farming%)

### Changes
- Support property placeholders in menu placeholder conditions, such as {skill} for the skill name in certain menus
- Slightly tweaked the jobs item lore style in the level_progression menu

### Bug Fixes
- Fix grindstone experience exploit
- Fix NoSuchMethodError on older versions

## 2.1.0

### New Features
- Add jobs system
  - Jobs is an optional feature that allows players to select skills as jobs to earn money while gaining XP.
  - To use jobs, the jobs.enabled option must be set to true in config.yml
  - Income for all XP sources is configured under jobs.income
    - If use_xp is true, sources will give a multiple of the XP gained based on default.income_per_xp
    - If use_expression is true, sources will give XP based on the result of default.expression
      - The available variables include xp, base_xp (value without multipliers), level (skill level), power, and skill_average
    - If use_final_xp is set to false, the calculation for income_per_xp will exclude all XP multipliers
  - Job selection is configured under jobs.selection
    - Players select a skill as a job using a new item in the level progression menu of a skill (gold_ingot by default)
    - If require_selection is set to false, players will gain income for all skills and selection will be hidden
    - The default_job_limit is the maximum number of skills that can be selected as jobs at the same time
    - The limit can be changed per-player using the auraskills.jobs.limit.[number] permission node (ex: auraskills.jobs.limit.4)
    - The disable_unselected_xp option will block gaining XP in all skills that are not active jobs if set to true
  - Income can also be configured per-source inside the sources files
    - Keys can be added to both the default section or an individual source
    - The following keys can be added to a source:
      - income_per_xp - The income to give per XP gained, works the same as the value in config.yml
      - income - A fixed decimal value for the money to give
      - income_expression - An expression to calculate the income, works the same as the expression in config.yml
  - Menu files will automatically update to add the new items and components to show job selection
    - However, you must manually add a new line `- component: skill_job_active` to the lore of the skill template in menus/skills.yml under `- component: max_level`
    - Everything will still work if this line isn't manually added, players just won't see the "Active Job" displayed in the skills menu
  - New jobs commands:
    - `/skills jobs add <job> [user]` - Joins a specific job for yourself or a different player
    - `/skills jobs remove <job> [user]` - Quits a specific job for yourself or a different player
    - `/skills jobs removeall [user]` - Quits all jobs for yourself or a different player
    - When user is not specified, these commands use the auraskills.command.jobs permission (op by default)
    - To specify a different user, the sender must also have the auraskills.command.jobs.other permission (op by default)
  - New jobs PlaceholderAPI placeholders:
    - `%auraskills_jobs_list%` - Lists active jobs in a comma separated list of default skill names in all lowercase
    - `%auraskills_jobs_list_formatted%` - Lists active jobs in a comma separated list with the skill display name of the config default_language
    - `%auraskills_jobs_count%` - Gets the number of jobs the player currently has active
    - `%auraskills_jobs_limit%` - Gets the maximum number of jobs the player is allowed to have active at the same time
- Add trait commands for adding trait modifiers directly to players
  - `/skills trait add <player> <trait> <name> <value> [silent] [stack]` - Adds a trait modifier to a player with the given name
  - `/skills trait remove <player> <name> [silent]` - Removes a trait modifier from a player with the given name
  - `/skills trait list [player] [trait]` - Lists trait modifiers of a player
  - `/skills trait removeall [player] [trait] [silent]` - Removes all trait modifiers from a player
  - The functionality of these commands is almost identical to the modifier commands for stat modifiers
  - These commands use the existing auraskills.command.modifier permission
- Add item and armor trait commands for adding trait modifiers to items
  - `/skills item|armor trait add <trait> <value> [lore]` - Adds a trait modifier to the held item, with lore by default
  - `/skills item|armor trait remove <trait>` - Removes a trait modifier from the item held
  - `/skills item|armor trait list` - Lists all trait modifiers on the item held
  - `/skills item|armor trait removeall` - Removes all trait modifiers from the item held
  - Where item|armor is shown above, only item or armor should be specified, not both
  - These commands use the existing auraskills.command.item.modifier or auraskills.command.armor.modifier permissions
  - Like stat modifiers, trait modifiers use PDC and do not depend on lore to function
- Add item ignore commands to ignore interactions on specific items
  - This allows you to disable mana abilities on custom items to allow separate right-click functionality
  - `/skills item ignore add` - Adds the tag that ignores the held item from mana ability interactions
  - `/skills item ignore remove` - Removes the tag that ignores the held item from mana ability interactions
- Add format_title option to menus to allow disabling color and MiniMessage parsing of menu titles
  - To use, manually add it to the options section of a menu file (create it if it doesn't exist)
- Add PlaceholderAPI support to level up chat message
- Add `%auraskills_xp_bar_[skill]%` placeholder that gets the XP progress bar shown in the menus

### Changes
- Optimize menus to significantly improve performance for idle open menus
- Change the default skills and level_progression menu files to fully use block style YAML syntax
- The default contexts materials of the skill template in the level_progression menu now match the materials in the skills menu
- Add symbol placeholder support to item modifier lore message
- Automatic backups will now be skipped if the server has too many users (uses new max_users config option)
- Add ensure_scaling_disabled option to hp trait in stats.yml to disable health scaling if the trait is disabled (true by default)
- Update Japanese, Polish, and Chinese Simplified messages

### Bug Fixes
- Fix Bleed and Absorption particle errors on 1.20.5+
- Fix mana add command always setting mana to max
- Fix hex colors in ability messages not working
- Fix added contexts sections breaking some level_progression templates
- Fix hp action_bar_scaling not applying to menus and chat
- Fix multiplier permissions set to false not being ignored
- Fix Disenchanter not working when shift clicking to remove an item with a different item on the cursor
- Fix hide_attributes flag not working in 1.20.5+
- Fix Alchemist duration lore stacking on the same potion

### API Changes
- Add job methods to SkillsUser
- Add Trait#getMenuDisplay to get the trait value formatted as shown in the stats menu
- Fix MenuManager#buildMenu not working when called multiple times to extend the same menu

## 2.0.9

Note: If you are using the 15 skill legacy preset and previously updated to 2.0.8, your sources files for alchemy, agility, and enchanting may have been forcefully updated to include duplicate sources due to a bug in source updating. While this update fixes the bug going forward, you may have to manually remove sources that are duplicated with healing, endurance, and forging, respectively.

### New Features
- Add menu conditions system
  - Conditions on items are used to add requirements for viewing or clicking an item
  - View conditions will hide the item if not all conditions are met
  - View conditions are added with a `view_conditions` key under an item
  - Click conditions will prevent click actions or built-in click behavior from running if not all conditions are met
  - Click conditions are added using the `on_click_conditions` key, or for a specific button trigger like `on_right_click_conditions`
  - The syntax for any condition key is a map list of conditions that all must be met. This is similar to the click actions syntax.
  - Permission condition type:
    - Permission conditions check if the player has a permission
    - Uses `type: permission` (optional due to auto type detection)
    - Must specify a `permission` key for the permission node to check
    - An optional boolean `value` can be specified (defaults to true)
  - Placeholder condition type:
    - Placeholder conditions compare two values that can contain PlaceholderAPI placeholders
    - Uses `type: placeholder` (optional due to auto type detection)
    - Must specify a `placeholder` string key as the left side value to be compared
    - Must specify a `value` string key as the right side value to be compared
    - An optional `compare` string can be specified for the type of comparison operation to perform (defaults to equals)
      - `equals` checks for numerical or string equality
      - `greater_than` checks if `placeholder` is strictly greater than `value`
      - `greater_than_or_equals` checks if `placeholder` is greater than or equal to `value`
      - `less_than` checks if `placeholder` is strictly less than `value`
      - `less_than_or_equals` checks if `placeholder` is less than or equal to `value`
    - Any `compare` other than `equals` requires both `placeholder` and `value` to be evaluated to doubles
- Add menu `on_open` and `on_close` actions for running actions when a menu is opened or closed
  - These are map lists of actions defined directly in the top-level of the menu file
  - `on_close` will run even if the player switches to a different menu immediately
- Add sound menu action
  - Plays a sound for a player
  - Uses `type: sound` (optional due to auto type detection)
  - Must specify a `sound` string key as the sound type to player. This uses the vanilla names matching the /playsound command.
  - An optional `category` string can be specified (defaults to master)
  - An optional `volume` number can be specified (defaults to 1)
  - An optional `pitch` number can be specified (defaults to 1)
- Add syntax to duplicate default menu items with placeholders
  - Creating a new item with a name in the format `item_name(1)` will replace all the display_name and lore placeholders exactly like the default `item_name`.
  - Any number can be used within the parenthesis
  - This is useful for duplicating default items across multiple slots with different materials and custom_model_data for resource packs
- Add syntax for duplicating the exact same menu item easily across multiple slots
  - Specify a list of slot values with the `pos` key instead of a single value

### Changes
- Click action types will now be automatically detected, so specifying a `type` is no longer required

### Bug Fixes
- Fix duplicate source updating for legacy preset
- Fix command actions not working without PlaceholderAPI

## 2.0.8

### New Features
- Minecraft 1.20.5 and 1.20.6 support
- Add hide_tooltip option to items in menus/loot to completely hide the tooltip
  - Tooltips are hidden automatically for menu fill items
  - Only works on 1.20.5+

### Changes
- Various optimizations for servers with high player counts
  - Optimize BlockLeveler with cache for sources
  - Optimize multiplier permission lookup with cache using LuckPerms events
  - Optimize Treecapitator
- Treecapitator now gives XP for all blocks broken by default
  - This can be disabled in mana_abilities.yml by setting give_xp to false
- Update Korean messages

### Bug Fixes
- Fix hex colors in skill names not working fully in menus
- Fix mana ability is not loaded error
- Fix Grappler ignoring region protection

### API Changes
- Slate is now relocated in the AuraSkills jar, so if you use dev.aurelium.slate packages with the api, you should relocate them to dev.aurelium.auraskills.slate using the Gradle Shadow or Maven Shade plugin
- Add MainConfig#getStartLevel and getHighestMaxLevel
- Add MenuManager#registerGlobalReplacer

## 2.0.7

### New Features
- Add trait placeholders
  - `%auraskills_trait_[trait]%` gets the effective level of a trait
  - `%auraskills_trait_[trait]_bonus%` gets the bonus level of a trait (level excluding the base value)
  - `%auraskills_trait_[trait]_menu%` gets the trait in the same format displayed in the stats menu
- Add mana ability placeholders (thanks Erik)
  - `%auraskills_mability_[ability]%` gets the player's mana ability level
  - `%auraskills_mability_[ability]_value%` gets the mana ability value
  - `%auraskills_mability_[ability]_active%` returns true if mana ability is active, false otherwise
- Add MythicMobs hook to fix issues with damage (thanks Erik)
  - Add takeMana mechanic
    - Syntax: takeMana{m=number}
  - Add giveSkillXP mechanic
    - Syntax: giveSkillXP{xp=number,s=skill}
  - Add hasMana condition for MythicCrucible
    - Syntax: hasMana{m=number}
- Add cause and excluded_cause options to entity source
  - These options can be optionally added to filter DamageCause
- Add directly_to_inventory option to config.yml to make all bonus item drops from luck traits and loot always add directly to the player's inventory instead of being dropped in the world
  - This option does not affect vanilla item drops

### Changes
- Bleed no longer creates invulnerable damage frames
- Add Turkish, Finnish, and Thai messages

### Bug Fixes
- Fix disabled abilities showing in level progression menu items
- Fix hex colors in stat names not working in menus
- Fix the WorldGuard hook not loading blocked_check_replace_regions option
- Fix user data being reset in some cases
- Fix block luck not applying to players in Adventure mode
- Fix CustomSkill not showing defined messages in menus

### API Changes
- Add openMenu methods and registerContext to MenuManager
- Add parseItem and parseMultipleItems to ItemManager, allowing parsing of ItemStack from a ConfigurationNode
- Add passesFilter to ItemManager
- Add loot API for accessing existing loot tables and registering new loot types
  - New loot tables can be loaded by using NamespacedRegistry#setLootDirectory
- Add SourceManager API for getting sources of a type
- Remove the extra repositories that were required to load the dependency in 2.0.6

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