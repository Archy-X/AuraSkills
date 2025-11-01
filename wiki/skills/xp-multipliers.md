---
description: Guide to XP multipliers
---

# XP Multipliers

**Multipliers** are a way of increasing the amount of skill XP a player receives without changing the sources config.

Multipliers are permission based, meaning you can add them to individual players or groups of players (such as ranks). If your permissions plugin supports temporary permissions, you can even create temporary XP multiplier events.

## Adding Multipliers

To add a multiplier, use your permissions plugin to add a permission in the following format:

> **auraskills.multiplier.\[percent]**

Replace \[percent] with the **percent more XP** you want the multiplier to give. For example, the permission `auraskills.multiplier.100` will give 100% more or 2x XP. The percent supports decimals using `.` (e.g. `auraskills.multiplier.10.5`).

## Skill Specific Multipliers

Multipliers can also be added to only multiply XP from sources of a specific skill. The format of the permission is `auraskills.multiplier.[skill].[percent]` where \[skill] is the default English name of the skill in lowercase.

Example permission for 1.5x farming multiplier: `auraskills.multiplier.farming.50`

## Multiple Multipliers

If a player has more than one multiplier permission, the multipliers will add together and work as if there was only a single multiplier. For example, 100 (2x) and 50 (1.5x) multipliers will add to 150 (2.5x).

If you want multiplier multipliers that have the same value, you cannot simply use the same permission because inherently permissions must be unique. As a workaround, you can add trailing decimal zeros so that they are technically unique, but have the same value.&#x20;

For example, `auraskills.multiplier.100` will work the same as `auraskills.multiplier.100.0`. You can continue adding trailing zeros to add as many duplicates as you like.

## LuckPerms Examples

The following are examples of adding multipliers using LuckPerms, a popular permissions plugin. If you use a different permissions plugin, use the commands from that plugin to add the multiplier like any other permission.

Adding a 2x multiplier to a player:

```
/lp user [player] permission set auraskills.multiplier.100
```

Adding a 1.5x multiplier to a rank called vip:

```
/lp group vip permission set auraskills.multiplier.50
```

Adding a temporary 3x multiplier to everyone for 12 hours:

```
/lp group default permission settemp auraskills.multiplier.200 true 12h
```

Removing a 2x multiplier from a player:

```
/lp user [player] permission unset auraskills.multiplier.100
```

::: warning
When removing multipliers, you must use the exact text of the permission you added.
:::

## Item and Armor Multipliers

Multipliers can also be added to items and armor similar to stat modifiers. Item multipliers increase XP when holding the item. Armor multipliers increase XP when wearing the item. Multipliers can be either global or only for a specific skill.

The commands used for item/armor multipliers:

* `/sk item/armor multiplier add <target> <value> [lore]`
* `/sk item/armor multiplier remove <target>`
* `/sk item/armor multiplier list`
* `/sk item/armor multiplier removeall`

Use either item or armor in the command depending on the type of multiplier you want. Target can be either global or the name of a skill. The value of the multiplier is the percent more XP gained (works the same as permission multipliers).

These multipliers are included in the /sk multiplier command, which can display skill-specific multipliers if they are different from the global multiplier.
