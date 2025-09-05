---
description: Guide to block requirements
---

# Block requirements

Block requirements are customizable restrictions on breaking, placing, or harvesting blocks. For requirements on
using items, see [Item Requirements](item-requirements.md).

## Adding block requirements

Block requirements are defined under the `requirement.blocks.list` in `config.yml`. For example, adding a requirement of Mining level 5 to break iron ore:

```yaml
requirement:
  block:
    list:
    - material: iron_ore
      allow_break: false
      allow_place: true
      requirements:
      - type: skill_level
        skill: mining
        level: 5
```

Since the default config has an empty list defined as `list: []`, you must
remove these brackets when adding requirements.

### Requirement section keys

Each element in `list` represents a group of one or more requirements nodes for a single block type.
The following keys can be defined:
* `material` - The name of the block to add requirements for (required)
* `allow_break` - Whether to ignore requirements on block break (defaults to false)
* `allow_place` - Whether to ignore requirements on block place (defaults to false)
* `allow_harvest` - Whether to ignore requirements on block harvest. This is for crops that drop items but are not broken. (defaults to false)

When none of the above allow options are defined, the block cannot be broken, placed, or harvested when requirements are not met.

* `requirements` - A map list of the requirement nodes for this block. All the requirements nodes must be met to be able to break/place
  the block defined by `material`. The keys for each node are listed below.

### Requirement node keys

The following keys are defined in each mapping of the `requirements` list of a requirement section.

* `type` - The type of requirement, which can be `skill_level`, `permission`, `excluded_world`, or `stat`
* `message` - The error message to send to the player when the requirement is not met. Supports MiniMessage and PlaceholderAPI (optional).

Each type has specific keys below that must be added to define type behavior. These keys are added in the same indent level as `type`.

#### Skill level

The `skill_level` type requires the player to be at least a specific level in a skill.

Keys:
  * `skill` - The name of the skill to add a level requirement for
  * `level` - The minimum skill level the player must be

#### Permission

The `permission` type requires the player to have a specific permission node.

Keys:
  * `permission` - The permission node required

#### Excluded world

The `excluded_world` type defines a list of worlds that will make the requirement fail if the player is in one of them.

Keys:
  * `worlds` - The list of worlds to not allow the player to be in

#### Stat

The `stat` type requires the player to be at least a specific stat level.

Keys:
  * `stat` - The name of the stat to add a level requirement for
  * `value` - The minimum stat value that the player must be

## General options

The `requirement.blocks` section in `config.yml` contains general options related to the block requirement system:
* `enabled` - Whether block requirements are checked at all
* `bypass_in_creative_mode` - Whether to ignore block requirements for players in creative mode (defaults to true)
* `bypass_if_op` - Whether to ignore block requirements for players that are op (defaults to false)

## Example

Example of multiple requirement sections using all requirement node types:

```yaml
requirement:
  block:
    list:
    - material: iron_ore
      allow_break: false
      allow_place: true
      requirements:
      - type: skill_level
        skill: mining
        level: 5
      - type: permission
        permission: some.permission.node
    - material: sweet_berry_bush
      allow_place: true
      allow_harvest: true
      requirements:
      - type: excluded_world
        worlds:
        - world_nether
      - type: stat
        stat: regeneration
        value: 100
```
