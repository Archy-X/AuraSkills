---
description: Guide to stats and the stats.yml file
---

# Stats

**Stats** are values that represent specific gameplay buffs called **traits.** A player's stat levels and their descriptions can be viewed in the stats menu using `/stats` or by clicking the stat item (their player head by default) in the main skills menu.

Stats are gained alongside leveling skills; each skill has a primary stat that increases for every skill level and a secondary stat that increases every other skill level. These are configured through [stat rewards](../rewards.md#stat-rewards-stat). Stats can also be increased through modifiers created with commands or on items.

**Traits** are values for the singular gameplay aspect that a stat affects. Since some stats have multiple effects, such as Wisdom's experience bonus and max mana increase, traits allow these effects to be configured independently as well as swapped between stats. Traits increase in value based on a constant multiplier of the stat level, which allows stat levels to display in a more user-friendly level while the trait is decimal that directly shows its effect.

There are 9 stats by default, with the trait(s) they level up also shown:

* Strength -> Attack Damage
* Health -> Hp
* Regeneration -> Saturated Regen, Hunger Regen, Mana Regen
* Luck -> Farming Luck, Foraging Luck, Mining Luck, Excavation Luck, Fishing Luck
* Wisdom -> Experience Bonus, Anvil Discount, Max Mana
* Toughness -> Damage Reduction
* Crit Chance -> Crit Chance
* Crit Damage -> Crit Damage
* Speed -> Movement Speed

## Configuration

Stats and traits are configured in the `stats.yml` file. Each stat has a section named after its full Namespaced ID (prefixed with `auraskills/` for all default stats) in the `stats` section. Each trait has a similar section under `traits`. Below is an example stat section for Regeneration with comments explaining each option:

```yaml
auraskills/regeneration:
  # Whether the stat is enabled. Disabled stats will have no effect and
  # will be hidden from menus.
  enabled: true
  # A map of traits that this stat controls.
  traits:
    auraskills/saturation_regen: # The trait for health regen at full hunger
      # The increase in the trait per stat level. In this case, the player 
      # regenerates 0.008 more HP per Regeneration stat level.
      modifier: 0.008 
    auraskills/hunger_regen:
      modifier: 0.008
    auraskills/mana_regen:
      modifier: 0.01
```

### Trait options

The traits section contains configurable options specific to each trait. These options were formerly in their stats' section before 2.0. If the `enabled` option (common to all traits) is set to false, the trait will have no effect in game and will be hidden from menus.

#### Attack damage

* `hand_damage` - Whether damage from hitting with an empty hand should be affected.
* `bow_damage` - Whether damage from bows and projectiles should be affected.
* `display_damage_with_health_scaling` - If true, the damage numbers in the menus and damage holograms will be scaled based on the `action_bar_scaling` option in the `auraskills/hp` trait section.
* `use_percent` - Whether the attack damage bonus should be applied as a percent increase of the base damage. If false, damage will be added as the flat trait value.

#### Hp

* `health_scaling` - Whether the player's displayed hearts should be scaled, meaning 2 HP = 1 heart does not apply at higher HP levels. This means it requires increasing amounts of HP to get an additional visual heart, which prevents hearts blocking too much of the screen at high HP values. This options is entirely visual and does not affect the real amount of HP of the player, which is what is shown on the action bar. The scaling amounts are configured by the `hearts` option.
* `hearts` - A map of entries that determine when extra visual hearts are shown if `health_scaling` is enabled. The key is the index of the heart and the value is the real HP number needed for that heart to be shown. For example, `'17': 48` means that the 17th heart (7th extra heart) will be shown when the player has at least 48 HP (number on the action bar).
* `action_bar_scaling` - A number that is multiplied by the player's Minecraft HP to determine the HP number shown on the action bar (and damage numbers if `display_damage_with_health_scaling` in strength in enabled).
* `update_delay` - The number of ticks to delay recalculating and applying the player's health on join or world change. Set this to `1` if you are having compatibility issues with mini-game plugins where health is too high when switching back to the main world.
* `force_base_health` - If true, the player's base health attribute value will be forced to 20 on every refresh. Only enable if you have compatibility issues with other plugins.
* `keep_full_on_increase` - If true, increases in a player's max health when the player is already at full HP will automatically set them at the new max HP.
* `ensure_scaling_disabled` - Disables health scaling if the trait is disabled in order to remove past health scaling. Set to false if you have another plugin that is handling health scaling.

#### Saturation regen and hunger regen

* `use_custom_delay` - If true, the vanilla system for health regeneration will be replaced by a custom timer with a customizable delay and base value.
* `delay` - The number of ticks between health regen if `use_custom_delay` is enabled.
* `base` - The base HP to regenerate if `use_custom_delay` is enabled.

#### Mana regen

* `base` - The base amount of mana to regenerate per second when the trait is at level 0.

#### Double drop

* `max_percent` - The cap for the percent chance to double drop blocks.

#### Max mana

* `allow_overflow` - Whether to allow mana amounts higher than max, such as when a player's max mana decreases but their previous mana was higher than the new max.
* `base` - The base amount of max mana players have when the trait is at level 0.

#### Damage reduction

* `formula` - The formula for calculating damage reduction from the trait value. The input variable `value` is the trait value (which is after the stat value is multiplied by the trait modifier). The expression result is a decimal from 0 to 1 where 0 is no damage reduction and 1 is all damage blocked. For example, an expression result of 0.32 means 32% damage reduction.

#### Crit chance

* `base` - The base amount of crit chance players have when the trait is at level 0.

#### Crit damage

* `base` - The base amount of crit damage players have when the trait is at level 0.

#### Movement speed

* `max` - The maximum movement speed value.

