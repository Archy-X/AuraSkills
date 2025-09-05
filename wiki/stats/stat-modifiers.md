---
description: Guide to stat modifiers
---

# Stat Modifiers

**Modifiers** are temporary [stat](./) buffs on a player. They come in 3 different types and are interacted with primarily using commands. Regular modifiers are attached to a name and are always active unless removed. Item modifiers apply when holding an item. Armor modifiers apply when wearing armor.

## Regular Modifiers

Regular modifiers are the simplest type of modifier. You add and remove them using commands. Each modifier has 3 properties, a `name`, `stat`, and `value`.

The `name` is used to identify the modifier. You **cannot** have more than one modifier with the same name on a single player.

The `stat` property is which stat the modifier should be applied on. You must use the official stat names, which are `health`, `strength`, `regeneration`, `luck`, `wisdom`, and `toughness`.

The `value` property is how much the stat should increase/decrease. For example, a `value` of `50` would add 50 levels to whatever stat it was applied on. Negative values subtract levels.

The commands used to interact with them are:

* `/sk modifier add [player] [stat] [name] [value] (silent)` - Adds a stat modifier to a player
* `/sk modifier remove [player] [name] (silent)` - Removes a specific stat modifier from a player
* `/sk modifier list (player) (stat)` - Lists all or a specific stat's modifiers for a player
* `/sk modifier removeall (player) (stat) (silent)` - Removes all stat modifiers from a player

## Item Modifiers

Item modifiers are like regular modifiers but stored on items. The modifier will only be applied when the player is **holding** the item. The commands will by default apply lore displaying the modifier's stat and value. The item modifier itself is independent of lore and is stored as custom NBT data. Therefore, you can modify lore however you want and it will still work. You can add multiple modifiers, but you can only have 1 modifier of each stat type.

The commands used to interact with them are:

* `/sk item modifier add [stat] [value] (lore)` - Adds an item stat modifier to the item held, along with lore by default
* `/sk item modifier remove [stat] (lore)` - Removes an item stat modifier from the item held, and the lore associated with it by default
* `/sk item modifier list` - Lists all item stat modifiers on the item held
* `/sk item modifier removeall` - Removes all item stat modifiers from the item held

## Armor Modifiers

Armor modifiers are exactly like item modifiers, except they only apply when the player is **wearing** the item as armor. You can have both item and armor modifiers on the same item and can have an item with an item and armor modifier of the same stat.

The commands used to interact with them are:

* `/sk armor modifier add [stat] [value] (lore)` - Adds an armor stat modifier to the item held, along with lore by default
* `/sk armor modifier remove [stat] (lore)` - Removes an armor stat modifier from the item held, and the lore associated with it by default
* `/sk armor modifier list` - Lists all item armor modifiers on the item held
* `/sk armor modifier removeall` - Removes all armor stat modifiers from the item held
