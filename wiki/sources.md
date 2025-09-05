---
description: Guide to configuring XP sources
---

# Sources

**Sources** are the gameplay actions that give players XP in a skill. This includes things like mining blocks or killing mobs. Sources for each skill are configured in the `sources` folder, with a different file for each skill. Each source's XP value can be changed, and entirely new sources can be added based on a source type and its options.

## Default section

The `default` section is used in a source file to save lines by not having to write out the same options for every source. Each key in the default section is copied to every single source in the `sources` section, unless it is overriden by a key with the same path in the specific source.&#x20;

For example, the consider following source configuration with a default section:

```yaml
default:
  type: brewing
  trigger: takeout
  menu_item:
    material: potion
sources:
  awkward:
    ingredient: nether_wart
    xp: 10
    menu_item:
      potion_data:
        type: awkward
```

This is the same as the following source configuration without the default section:

```yaml
sources:
  awkward:
    type: brewing # Copied from previous default
    trigger: takeout # Copied from previous default
    ingredient: nether_wart
    xp: 10
    menu_item: # The menu_item section is combined with the previous default section
      material: potion # Material from the previous default
      potion_data: # Defined key from the source itself
        type: awkward
```

The default section is useful in making the file more concise for skills with sources of mostly one type that also share common options.

## Types

Each source must have a `type` key that defines the type of action the source is. If you see a source that doesn't specify a type, that means it's using the type in the `default` section.&#x20;

Each source has a name, which is simply the name of the section it is defined in. This name can be used in the [tags](sources.md#tags) section to reference the source. Sources must have unique names within a single skill.

::: info
Some options have a plural list variant, such as `block` and `blocks`. If the plural key is defined, the singular key should not be used, even if it is listed as required.
:::

#### Global options

The following is a list of options that apply to all source types:

* `xp` - The XP amount to give for the source. This is the base amount and does not include any ability, permission, or item multipliers. (Required)
* `display_name` - The readable name used in menus to identify the source. Default sources already have display names defined in the messages file at the path `sources.[type].[name]`. Only define a `display_name` on the source if you want to override the messages value or for newly created sources you don't need to be localized.
* `menu_item` - A section that defines the item used in the sources menu to represent the source. Placeholders to other keys can be used in `material` for example, to only need to define a single menu\_item in the default section. See [#Menu item](sources.md#menu-item) for details.
  * Oraxen items are support by using a string key prefixed with `oraxen:`. For example, `menu_item: oraxen:mythril` will use the exact item defined in Oraxen, including any NBT. This is defined as a string value directly on the `menu_item` key rather than the map section used for normal menu items.
* `unit` - A placeholder that defines the name of the unit for some sources whose XP amount is dynamic, such as Defense XP per damage or Forging XP per anvil cost experience. This is needed for some units to function when giving XP. See the section for the specific type for valid values.
* `income_per_xp` - Gives money based on the value times the XP gained. Only works if jobs are enabled in the [Main Config](main-config/#jobs). This works the same as the income\_per\_xp in the main config, but overrides it for the specific source. Mutually exclusive with income and income\_expression.
* `income` - Gives a fixed decimal money amount when the source is gained. Only works if jobs are enabled. Mutually exclusive with income\_per\_xp and income\_expression.
* `income_expression` - An expression to calculate the income, works the same as the jobs.income.default.expression in the [Main Config](main-config/#jobs). Only works if jobs are enabled. Mutually exclusive with income\_per\_xp and income.

### Anvil

The anvil source (`type: anvil`) gives XP when combining items in an anvil.

#### Options

* `left_item` - An [item filter](sources.md#item-filter) defining valid items in in the left slot. (Required)
* `right_item` - An item filter defining valid items in the right slot. (Required)
* `multiplier` - A placeholder for multiplying the base XP. Currently must be the value `'{repair_cost}'`, which is the amount of experience levels used in the anvil.

### Block

The block source (`type: block`) gives XP for breaking or interacting with blocks. The options can be used to define complex sources involving multiple blocks or specific block states.

#### Options

* `block` - The block type/material. This must be a valid Bukkit [Material](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html) in all lowercase. (Required)
  * Oraxen custom blocks are supported by prefixing the block name with `oraxen:`. For example, `block: oraxen:mythril_ore` will automatically register the block state from Oraxen without neeeding to manually define a `state`.
* `blocks` - A list of multiple block types used to group multiple blocks to the same source. Overrides `block`.
* `trigger` - The type of action on the block. Can be either `break` or `interact`. `break` is simply when a block is broken by a player with left click. `interact` is when the block is right clicked. (Required)
* `triggers` - A list of multiple triggers.
* `check_replace` - Whether player-placed blocks should not give XP. If true, placed blocks will not give XP. If false, any block that matches the source will give XP. Defaults to true.
* `state` - A section of keys that defines the specific block state the block must match.
* `states` - A list of block state sections to match any of the block states in the list.
* `after_state` - A block state that is checked to match one tick after the block is interacted with. If the block does not match the after\_state, XP will not be given. Only works if `trigger` is set to `interact`.
* `after_states` - A list of block states to check on tick after. XP is given if the block still matches any states in the list.
* `state_multiplier` - An expression with block state variables that evaluates to a number to multiply the base `xp` given.
* `support_block` - A direction defined for some blocks that will automatically break if an adjacent block is broken. Valid values are `above`, `below`, `side`, and `none`. This ensures that the block can be unmarked as a player placed block when it is indirectly broken. Defaults to `none`.
* `max_blocks` - The maximum number of blocks Treecapitator can break for this trunk type.

### Brewing

The brewing source (`type: brewing`) gives XP when brewing potions in a brewing stand.&#x20;

#### Options

* `ingredient` - An item filter defining valid potion ingredients
* `trigger` - When to give XP, either on `brew` or `takeout`. Using `brew` means that auto-brewers will still give XP to the player who placed the brewing stand.

### Damage

The damage source (`type: damage`) gives XP when the player takes damage. In the default sources, this handles both the Defense sources and fall damage in Agility. The resulting XP given is the `xp` key multiplied by the amount of damage taken.

#### Options

* `cause` - The cause of the damage that is required for the source. Must be a valid Bukkit [DamageCause](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html) in all lowercase.
* `causes` - A list of multiple damage causes.
* `excluded_cause` - A cause that is excluded for the source. If no `cause`/`causes` is specified, any cause other than the `excluded_cause` will work.
* `excluded_causes` - A list of multiple excluded damage causes.
* `damager` - A specific entity type that the player must be damaged from. Can be either `mob`, `player`, or any Bukkit [EntityType](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html) in all lowercase. Specifying a damager automatically excludes the source from non-entity damage, like fall damage.
* `damagers` - A list of multiple valid entity types.
* `excluded_damager` - A specific entity type that is excluded from giving XP for the source.
* `excluded_damagers` - A list of entities types excluded from giving XP.
* `must_survive` - Whether the player must survive the damage taken in order to gain XP. Defaults to true.
* `use_original_damage` - Whether the XP given should be multiplied by the original damage dealt without any damage reduction modifiers (armor, stats, abilties, etc). Defaults to true.
* `cooldown_ms` - A delay before XP can be gained again in milliseconds (200 by default).

### Enchanting

The enchanting source (`type: enchanting`) gives XP when enchanting an item in an enchanting table.

#### Options

* `item` - An item filter defining the valid items to enchant. (Required)
* `unit` - The unit to use to multiply XP. Currently must be `'{sources.units.enchant_level}'`

### Entity

The entity source (`type: entity`) gives XP for a player killing or damaging an entity.

#### Options

* `entity` - A valid Bukkit [EntityType](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html) that specifies the type of entity the player kills/damages. (Required)
* `trigger` - The trigger for when to given XP, either on entity `death` or `damage`. (Required)
* `triggers` - A list of multiple triggers.
* `damager` - A damager to match when giving XP, which can either be `player`, `projectile`, or `thrown_potion`.
* `damagers` - A list of multiple valid damagers.
* `scale_xp_with_health` - If the `trigger` is damage, the damage XP multiplier will be scaled by the damaged mob's max health. The total XP gained from killing a mob will be consistent between death and damage triggers. Defaults to true.
* `cause` - The cause of the damage that is required for the source. Must be a valid Bukkit [DamageCause](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html) in all lowercase.
* `causes` - A list of multiple damage causes.
* `excluded_cause` - A cause that is excluded for the source. If no `cause`/`causes` is specified, any cause other than the `excluded_cause` will work.
* `excluded_causes` - A list of multiple excluded damage causes.

### Fishing

The fishing source (`type: fishing`) is used for players fishing.

#### Options

* `item` - An item filter defining the item the player fishes up.

### Grindstone

The grindstone source (`type: grindstone`) gives XP for disenchanting items in a grindstone.

#### Options

* `multiplier` - A placeholder to multiply the base `xp` by when giving XP. Currently can only be `'{total_level}'`, which is the sum of the enchantment levels of all the enchants removed by the grindstone.

### Item consume

The item consume source (`type: item_consume`) gives XP when players consume a potion or eat an item.

#### Options

* `item` - An item filter defining the item that is consumed. (Required)

### Jumping

The jumping source (`type: jumping`) gives XP for the player jumping.

#### Options

* `interval` - The number of jumps required to give XP. The XP given per jump is `xp/interval`. Defaults to 100.

### Mana ability use

The mana ability use source (`type: mana_ability_use`) gives XP when the player uses a mana ability. The amount of XP given is the `xp` multiplied by the amount of mana consumed.

#### Options

* `mana_ability` - The name of a specific mana ability to only give XP for when used.
* `mana_abilities` - A list of mana abilities to only give XP for using.

### Potion splash

The potion splash source (`type: potion_splash`) gives XP when a player uses a splash or lingering potion.

#### Options

* `item` - An item filter defining the type of potion splashed. (Required)

### Statistic

The statistic source (`type: statistic`) gives XP when a Minecraft player statistic increases. XP is given at a fixed interval controlled by the `xp_gain_period` option under Endurance in `skills.yml`. The default period is every 5 minutes. For the source to work, `stats.disable-saving` in the server's `spigot.yml` must be false (it's false by default, so only check if you changed it).

#### Options

* `statistic` - The name of the statistic to track increases and give XP for. Must be a valid Bukkit [Statistic](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Statistic.html) in all lowercase. (Required)
* `multiplier` - An flat amount to multiply the XP gained by. (Defaults to 1)
* `minimum_increase` - The minimum amount the statistic has to increase by within the check period in order for XP to be given. If the amount gained is less than the minimum, it will still be added towards the next time the amount is checked. (Defaults to 1)

## Item filter

Multiple sources that have an `item`, `ingredient`, or similar options use the item filter format, which  defines a filter the item used in the source type generally has to pass in order to be matched to the specific source. This allows specifying only one specific item with an exact material and meta, or more general filters allowing multiple materials.

### General options

* `material` - A single specific material the item must be. This should be a valid Bukkit [Material](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) in all lowercase.
* `materials` - A list of multiple materials the filter can match.
* `excluded_material` - A single material to exclude from being matched.
* `excluded_materials` - A list of multiple materials to exclude from being matched.
* `category` - A category name to match all items from. Valid values are `weapon`, `armor`, `tool`, `fishing_junk`, and `fishing_treasure`.

### Direct value

In cases where you only need a single material in the filter, you can directly specify the material name as a value, instead of in the subsection.

For example:

```yaml
ingredient: nether_wart
```

is equivalent to

```yaml
ingredient:
  material: nether_wart
```

### Meta options

Further options can be used to narrow down the item more specifically than just the material. These options are placed in the same indentation level as the general options (one level to the right of the section name like `item` or `ingredient`).

* `display_name` - A string display name that must exactly match with the item.
* `lore` - A list of strings defining the lore that must exactly match with the item.
* `potion_data` - A section containing options specifying the type of potion to match.
  * `type` - A valid Bukkit [PotionType](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionType.html) to match in all lowercase.
  * `types` - A list of multiple types of potions to match.
  * `excluded_type` - A PotionType to exclude from matching.
  * `excluded_types` - A list of multiple types of potions to exclude from matching.
  * `extended` - Whether the potion must have an extended duration.
  * `upgraded` - Whether the potion must have an upgraded level.
* `custom_model_data` - An integer to match the CustomModelData id of an item. Both the material and custom\_model\_data must match to pass the filter.

### Examples

Example of an item filter used in an item\_consume source:

```yaml
drink_regular:
  type: item_consume
  item: # The item filter section name
    material: potion # The item consumed must be a potion
    potion_data:
      # The type of potion cannot be mundane, thick, awkward, or a water bottle
      excluded_types: [ mundane, thick, water, awkward ]
      extended: false # The potion cannot be extended
      upgraded: false # The potion cannot be upgraded
  xp: 20
```

Example of an item filter used in a brewing source:

```yaml
regular:
  type: brewing
  trigger: takeout
  ingredient: # The item filter section name
    # The ingredient cannot be any of these materials
    excluded_materials: [ redstone, glowstone_dust, nether_wart, gunpowder, dragon_breath ]
  xp: 15
```

## Menu item

As mentioned above, the `menu_item` option is a section that defines the item used in the sources menu to represent the source. The format for this section is explained in the [Slate Items page](../slate/items).

Built-in placeholders can be used to reference other keys inside the menu\_item.

```yaml
default:
  type: block
  trigger: break
  menu_item:
    material: '{block}'
```

The above example default section for a block source will replace the `{block}` placeholder with the value of `block` for each source below. So if the dirt source has `block: dirt`, the resulting menu item material will be `material: 'dirt'`.

## Tags

The `tags` section of a source file is used to configure plugin-provided list of sources for certain abilities or mechanics. You cannot add or remove tags from the section, only modify the list of the provided tags.

For example, the `farming_luck_applicable` tag is the list of sources that will apply the double drop bonus of the Farming Luck trait.

### Wildcards and exclusions

By default you might not see the names of sources in the list, since many just the symbol `*` on the list. This adds all the sources in the skill to the tag.

If you want to exclude a source from the list that already has a wildcard without addding every single other source, you can prefix the source name with `!` to exclude it. For example, the exclude only sugar cane from Farming Luck:

```yaml
tags:
  farming_luck_applicable:
    - '*'
    - '!sugar_cane'
```

Wildcards can also be combined with text at the beginning or end of the string:

```yaml
tags:
  farming_luck_applicable:
    - 'stripped_*'
```

This example shows making Farming Luck only applicable to wheat, potato, carrot, and beetroot. Note that the names used in the list are the source names (section names under `sources`), not the value of `block` within the source.

```yaml
tags:
  farming_luck_applicable:
    - wheat
    - potato
    - carrot
    - beetroot
```

### Excavation tags

* `excavation_luck_applicable` - List of sources that can drop extra items from the Excavation Luck trait
* `metal_detector_applicable` - List of sources that can drop the `rare` loot table for Excavation
* `lucky_spades_applicable` - List of sources that can drop the `epic` loot table for Excavation
* `terraform_applicable` - List of sources that can be broken by the Terraform mana ability

### Farming tags

* `farming_luck_applicable` - List of sources that can drop extra items from the Farming Luck trait

### Foraging tags

* `foraging_luck_applicable` - List of sources that can drop extra items from the Foraging Luck trait
* `trunks` - List of sources can activate Treecapitator
* `treecapitator_applicable` - List of sources that can be broken by Treecapitator

### Mining tags

* `mining_luck_applicable` - List of sources that can drop extra items from the Mining Luck trait
* `speed_mine_applicable` - List of sources that can activate the Speed Mine mana ability

