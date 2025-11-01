---
description: Guide to abilities and the abilities.yml file
---

# Abilities

> Not to be confused with [Mana Abilities](mana-abilities.md).

**Abilities** are passive gameplay buffs unlocked and leveled up alongside leveling skills. By default, each skill has 5 abilities that unlock at skill levels 1-5 (or 2-6 if `start_level` in config.yml is 1) and level up every 5 skill levels. Abilities are not fixed to their default skill; swapping abilities to other skills or disabling an ability can be easily done by removing it from the `abilities` list in the skill's section of `skills.yml`.

## Configuration

All ability-related configuration other than linking with skills is done in the `abilities.yml` file. Each ability contains many common options, and some have options specific to the ability.

### Common options

* `enabled` - Whether the ability should be enabled. Disabled abilities will have no effect and be hidden from menus. This does the same thing as removing it from the `abilities` list in `skills.yml`, though this `enabled` option is more suited for temporarily disabling an ability.
* `base_value` - The value this ability has at level 1 (when it is just unlocked). An ability's value determines it's power/effect and is directly shown wherever the ability is described in the menus. An ability that has a certain chance to activate usually has the chance controlled directly by the value in percent. For example, if Bountiful Harvest has a `base_value` of 5.0, it has a 5% chance to activate at level 1.
* `value_per_level` - A number that the value of the ability is increased by for each level of the ability past 1. The formula for effective value is `value = base_value + (value_per_level * (level - 1))`, where `level` is the level of the ability, not the skill. See the `level_up` option below for how the ability level is calculated.
* `unlock` - The skill level the ability is unlocked at. This can either be a fixed integer, or a string expression using the placeholder `{start}`, which is value of the `start_level` option in `config.yml`. The default configs use this method to have the first ability always unlock when you first level up a skill up to the fifth unlock.
* `level_up` - The interval in skill levels between when the ability levels up. The formula for a user's ability level is `level = (skill_level - unlock)/level_up + 1`, not including any cap by `max_level`.
* `max_level` - The maximum level the ability can be (not the skill level). Setting the value to `0` indicates no maximum level, making the max ability level determined by the max skill level and the above formula.
* `secondary_base_value` - Some abilities have a secondary base value to control another variable. Works the same as `base_value`.
* `secondary_value_per_level` - Some abilities have a secondary value per level to control another variable. Works the same as `value_per_level`.

### Ability-specific options

The following table of options only apply to one or a few abilities.

<table><thead><tr><th width="242">Option key</th><th width="181">Abilities applicable</th><th>Description</th></tr></thead><tbody><tr><td>scale_base_value_chance</td><td>treasure_hunter, epic_catch, metal_detector, lucky_spades</td><td>If true, the value of the ability will scale the loot table base_chance instead of adding. The chance is multiplied by <code>1 + value/100</code>.</td></tr><tr><td>enable_message</td><td>first_strike, revival</td><td>Whether the player should be sent an action bar message when the ability activates.</td></tr><tr><td>cooldown_ticks</td><td>first_strike</td><td>The number of ticks required for First Strike to refresh. Defaults to 6000 (5 minutes).</td></tr><tr><td>enable_enemy_message</td><td>bleed</td><td>Whether to send an action bar message when a player makes an enemy bleed.</td></tr><tr><td>enable_self_message</td><td>bleed</td><td>Whether to send an action bar message when yourself is bleeding.</td></tr><tr><td>enable_stop_message</td><td>bleed</td><td>Whether to send an action bar message when your own bleeding stops.</td></tr><tr><td>base_ticks</td><td>bleed</td><td>The number of bleed ticks to add when the player is not already bleeding.</td></tr><tr><td>added_ticks</td><td>bleed</td><td>The number of bleed ticks to add when the player is already bleeding.</td></tr><tr><td>tick_period</td><td>bleed</td><td>The number of Minecraft ticks between each time a bleed tick applies.</td></tr><tr><td>show_particles</td><td>bleed</td><td>Whether to spawn particles when bleeding.</td></tr><tr><td>health_percent_required</td><td>fleeting</td><td>The maximum percent health a player can be for Fleeting to apply.</td></tr><tr><td>add_item_lore</td><td>alchemist</td><td>Whether to add lore to potions brewed to display the duration bonus.</td></tr><tr><td>speed_reduction</td><td>stun</td><td>The speed reduction factor when stunned. 0.2 = 20% reduction in speed.</td></tr></tbody></table>

## Menu

The abilities menu displays all abilities and mana abilities of a skill, with their descriptions and levels. It can be accessed by clicking the ability item in the level progression menu (light blue dye by default).

Ability descriptions are also shown on each skill level pane as rewards in the level progression menu. A shortened version of the description known as the info message is shown in the skills menu on each skill item.

## Messages

The name, description, and info messages of abilties are configured in the `abilities` section of the messages file.
