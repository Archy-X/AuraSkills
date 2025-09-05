---
description: List of PlaceholderAPI placeholders
---

# Placeholders

AuraSkills provides PlaceholderAPI placeholders that work out of the box, without needing to download an ecloud expansion. Due to the plugin renaming in 2.0, existing placeholders prefixed with `%aureliumskills_` will still work alongside `%auraskills_` placeholders. However, you should slowly migrate your placeholders to use `%auraskills_`, since the old prefix may be removed in later versions.

## List

* `%auraskills_power%` - Combined skill level
* `%auraskills_[skill]%` - Level for a certain skill (ex: %aureliumskills\_farming%)
* `%auraskills_[skill]_roman%` - Level for a certain skill as a roman numeral
* `%auraskills_[stat]%` - Level for a certain stat (ex: %aureliumskills\_health%)
* `%auraskills_[stat]_int%` - Level for a certain stat rounded to an integer
* `%auraskills_average%` - Average skill level
* `%auraskills_average_int%` - Average skill level rounded to an integer
* `%auraskills_average_1%` - Average skill level rounded to one decimal
* `%auraskills_hp%` - Hp as an integer
* `%auraskills_hp_1%` - Hp rounded to 1 decimal
* `%auraskills_hp_2%` - Hp rounded to 2 decimals
* `%auraskills_hp_max%` - Max hp
* `%auraskills_hp_percent%` - Percent hp as an integer
* `%auraskills_mana%` - Current player mana
* `%auraskills_mana_int%` - Mana of a player rounded to an integer &#x20;
* `%auraskills_mana_max%` - Max mana
* `%auraskills_mana_max_int%` - Max mana rounded to an integer
* `%auraskills_lb_power_[place]%` - Gets a formatted value of username and level for the power leaderboard at a certain place
* `%auraskills_lb_power_[place]_name%` - Gets the username for the power leaderboard at a certain place
* `%auraskills_lb_power_[place]_value%` - Gets the level for the power leaderboard at a certain place
* `%auraskills_lb_[skill]_[place]%` - Gets a formatted value of username and level for a skill leaderboard at a certain place
* `%auraskills_lb_[skill]_[place]_name%` - Gets the username for a skill leaderboard at a certain place
* `%auraskills_lb_[skill]_[place]_value%` - Gets the level for a skill leaderboard at a certain place
* `%auraskills_rank%` - Gets the power leaderboard rank
* `%auraskills_rank_[skill]%` - Gets a skill leaderboard rank
* `%auraskills_xp_required_formatted_[skill]%` - Gets the required XP for the next level of a player formatted with suffixes (ex: 5k)
* `%auraskills_xp_required_[skill]%` - Gets the required XP for the next level of a player as an exact number
* `%auraskills_xp_progress_int_[skill]%` - Gets the percent progress to the next level rounded to an integer
* `%auraskills_xp_progress_1_[skill]%` - Gets the percent progress to the next level rounded to 1 decimal place
* `%auraskills_xp_progress_[skill]%` - Gets the exact percent progress to the next level
* `%auraskills_xp_int_[skill]%` - Gets the current XP for a player rounded to an integer
* `%auraskills_xp_formatted_[skill]%` - Gets the current XP for a player formatted with suffixes
* `%auraskills_xp_[skill]%` - Gets the exact skill XP for a player
* `%auraskills_multiplier%` - Gets a player's permission XP multiplier rounded up to 2 decimal places (1 = normal, 2 = 2x XP)
* `%auraskills_multiplier_[skill]%` - Gets a player's permission XP multiplier for a specific skill rounded up to 2 decimal places (1 = normal, 2 = 2x XP)
* `%auraskills_multiplier_percent%` - Gets a player's permission XP multiplier as a percent more value rounded to an integer (40 = 40% more XP)
* `%auraskills_multiplier_percent_[skill]%` - Gets a player's permission XP multiplier for a specific skill as a percent more value rounded to an integer (40 = 40% more XP)
* `%auraskills_actionbar_status%` - Gets whether the idle action bar is enabled for a player. Returns true or false.
* `%auraskills_[ability]%` - Gets an ability level. Replace `[ability]` with the default English name of an ability in lowercase.
* `%auraskills_[ability]_roman%` - Gets ability level as a Roman numeral.
* `%auraskills_[ability]_value%` - Gets an ability value.
* `%auraskills_[ability]_value_int%` - Gets ability value rounded to an integer.
* `%auraskills_[ability]_value_2%` - Gets the secondary value of an ability if it exists.
* `%auraskills_[ability]_value_2_int%` - Gets secondary ability value rounded to an integer if it exists.
* `%auraskills_mability_[ability]%` - Gets the player's mana ability level.
* `%auraskills_mability_[ability]_roman%` - Gets mana ability level as a Roman numeral.
* `%auraskills_mability_[ability]_value%` - Gets the mana ability value.
* `%auraskills_mability_[ability]_value_int%` - Gets mana ability value rounded to an integer
* `%auraskills_mability_[ability]_active%` - Returns true if mana ability is active, false otherwise.
* `%auraskills_trait_[trait]%` - Gets the effective level of a trait.
* `%auraskills_trait_[trait]_bonus%` - Gets the bonus level of a trait (level excluding the base value).
* `%auraskills_trait_[trait]_menu%` - Gets the trait in the same format displayed in the stats menu.
* `%auraskills_jobs_list%` - Lists active jobs in a comma separated list of default skill names in all lowercase.
* `%auraskills_jobs_list_formatted%` - Lists active jobs in a comma separated list with the skill display name of the config default\_language.
* `%auraskills_jobs_count%` - Gets the number of jobs the player currently has active.
* `%auraskills_jobs_limit%` - Gets the maximum number of jobs the player is allowed to have active at the same time.
* `%auraskills_jobs_active_[skill]%` - Returns true/false whether the job is active (ex: %auraskills\_jobs\_active\_farming%)
