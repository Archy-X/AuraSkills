---
description: Guide to mana abilities and the mana_abilities.yml file
---

# Mana Abilities

> Not to be confused with passive [abilities](abilities.md).

**Mana abilities** are active abilities that consume mana when activated. By default, mana abilities unlock at skill
level 6 and level up every 6 skill levels. Each skill can have at most one mana ability. Mana abilities can be swapped
between skills by changing the `mana_ability` option in `skills.yml`.

## List

<table data-full-width="false"><thead><tr><th width="168">Name</th><th width="116">Skill</th><th>Description</th></tr></thead><tbody><tr><td>Replenish</td><td>Farming</td><td>Replants crops automatically for a certain duration. Right click with a hoe and break a crop to activate. Works with wheat, carrots, potatoes, nether wart, and beetroot.</td></tr><tr><td>Treecapitator</td><td>Foraging</td><td>Breaks entire trees instantly for a certain duration. Right-click with an axe and break a log to activate. The algorithm is not final and will be improved later on to work perfectly with all tree types.</td></tr><tr><td>Speed Mine</td><td>Mining</td><td>Gives Haste 10 for a certain duration. Right-click with a pickaxe and break stone or an ore to activate.</td></tr><tr><td>Sharp Hook</td><td>Fishing</td><td>Deal damage to a hooked entity when left-clicking with a fishing rod.</td></tr><tr><td>Terraform</td><td>Excavation</td><td>When digging, break connected blocks instantly in a 4 block radius horizontally for a duration. Right click shovel and dig block to activate.</td></tr><tr><td>Charged Shot</td><td>Archery</td><td>Arrows you shoot will deal more damage based on how far the bow was pulled back, consuming mana in the process. Does more damage per mana consumed. Left click a bow to toggle charged shot mode.</td></tr><tr><td>Absorption</td><td>Defense</td><td>Incoming damage will decrease mana by 2x Minecraft damage instead of your health. Mana will not regenerate while Absorption is active. Left click shield and take damage to activate.</td></tr><tr><td>Lightning Blade</td><td>Fighting</td><td>Increases attack speed by a percent for a duration. Right click sword and attack entity to activate.</td></tr></tbody></table>

## Configuration

Configuring mana abilities is done in `mana_abilities.yml`. The format is similar to the normal ability configuration,
but with some different options.

### Common options

* `enabled` - Whether the mana ability should be enabled. Disabled mana abilities will have no effect and be hidden from
  menus. This does the same thing as removing it from `mana_ability` option in `skills.yml`, though this `enabled`
  option is more suited for temporarily disabling a mana ability.
* `base_value` - The value this mana ability has at level 1 (when it is just unlocked). The value determines the
  duration for most mana abilities, except Sharp Hook (damage dealt), Charged Shot (damage per mana), and Lightning
  Blade (attack speed increase).
* `value_per_level` - A number that the value of the mana ability is increased by for each level of the ability past 1.
  The formula for effective value is `value = base_value + (value_per_level * (level - 1))`, where `level` is the level
  of the ability, not the skill. See the `level_up` option below for how the ability level is calculated.
* `base_cooldown` - The cooldown of this mana ability at level 1 in seconds. The cooldown starts counting down after the
  mana ability wears off.
* `cooldown_per_level` - The change in the cooldown per mana ability level. By default the this is negative so the
  cooldown decreases as you level up. If you are increasing the max skill level or mana ability level, be careful to
  make sure this doesn't make the cooldown negative.
* `base_mana_cost` - The cost in mana to activate the mana ability at level 1.&#x20;
* `mana_cost_per_level` - The change in mana cost per mana ability level. Player's won't be able to use the mana ability
  if the cost is higher than their max mana (increased by the Wisdom stat).
* `unlock` - The skill level the ability is unlocked at. This can either be a fixed integer, or a string expression
  using the placeholder `{start}`, which is value of the `start_level` option in `config.yml`. The default configs use
  this method to have the first ability always unlock when you first level up a skill up to the fifth unlock.
* `level_up` - The interval in skill levels between when the ability levels up. The formula for a user's ability level
  is `level = (skill_level - unlock)/level_up + 1`, not including any cap by `max_level`.
* `max_level` - The maximum level the ability can be (not the skill level). Setting the value to `0` indicates no
  maximum level, making the max ability level determined by the max skill level and the above formula.

### Ability-specific options

<table><thead><tr><th width="218">Option key</th><th>Mana abilities applicable</th><th>Description</th></tr></thead><tbody><tr><td>require_sneak</td><td>Replenish, Treecapitator, Speed Mine, Terraform, Lightning Blade</td><td>If true, players must right click and sneak at the same time to ready the mana ability.</td></tr><tr><td>check_offhand</td><td>Replenish, Treecapitator, Speed Mine, Terraform, Lightning Blade</td><td>If true, right clicking to place a block from the offhand will not ready the mana ability of the tool in the main hand.</td></tr><tr><td>sneak_offhand_bypass</td><td>Replenish, Treecapitator, Speed Mine, Terraform, Lightning Blade</td><td>If true, sneaking while right clicking with a block in the offhand will ready the mana ability of the tool in the main hand.</td></tr><tr><td>replant_delay</td><td>Replenish</td><td>The delay in ticks between when a crop is broken and when Replenish replants the seed.</td></tr><tr><td>show_particles</td><td>Replenish</td><td>Whether to display particles when replanting.</td></tr><tr><td>prevent_unripe_break</td><td>Replenish</td><td>Whether attempting to break an non-fully-grown crop with a hoe while Replenish is active should be prevented.</td></tr><tr><td>max_blocks_multiplier</td><td>Treecapitator</td><td>A multiplier on the maximum number of blocks that can be broken at once by Treecapitator. The base number of blocks depends on the wood type.</td></tr><tr><td>give_xp</td><td>Treecapitator</td><td>Whether to give skill XP for every block broken by Treecapitator.</td></tr><tr><td>haste_level</td><td>Speed Mine</td><td>The level of the haste effect that should be given by Speed Mine. Counts from 1 for the lowest possible level.</td></tr><tr><td>display_damage_with_scaling</td><td>Sharp Hook</td><td>Whether the damage number shown in menus should be multiplied by the action_bar_scaling option of the hp trait in stats.yml.</td></tr><tr><td>enable_sound</td><td>Sharp Hook, Charged Shot</td><td>Whether a sound should be played when the player uses the mana ability.</td></tr><tr><td>disable_health_check</td><td>Sharp Hook</td><td>If true, Sharp Hook will still apply even if the plugin thinks it will do 0 damage. Only enable this if you have compatibility issues.</td></tr><tr><td>always_enabled</td><td>Charged Shot</td><td>If true, all arrows fired will be charged shots if the player has the mana ability unlocked. This disables the left click toggling functionality.</td></tr><tr><td>max_blocks</td><td>Terraform</td><td>The maximum number of blocks that can be broken at once by Terraform.</td></tr><tr><td>enable_particles</td><td>Absorption</td><td>Whether particles should be shown when a player absorbs a hit.</td></tr></tbody></table>

## Messages

The names and descriptions of mana abilities can be edited in the `mana_abilities` section of the messages file.

## Disabling mana

While mana is a core component of the mana abilities system, the mana system can be essentially disabled through a few
steps:

1. Set the `mana.enabled` option in `config.yml` to false. This removes mana costs from mana abilities and most mentions
   of mana in menus. While most mana abilties will still work just without a mana cost, the Absorption and Charged Shot
   mana abilities will be disabled because their mechanics fully depend on mana.
2. In the `messages/global.yml` file, edit the `action_bar.idle` message by removing the <code v-pre>{{action_bar.mana_display}}</code>
   part and extra spaces. Since the idle action would just display health, you probably want to disable it completely by
   setting `action_bar.idle` to false in `config.yml`.
3. Up to this step, mana has already been functionally disabled. But to fully remove mentions of mana in menus, replace
   mentions of the word "Mana" in the messages file of your language to something else, like "Active Ability" for
   example.
