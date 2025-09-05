---
description: Guide to the config.yml file
---

# Main Config

The `config.yml` is the main plugin configuration file found in the `plugins/AuraSkills` folder. It is used for general or miscellaneous config options related to storage/database, external plugin hooks, languages, action bar, boss bar, worlds/regions, modifiers, requirements, and more.

Options for each [skill](../skills/) and stat were formerly here, but have been moved to their own `skills.yml` and `stats.yml` files.

If an option you see in the config is missing, this page may not have been updated yet or the option may have been removed. You can find any config additions and changes in the full plugin [changelog](https://github.com/Archy-X/AureliumSkills/blob/master/Changelog.txt).

Last Updated Version: `2.3.6`

## Options

### SQL

`sql:`

* `enabled` - Whether SQL should be used for data storage (requires a restart to enable).
* `type` - The type of SQL database to use; currently only `mysql` is supported.
* `host` - SQL hostname
* `port`- Port (must be number)
* `database` - Database name (must be created already)
* `username` - SQL username
* `password` - SQL password
* `load_delay` - Number of ticks to delay loading data after a player joins, useful for syncing multiple servers to a single database.
* `always_load_on_join` - If true, player data will always be loaded from the database when a player joins, regardless if it is already in memory.
* `ssl` - Whether to use SSL.
* `maximum_pool_size`, `minimum_idle`, `connection_timeout`, `max_lifetime`, `keepalive_time` - Options used to configure the Hikari connection pool. These should not be changed unless you have issues with connection stability and know what you are doing.

### Languages

`default_language` - The default language for players; must have a file that matches (ex: `messages_en.yml` for `en`)

`try_detect_client_language` - If set to `true`, the plugin will try to use the client's language, if available and valid. This is only for players who have not set a language using commands, or if their language was reset after a server restart. If the client language is not a valid plugin language, it will use the `default-language`. If set to `false`, all unset players will use the `default-language`.

`languages` - A list of languages players can switch to using `/skills lang <language>`; must also have a file that matches. Custom language files are defined here.

### Hooks

`hooks:`

* `enabled` - Whether the hook to the given plugin should be registered. Hooks will only attempt to be loaded if the given plugin is detected and enabled, so hooks can be set to `true` without having the plugin on the server. Some hooks, like HolographicDisplays and DecentHolograms perform the same function, so one must be disabled before the other can be enabled.

Specific options for each hook are under the section with the plugin name below:

#### LuckPerms

* `LuckPerms:`
  * `use_permission_cache` - Whether to enable the system that caches LuckPerms multiplier permissions for optimizing multiplier calculations. If you change multiplier permissions using certain features that don't trigger LuckPerms events, such as world contexts, this feature may have to be set to false.

#### WorldGuard

* `WorldGuard:`
  * `blocked_regions` - Players in regions on this list will not be able to gain XP naturally in any skill.
  * `blocked_check_replace_regions` - Regions on this list will disable block sources checking if the block broken has been player placed.

### Action bar

`action_bar:`

* `enabled` - Whether the action bar should be enabled/disabled. (Must be set to `true` to have any action bars; setting to `false` disables all action bar types).
* `idle` - Controls the idle action bar (not gaining xp). **Set this to false if you don't want health and mana at the bottom of the screen.**
* `ability` - Controls the action bar for ability messages (raise/lower, activate, etc.). If set the false, the ability messages will be sent through chat instead.
* `xp` - Controls the action bar for gaining xp (not maxed)
* `maxed` - Controls the action bar when xp is gained in a maxed skill.
* `update_period` - How often the action bar should update, in ticks (Increase this value if action bar is causing lag).
* `round_xp` - If enabled, current xp will be rounded to an integer.
* `placeholder_api` - Whether PlaceholderAPI placeholders should be replaced in the action bar, given that you have PlaceholderAPI.
* `use_suffix` - Whether to format the current player's XP with number suffixes (k, m, etc). Only applies if `xp` is set to true.
* `format_last` - If true, parsing of MiniMessage will happen after placeholders (like hp and mana) are replaced on each send. This makes MiniMessage gradients work at the cost of performance.
* `update_async` - If true, the idle action bar will be updated and sent asynchronously. This is an experimental option.

### Boss bar

`boss_bar:`

* `enabled` - Whether boss bars should be enabled for xp gains.
* `mode` - Can be either `single` or `multi`. `multi` means multiple boss bars will display if gaining XP from different types of skills, `single` is only one at a time.
* `stay_time` - How long the boss bar should stay up after not gaining xp, in ticks.
* `update_every` - Controls how often the boss bar should update when gaining xp consecutively, increase if having lag issues.
* `round_xp` - If enabled, the current xp will be rounded to an integer.
* `use_suffix` - Whether to format the current player's XP with number suffixes (k, m, etc).
* `format` - The format list allows you to change the boss bar color and style for each skill:
  * Format: '\[SKILL] \[COLOR] \[STYLE]'
  * Available colors are BLUE, GREEN, PINK, PURPLE, WHITE, RED, and YELLOW
  * Available styles are PROGRESS, NOTCHED\_6, NOTCHED\_10, NOTCHED\_12, and NOTCHED\_20
* `animate_progress` - Toggles boss bar animation (the delay when updating progress).

### Jobs

`jobs:`

* `enabled` - Whether the jobs system is enabled. Even if selection is disabled, this option must be enabled in order for source income to work. The Vault plugin and an economy plugin must be installed in order for income to be given.
* `selection:`
  * `require_selection` - Whether players need to select skills as jobs through the level progression menu in order to earn income while gaining XP. If false, all skills will give income with XP.
  * &#x20;`default_job_limit` - The default maximum number of jobs a player can have active at once. This limit can be changed per-player using the auraskills.jobs.limit.\[number] permission node.
  * `disable_unselected_xp` - If true, players will be blocked from gaining XP in all skills besides the skills that are active jobs.
  * `cooldown_sec` - The number of seconds players have to wait to select a new job after selecting one.
* `income:`
  * `use_xp` - Whether the `default.income_per_xp` should be used as the default income value for sources.
  * `use_expression` - Whether the `default.expression` should be used as the default income value for sources. This overrides `use_xp`.
  * `default:`
    * `income_per_xp` - The multiplier for the source XP that determines how much income is given for a source by default. For example, gaining 14 skill XP from a source with an `income_per_xp` of 0.1 will give 1.4 Vault currency to the player.
    * `expression` - An expression used to calculate the income given for a source by default if `use_expression` is true. The available variables include xp, base\_xp (value without multipliers), level (skill level), power, and skill\_average.
  * `batching:`
    * `enabled` - If true, accumulated income will be given in an interval instead of immediately. This can reduce lag from a large amount of calls to Vault economy for large servers. The effective rate of income gain from sources does not change.
    * `interval_ms` - The minimum delay between income gains when batching is enabled is defined by the interval\_ms parameter. For example, with an interval\_ms of 2000, each time XP is gained, the system checks the timestamp of the last batched income gain. If more than 2 seconds have passed since the last gain, the income is given immediately. If less than 2 seconds have passed, the income is added to the next batch and will be distributed when XP is gained 2 seconds after the last batch time.
    * `display_individual` - If true, the boss bar will display the original individual income instead of the batched income. This means if this is false and batching is enabled, some boss bars will not display any income gain, and the boss bar for a batch execution will display the accumulated amount.
  * `use_final_xp` - If set to false, the calculation for income\_per\_xp will exclude all XP multipliers.

::: info
Jobs income can also be configured per-source in the [sources configuration](../sources.md#global-options).
:::

`enable_roman_numerals` - Whether Roman numerals should be used for skill levels.

### Anti-AFK

`anti_afk:`

* `enabled` - Whether the anti-AFK system is enabled. If false, all aspects of the system are disabled.
* `logging_enabled` - Whether logging for failing anti-AFK checks is enabled. Logs will be sent to any online player with the auraskills.antiafk.notify permission (op by default).
* `log_threshold` - An expression determining the count of identical conditions required for logging an event. The min\_count variable can be used in this expression to reference the min\_count in the checks section for a check type.
* `checks:`
  * `[check_name]:`
    * `enabled` - Whether this individual check type is enabled.
    * `min_count` - The minimum number of failed conditions in a row to start blocking XP gain. The counter increments for every identical condition (position, yaw, pitch, or identity) and resets when a differing condition is detected.
    * `max_distance` - The maximum distance moved to still be counted as a check failure for position/coordinates based checks.

### Damage holograms

`damage_holograms:`

* `enabled` - Enable/disable damage holograms (requires either HolographicDisplays or DecentHolograms hook to be enabled and that plugin to be on the server).
* `scaling` - Whether the damage displayed on holograms should be scaled according to the `action_bar_scaling` option of the hp trait in `stats.yml`.
* `decimal:`
  * `display_when_less_than:` - Display decimals in damage holograms when less than a specified damage.
  * `max_amount` - The maximum amount of decimal digits to display.
* `offset:`
  * `x` - X coordinate offset
  * `y` - Y coordinate offset
  * z - Z coordinate offset
  * `random`:
    * `enabled` - Whether random hologram positions should be enabled.
    * `x_min` - Minimum X coordinate offset
    * `x_max` - Maximum X coordinate offset
    * `y_min` - Minimum Y coordinate offset
    * `y_max` - Maximum Y coordinate offset
    * `z_min` - Minimum Z coordinate offset
    * `z_max` - Maximum Z coordinate offset

### Leaderboards

`leaderboards:`

* `update_period` - How often leaderboards should be updated, in ticks.
* `update_delay` - How long after server startup should the leaderboards be updated, in ticks (does not include the immediate update on startup).

`start_level` - The skill level that players start at. Defaults to 0, use 1 to revert to Beta mechanics.

`enable_skill_commands` - Whether skill name commands should be enabled such as `/farming` or `/mining` (Requires restart to have an effect).

`check_block_replace:`

* `enabled` - Whether blocks placed by players should not give xp; keep `true` unless you are having plugin compatibility issues.
* `blocked_worlds` - A list of worlds that should not be checked for block replacement. Checking will be disabled in these worlds regardless of what `enabled` is set to.

### Worlds and regions

`blocked_worlds` - Players in worlds on this list will not be able to gain xp naturally in any skill.

`disabled_worlds` - Most of the plugin's gameplay functionality will be disabled in worlds on this list, including but not limited to stats, abilities, gaining xp, and the action bar (commands and menus will still be available).

`disable_in_creative_mode` - Whether players should not be able to gain xp while in creative mode.

### Data validation

`data_validation:`

* `correct_over_max_level` - If true, the plugin will prevent skill levels being over max level on join.

### Death options

`on_death:`

* `reset_skills` - Whether to reset a player's skill levels when they die.
* `reset_xp` - Whether to reset a player's XP in their current skill levels when they die. Skill levels are not changed.
* `reset_xp_ratio` - The ratio of the player's current XP that should remain after death if `reset_xp` is enabled. A value of `0.4` will removed 60% of the player's current XP in each skill, so 40% of their XP will remain.

### Auto-save

`auto_save:`

* `enabled` - Whether data for online players should save periodically instead of just when they log out. This is useful if you experience skill data losses due to server crashes.
* `interval_ticks` - How often (in ticks) to auto-save.

### Leveler

`leveler:`

* `title:`
  * `enabled` - Whether a title should be displayed to players on skill level up.
  * `fade_in` - Title fade in time, in ticks
  * `stay` - How long the title should last, in ticks
  * `fade_out` - Title fade out time, in ticks
* `sound:`
  * `enabled` - Whether a sound should be played to players on skill level up.
  * `type` - The name of the sound that should be played (must be a valid sound name).
  * `category` - The sound category the sound should be played in.
  * `volume` - Sound volume
  * `pitch` - Sound pitch
* `double_check_delay` - The level up check delay for large xp gains at once, in ticks (lower is faster).

### Mana

`mana:`

* `enabled` - If false, mana abilities will not cost mana to use and mana displays will be hidden from the action bar and menus.
* `cooldown_timer_period` - The number of ticks between counting down mana ability cooldowns. Increasing can help reduce lag caused by TimerCooldown for mana abilities. The overall cooldown time remains the same. Requires a restart to take effect.
* `stat_info:`
  * `enabled` - Whether the stat info menu that shows modifiers is enabled, which is accessed by clicking a stat button in the /stats menu.

### Modifier

`modifier:`

* `armor:`
  * `equip_blocked_materials` - A list of blocks that should not grant stats of armor when right-clicked; add to this list when stats are given but armor is not equipped.
* `item:`
  * `check_period` - How often, in ticks, the item held in a player's hand should be checked for stat item modifiers (increase if you have lag)
  * `enable_off_hand` - Whether stat modifiers should work in the off hand
* `auto_convert_from_legacy` - Whether the old modifier nbt format should be converted to the new one. Set to true if you have items from Beta with modifiers that no longer work.

### Requirement

`requirement:`

* `enabled` - Whether requirements should be checked at all. If you do not use requirements, disabling will improve performance.
* `item:`
  * `prevent_tool_use` - Whether block breaking should be blocked when a player does not meet a requirement
  * `prevent_weapon_use` - Whether attacking entities should be blocked when a player does not meet a requirement
  * `prevent_block_place` - Whether block placing should be blocked when a player does not meet a requirement
  * `prevent_interact` - Whether interacting (right clicking) should be blocked when a player does not meet a requirement
  * `global` - Define item requirements that should apply to every item of that type. Format: - '\[material] \[skill\_1]:\[level\_1] \[skill\_2]:\[level\_2] ...'
* `armor:`
  * `prevent_armor_equip` - Whether armor should be unable to be equipped when a player does not meet a requirement
  * `global` - Define armor requirements that should apply to every item of that type. Format: - '\[material] \[skill\_1]:\[level\_1] \[skill\_2]:\[level\_2] ...'
* `override_global` - If true, global requirements will be ignored if item-specific requirements are defined.

### Critical

`critical:`

* `base_multiplier` - The base damage multiplier for critical hits
* `enabled` - Options in this category control whether that item type should be able to deal critical hits. (`hand` is for empty fist, `other` is for holding any other item not on the list)

### Source

`grindstone:`

* `blocked_enchants` - A list of enchantments that should be blocked from counting XP for grindstone sources. Add unremovable enchants/curses from custom enchant plugins here.

`statistic:`

* `gain_period_ticks` - How often statistic sources should give XP. This does not change the effective rate of XP gain, just the time between statistic checks.

`entity:`

* `give_alchemy_on_potion_combat` - Whether killing/damaging an entity using potions should be counted as Alchemy XP instead of Fighting/Archery.

### Menus

`menus:`

* `lore_wrapping_width` - The number of characters per line before newlines are automatically inserting in menu lore lines where wrapping is enabled.
* `placeholder_api` - Whether PlaceholderAPI placeholders should be used in menus.
* `stats:`
  * `show_trait_values_directly` - If true, stats that have a single trait with a `modifier` of exactly 1 will show the value of the trait instead in menus. This allows trait base values, such as the 20 HP every player has by default, to be included in the shown level.
* `removal_protection` - Enables additional protections against removing items from menus.

### Loot

`loot:`

* `update_loot_tables` - Whether new loot items introduced in default configs should be automatically added when updating the plugin.
* `directly_to_inventory` - If true, all bonus item drops from luck traits and loot will always add directly to the player's inventory instead of being dropped in the world. This option does not affect vanilla item drops.

`check_for_updates` - Whether the plugin should check for new updates on startup and when a player with the `auraskills.checkupdates` permission joins

### Automatic backups

`automatic_backups:`

* `enabled` - Whether automatic backups should be taken on server shutdown
* `minimum_interval_hours` - The minimum interval, in hours, between automatic backups. Automatic backups will only be taken at least this amount of hours after the last one.
* `max_users` - If the server has over this many total users, automatic backups will be skipped.

`save_blank_profiles` - If false, player data of players who have not leveled any skills or gained any XP will not be saved into storage.

### Experimental options

Experimental options are not included in the config by default. Each option you want to enable must be manually added in an `experimental` section. These options
are usually features that are intended to be enabled by default eventually, but may be unstable or require additional testing.

`experimental:`

* `optimize_leaderboard_updating` (boolean) - Whether to enable optimized fetching of user data from the SQL database for leaderboard updates. When enabled, only
  users updated in the database since the last leaderboard update will be fetched after the initial load.
