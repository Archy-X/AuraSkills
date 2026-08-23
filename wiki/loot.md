---
description: Guide to configuring loot tables
---

# Loot

## Overview

The Fishing and Excavation skills have abilities that can drop custom loot, which is configured in the `loot` folder. For Fishing, the `treasure_hunter` ability corresponds to the rare pool and the `epic_catch` ability gives items from the epic loot pool. The `metal_detector` ability in Excavation gives rare loot while the `lucky_spades` ability gives epic loot.

::: info
The drop chances for the abilities are configured separately in `abilities.yml`, see the Abilities page for more details.
:::

Beyond changing the items dropped for these abilities, the files enable adding custom loot pools unrelated to abilities, executing commands as loot, as well as creating new loot tables for other skills (Foraging and Mining).

## File structure

Loot tables are separated into files for each skill in the loot folder. The name of the file is the skill that the file corresponds to. By default, there are excavation and fishing loot tables. You can optionally add loot tables for mining and foraging, as long as they follow the format.

## Loot pools

Loot pools are lists of loot entries, and each loot table file can have multiple loot pools. In the default excavation and fishing loot tables, there are the rare and epic loot pools. However, you can add any amount of new loot pools.

Loot pools are defined in the pools section of the loot file. Each pool must have a unique name for the config section name. In excavation and fishing, `rare` and `epic` are reserved names that integrate with abilities, so it is discouraged to change them.

Keys:

* `base_chance` - The chance to select this pool without any added ability bonuses (1 = 1% chance) (defaults to 1)
* `selection_priority` - The order the pools are potentially selected, a higher priority means that it will attempt to select before lower priority pools (defaults to 1)
* `override_vanilla_loot` - Whether the regular loot from the block should be replaced with the selected loot. Does not apply to fishing (defaults to 1).
* `chance_per_luck` - The amount the chance of selecting the pool should increase by per Luck stat level the player has (0.1 = +0.1% chance per Luck) (defaults to 0)
* `require_open_water` - For Fishing, only give items in the pool when the player fishes in open water (5x5x5 volume of water) when set to true. This prevents giving loot when using most AFK fish farms (defaults to false).

Each pool has a loot section containing a map list of the loot entries. This uses the same type of syntax as rewards, which is explained [here](rewards.md#yaml-syntax).

## Loot entries

Loot entries can be one of the following types:
* [Item](#item-loot)
* [Command](#command-loot)
* [Entity](#entity-loot) (Fishing only)
* [Group](#group-loot)

Each loot entry must have certain required keys and can optionally have more.
Optional keys are either notated with (optional) or have their default values specified on this page.
Each key is explained below:

Universal Keys (apply to any loot type):

* `type` - The type of loot, either `item` or `command`
* `weight` - The weight of the entry compared to other loot in the same pool. A higher weight means the loot entry is more likely to be selected (defaults to 10).
* `message` - A chat message to send to the player when they get the loot. This supports color codes and PlaceholderAPI (optional).
* `sources` - A list of sources or source tags that this loot should exclusively apply to. The names of sources should match the exact name in sources\_config.yml. Does not support custom block sources yet (optional).
* `xp` - The amount of skill xp to give when this loot is dropped (defaults to the original source amount)

### Item loot

(`type: item`)

Item loot keys:

* `material` - The material of the item, must be valid bukkit material name (not case sensitive). Use material:id to specify legacy data if on 1.12
* `amount` - Controls the amount of item to give. This can either be a single number like '5' or a range of numbers such as '1-5', which gives a random amount from 1 to 5 (both inclusive) (defaults to 1)
* `key` - References an item key registered using /skills item register. If this is specified, material and all keys below do not apply
* `display_name` - The display name of the item, supports & for color codes unless escaped with \\& (optional)
* `lore` - The lore of the item, must be a list of each line, supports & for color codes unless escaped with \\& (optional)
* `enchantments` - A list of enchantments. See the [section below](loot.md#enchantments) that explains the format.
* `damage` - The ratio of total durability removed from the item. An item with `damage: 0.4` will have 40% less durability than max, or be at 60% of total durability. If damage is a mapping, durability will be set randomly based on a range. The following keys set the bounds:
  * `min` - The lower bound for the durability ratio removed.
  * `max` - The upper bound for the durability ratio removed.
* `potion_data` - A section containing potion data, see below for keys (optional):
  * `type` - The type of potion, must be a bukkit PotionType
  * `extended` - Whether the potion has an extended duration
  * `upgraded` - Whether the potion has an upgraded level
* `custom_effects` - A map list of effects, keys for each section below (optional):
  * `type` - The name of the effect, must be a bukkit PotionEffectType
  * `duration` - The duration of the effect, in ticks
  * `amplifier` - The amplifier of the effect (0 means level 1)
* `glow` - Set to true to make the item glow without enchantments showing (optional)
* `nbt` - Section for any custom NBT data (optional)
* `flags` - List of item flags to add to the item (optional)
* `ignore_legacy` - If true, the plugin will skip loading the item if the server version is below 1.13 to prevent material parsing errors. The only reason you should use this if you are planning on using the same config for legacy and modern versions and an item you have does not exist in 1.12. The plugin is able to recognize modern material names on legacy versions without any issues. (optional)

#### Enchantments

The `enchantments` key on an item is a list that has either string or mapping elements.

As a string, an enchantment is formatted as `<enchantment_name> <level>` or `<enchantment_name> <min_level>-<max_level>`. For example:

```yaml
enchantments:
- unbreaking 3 # Item always has Unbreaking 3
- sharpness 1-3 # Item has Sharpness with a random level from 1 to 3
```

An enchantment as a mapping has the following keys:

* `name` - The name of the enchantment
* `level` - The level of the enchantment, supports ranges
* `chance` - The chance that is enchantment is added to the item, from 0 to 1 (defaults to 1).

For example:

```yaml
enchantments:
- name: unbreaking
  level: 2
  chance: 0.5 # 50% chance to have Unbreaking 2, otherwise no enchantments
```

#### Examples

Basic example of an item loot:

```yaml
- type: item
  material: iron_ingot
  weight: 10
  amount: 1-3
```

Here is a more complex example with display name, lore, and enchantments:

```yaml
- type: item
  material: diamond_sword
  weight: 5
  amount: 1
  display_name: '<red>Fire Sword'
  lore:
    - '<gray>Powerful weapon'
    - ' '
    - '<blue>RARE'
  enchantments:
    - sharpness 5
    - fire_aspect 2
    - looting 3
```

Here is a potion with base potion data and custom effects:

```yaml
- type: item
  material: potion
  weight: 10
  amount: 1
  potion_data:
    type: speed
    upgraded: true
  custom_effects:
    - type: jump
      duration: 1000
      amplifier: 2
    - type: regeneration
      duration: 40
      amplifier: 3
```

An item with hidden enchants using item flags and custom model data using the nbt section

```yaml
- type: item
  material: paper
  weight: 10
  amount: 1
  custom_model_data: 14
  enchantments:
    - knockback 2
  flags:
    - hide_enchants
```

### Command loot

(`type: command`)

Command loot is used to execute any command when the player gets the loot, either through console or by the player.

Command loot keys:

* `executor` - Who should execute the command, either console or player (defaults to console)
* `command` - The command without the beginning /, supports built-in [string placeholders](#string-placeholders)
  and all PlaceholderAPI placeholders
* `commands` - A list of multiple commands to run. Only `commands` or `command` should be defined, not both.

Example:

```yaml
- type: command
  weight: 10
  executor: console
  command: say hi
```

Example of running multiple commands:

```yaml
- type: command
  weight: 10
  executor: console
  commands:
    - say 1
    - say 2
```

### Entity loot

(`type: entity`)

Entity loot spawns an entity when the player gets the loot. Currently only Fishing supports catching entity loot.

Entity loot keys:

* `entity` - Defines the type of entity caught.
  * Vanilla entities/mobs are defined by name (e.g. `entity: zombie`)
  * MythicMobs can be used with the `mythicmobs:` prefix (e.g. `entity: mythicmobs:SkeletonKing`)
* `health` - Overrides the entity max health attribute.
* `damage` - Overrides the entity attack damage attribute.
* `level` - Defines the AuraMobs or MythicMobs level.
* `velocity:`
  * `horizontal` - Overrides the default horizontal velocity of the entity when spawned.
  * `vertical` - Overrides the default vertical velocity of the entity when spawned.
* `hand`, `off_hand`, `feet`, `legs`, `chest`, `head` - Defines equipment on the spawned entity. The value is a mapping in the same item format as regular item loot (Using keys `material`, `enchantments`, etc.)

### Group loot

(`type: group`)

A loot entry with this type is simply a list of multiple other loot types. This allows multiple loot entries to be
given at the same time for a single roll.

Group loot keys:
* `entries` - A list of any loot entries that will be given together when the group loot is selected. The
  following keys will not do anything if defined on individual entries in the group: `weight` and `sources`. Those
  should only be defined on the group itself.

For example, you can run a command and send an action bar together:

```yaml
- type: group
  weight: 10
  entries:
    - type: command
      executor: console
      command: say hi
    - type: action_bar
      message: '<green>Earned loot'
      duration: 20
```

### Action bar

(`type: action_bar`)

Shows the player a message on the action bar when given.

Action bar keys:
* `message` - The message to send. Supports MiniMessage and PlceholderAPI.
  Additionally supports built-in [string placeholders](#string-placeholders).
* `duration` - The amount of time to pause the idle action bar so the message stays visible. Use either a plain number
  (ticks) or a string like `2s` (defaults to 40 ticks).

Example:

```yaml
- type: action_bar
  message: '<gold>You earned loot in {skill}!'
  duration: 40
```

### Title

(`type: title`)

Shows the player a title message in the center of the screen.

Keys:
- `title` - The main title text, supports MiniMessage. If not present, the title is blank.
- `subtitle` - The subtitle text, supports MiniMessage. If not present, the subtitle is blank.
- `fade_in` - The duration of the title fading in. Use a plain number for ticks or a format like `1s`.
- `stay` - The duration to keep the title on the screen.
- `fade_out` - The duration of the title fading out.

Example:

```yaml
- type: title
  title: '<red>Title'
  subtitle: '<gray>A subtitle'
```

### Chat

(`type: chat`)

Sends the player a chat message.

Keys:
- `text` - The text of the message to send, supports MiniMessage.

Example:

```yaml
- type: chat
  text: '<green>A chat message'
```

### Money

(`type: money`)

Use one of the following keys, not both:
- `amount` - A fixed amount of money to give.
- `formula` - An expression for determining the amount of money to give for a given `level` variable.

Example:

```yaml
- type: money
  formula: '100+10*level*level'
```

### Sound

(`type: sound`)

Keys:
- `sound` - The name of the sound.
- `category` - The category of the sound, such as `players`, `blocks`, or `ui`. Defaults to `master`.
- `volume` - The volume of the sound. Defaults to 1.
- `pitch` - The pitch of the sound. Defaults to 1.

Example:

```yaml
- type: sound
  sound: entity.player.levelup
  category: ui
  volume: 0.8
  pitch: 1.2
```

## String placeholders

In addition to all PlaceholderAPI placeholders, some loot types have values that
support custom built-in placeholders:
* `{player}` - Player username
* `{world}` - Name of the world the player is in
* `{skill}` - The display name of the skill the loot was obtained in
* `{skill_id}` - The official lowercase id of the skill

If the loot was obtained from a block (Excavation, Mining, etc.), the following placeholders
are also available:
* `{x}, {y}, {z}` - coordinates of the block (integers)
* `{x_center}, {y_center}, {z_center}` - decimal coordinates of the center of the block

## Loot selection

Loot selection logic uses the following steps:

1. The loot table is determined by skill that corresponds to the action the player does. For block based loot, the block must be a source for a supported skill (foraging, mining, excavation).
2. A loot pool is selected from the loot table starting from the highest priority loot pool. Whether pool is selected is base on the pool's base\_chance and any added chances. If a pool is not selected, the pool with the next highest selection priority will be attempted. If no loot pool gets selected, the below steps do not apply.
3. A single loot entry is selected from the pool based on the weight of the entry. A higher weight makes it more likely for it to be selected. The exact chance is calculated by `weight / sum of all weights in pool`.

## Loot requirements

Loot requirements restrict obtaining loot unless the player meets the defined requirements. Requirements can be set
for an entire loot table, a loot pool, or a loot entry using the `requirements` key.

The `requirements` key is a list of mappings, where each mapping is a requirement. **The types of requirements available are
explained in [block requirements](skills/block-requirements.md#requirement-types)**, since both use the same system.

### Examples

Require at least Excavation level 5 to obtain any loot from the entire loot table file:

```yaml
type: block
requirements:
  - type: skill_level
    skill: excavation
    level: 5
pools:
  # ...
```

Require at least 20 Strength stat to obtain loot from the `rare` loot pool:

```yaml
type: fishing
pools:
  rare:
    base_chance: 2
    selection_priority: 1
    requirements:
      - type: stat
        stat: strength
        value: 20
    loot:
      # ...
```

Require a permission node to obtain `iron_ingot` from the `rare` pool:

```yaml
type: fishing
pools:
  rare:
    base_chance: 2
    selection_priority: 1
    loot:
      - type: item
        material: iron_ingot
        weight: 15
        amount: 1
        requirements:
          - type: permission
            permission: some.permission.node
```

Require that the player is not in the nether **and** at least Mining level 10:

```yaml
requirements:
  - type: excluded_world
    worlds:
      - world_nether
  - type: skill_level
    skill: mining
    level: 10
```

## Custom loot tables

Completely new loot table files can be created for any skill with block sources or for mobs. This means the following new loot table file names are supported in the `loot/` folder:

* `farming.yml`
* `foraging.yml`
* `mining.yml`
* `mob.yml`

### Mob

For the mob loot table, add a `mobs` list to a loot entry to specific which mob it drops from. You should also specific `type: mob` at the beginning of the file. For example, the following loot entry drops a custom enchanted book (from a different plugin) when killing a blaze:

```yaml
type: mob
pools:
  adrenaline:
    base_chance: 0.5
    loot:
      - type: item
        material: enchanted_book
        enchantments: [adrenaline 1-3]
        mobs: [blaze]
```

### Block

Block loot tables must have `type: block` at the top of the file. When specifying the `sources` list, the values must be the exact name used as the section name inside the corresponding `sources` config file (not the material name).

The following is an example custom Farming loot table that has a 0.05% chance to drop an enchanted golden apple when breaking carrots. This will automatically handle breaking only fully-grown crops (for crops with growth stages) and naturally-grown crops (for things like melon and pumpkin).

```yaml
type: block
pools:
  carrot:
    base_chance: 0.05
    loot:
      - type: item
        material: enchanted_golden_apple
        amount: 1
        sources: [carrot]
```
