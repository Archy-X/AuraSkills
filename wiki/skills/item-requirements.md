---
description: Guide to item and armor requirements
---

# Item Requirements

Item requirements allow you to add skill level requirements in order to use an item. For example, you can create a pickaxe that only works when the player reaches Mining 15. There are two types of requirements: item and armor.&#x20;

Item requirements apply when using the item in the player's hand. Armor requirements only apply when equipping/wearing the item.

There are also two different scopes and ways to add requirements: using commands or global requirements.

## Command Requirements

Using commands to add requirements only adds the requirement to the specific item you are holding when creating the requirement. Therefore, you must distribute exact copies of that item in order for it to work.

Commands:

::: warning
The `item/armor` below means you only type `item` for item requirements and only `armor` for armor requirements.
:::

* `/sk item/armor requirement add <skill> <level> [lore]` - Adds an item/armor requirement to the held item. `skill` is the name of the skill that is required. `level` is the minimum skill level needed to use the item.  `lore` is an optional true/false argument that determines whether lore should be added to the item. Lore is completely separate from the functionality of the requirement, so you can change or remove any lore.
* `/sk item/armor requirement remove <skill> [lore]` - Removes an item/armor requirement from the item held. If `lore` is true, the lore that was originally added will be attempted to be removed from the item. This may not work if you added custom lore.
* `/sk item/armor requirement list` - Lists the item/armor requirements on the item held.
* `/sk item/armor requirement removeall` - Removes all item requirements from the item held.

## Global Requirements

Global requirements allow adding skill requirements to all items of a certain type/material. To add global requirements, you must add a `global` list to the `requirement.item` or `requirement.armor` section of `config.yml`.

The basic format for an entry in the list is:  `- MATERIAL SKILL:LEVEL`&#x20;

Here is an example of a global item requirement for diamond swords requiring Fighting 10 to use, including where it is located in the config:

```yaml
requirement:
  enabled: true
  item:
    global:
    - DIAMOND_SWORD FIGHTING:10
```

Multiple global requirements on a single item are also supported:

```yaml
requirement:
  armor:
    global:
    - DIAMOND_CHESTPLATE DEFENSE:10 AGILITY:4
```

::: info
Global requirements do not support custom items
:::
