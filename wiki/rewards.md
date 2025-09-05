---
description: Guide to creating to custom rewards
---

# Rewards

## Introduction

Rewards are a powerful way of performing actions or giving things when a player levels skills. There are many different types of rewards, including stats, commands, permissions, items, and money. Rewards also support custom messages in menus and level-up chat messages.

### Yaml syntax

Configuring rewards requires Yaml syntax that you might not have seen before, technically known as map lists. These are basically an array of objects in JSON. Map lists are defined like this:

```yaml
patterns:
  - type: stat # Start of first object in list
    stat: strength
    value: 1
    pattern:
      interval: 1
  - type: stat # Start of second object in list
    stat: health
    value: 1.5
    pattern:
      interval: 1
```

::: warning
Pay attention to the spacing above. The indent level of the keys (type, stat, value) should be aligned exactly as shown! If you have a loading error, it's probably a spacing/indentation issue.
:::

## File structure

Rewards are configurable in files in the `rewards` folder, and the name of the file corresponds to the skill it configures (eg. farming.yml controls Farming rewards only). The`global.yml` file configures rewards for all skills, so rewards added in it will be applied to all skills. You will already see files for each skill containing the default rewards. Each file is split into two main sections, `patterns` and `levels`.

### Patterns section

The patterns section is for rewards that repeat in a regular pattern within a skill's progression. Each reward in this section should have a `pattern` section that can define a `start`, `interval`, and `stop`. Patterns can be used for every type of reward.

The `start` value is the skill level the reward should first appear at; this should be at least 2 since the first level up possible is from level 1 to 2. If `start` isn't specified, it will default to 2.

The `interval` is the difference between consecutive rewards; an interval of 2 means the reward appears every other level (2, 4, 6,...). The default `interval` is 1.

The `stop` number is the maximum level the reward should appear at, so there will only be that reward below or at the specified `stop` level. This defaults to the `max-level` option of that skill in `skills.yml`.

Example pattern reward for a command that executes every level up:

```yaml
patterns:
  - type: command
    executor: console
    command: 'say this executes every level up'
    pattern:
      start: 2
      interval: 1
```

### Levels section

The levels section is for defining single rewards at a specific level. You must first define a section of the level you want to define for, like so:

```yaml
levels:
  5:
    - type: command
      executor: console
      command: say hi
```

This command reward will be executed when the player levels up to skill level 5.

## Reward types

Every reward must have a `type` value of the reward type it is. Each type of reward has different keys you can define that configure the reward, both required and optional.

### Stat rewards (`stat`)

Stat rewards level up an AuraSkills stat, which includes health, regeneration, strength, luck, wisdom, and toughness. Menu and level-up chat messages are automatically handled.

Keys:

* `stat` - The name of the stat the reward should level, use the default English names like `strength` (Required)
* `value` - The amount of the stat to level, defaults to 1 (Optional)
* `format` - Configures the decimal format for the value displayed in menus and level up messages. Uses the Java DecimalFormat pattern.

Example:

<pre class="language-yaml"><code class="lang-yaml"><strong>- type: stat
</strong>  stat: strength
  value: 1
</code></pre>

::: info
**Want to remove the default stat rewards?** You can do so by making the patterns section empty using `patterns: []`. However, you may need to adjust menu message newlines in the messages file to remove extra spaces when there are no stat rewards.
:::

### Command rewards (`command`)

Command rewards allow you to execute any command, either through console or the player.

Keys:

* `command` - The command to execute; do not include the beginning slash. (Required)
  * Supported placeholders:
    * {player} - The player's name
    * {level} - The level of the skill that the player just leveled up to
    * {skill} - The name of skill that was leveled up
    * All PlaceholderAPI placeholders (requires the PlaceholderAPI plugin installed). Built-in placeholders are applied before PlaceholderAPI placeholders so you can combine them together.
  * Color codes work using "&" unless escaped using "\\&"
* `executor` - Defines who executes the command, either `console` or `player`. Using a player executor may still take into account permissions. Defaults to console. (Optional)
* `revert_command` - Revert commands allow you to execute a command when a player loses the skill level due to admin commands. Placeholders work the same as the normal command value. (Optional)
* `revert_executor` - Defines who executes the revert command, `console` or `player` (Optional)

Example:

```yaml
- type: command
  executor: console
  command: say leveled up!
  revert_executor: console
  revert_command: say removed level up
```

### Permission rewards (`permission`)

Permission rewards grant a permission node to a player. Permission rewards are better than using command rewards to add permissions because:

1. Permissions are automatically removed when the player loses the level
2. Missing permissions are automatically granted if you added the reward to a level below what a player already has (applies on player join)

Permission rewards currently only work if you use [LuckPerms](https://www.spigotmc.org/resources/luckperms.28140) as your permissions plugin.

Keys:

* `permission` - The permission node to give (Required)
* `value` - The value of the permission, either `true` or `false`, but defaults to `true` (Optional)

Example:

```yaml
- type: permission
  permission: some.permission.node
  value: true
```

### Item rewards (`item`)

Item rewards give an item to the player, supporting any item through a key and registry system. Items must first be registered to a unique key in-game using `/skills item register [key]` and holding the item you want to register in your main hand. Items can be unregistered using `/skills item unregister [key]`. When a player's inventory is full, they will be notified and the item will be added to the player's unclaimed items. They can then claim the items using `/skills claimitems`. Unclaimed items are saved between restarts.

Keys:

* `key` - The key of the item that was registered using `/skills item register [key]` (Required)
* `amount` - The amount of that item to give, can exceed max stack size. If the amount exceeds the maximum stack size of an item, the given items will still follow the item's max stack size but will just be divided into multiple stacks. If items can't fit into the player's inventory they will be added to unclaimed items like normal. The amount defaults to the original amount of the item registered. (Optional)

Example:

```yaml
- type: item
  key: some_item_key
  amount: 24
```

### Money rewards (`money`)

Money rewards simply give money on level up through Vault Economy.

Keys:

* `amount` - The amount of money to give
* `formula` - An expression for determining the amount of money to give for a given `level` variable. Cannot be used with `amount`.

Example:

```yaml
- type: money
  amount: 1000
```

Example patterns section in for replacing legacy money rewards:

```yaml
patterns:
  - type: money
    formula: '100+10*level*level'
    pattern:
      interval: 1
```

If you don't use Vault or have special currencies, you can still give money using command rewards. Here is an example:

```yaml
- type: command
  executor: console
  command: eco give {player} %math_{level}*2+100%
```

## Messages

Command, permission, and item rewards support custom menu and chat messages. The `menu_message` key is for the message that will show on the level item in the level progression menu (accessible by clicking any skill in the /skills menu). This will appear under the "Rewards:" section right after stat rewards and before ability unlock/level ups. The `chat_message` key is for the message that will show on the list of rewards in chat when the player levels up. If the `menu_message` and `chat_message` you want are the same, you can simply use `message` instead as a shorthand.

The value of a message can either be a direct string (exactly what you put) or the path of a message key in the messages file. The plugin will first try to replace a message key and if not found will just use the direct string. Messages also support color codes using "&".

Note: Messages do not include new lines by default, you must use \n at the beginning if you want a new line for each reward.

Example of a menu and chat message being defined:

```yaml
- type: permission
  permission: some.permission.node
  value: true 
  menu_message: \n  Some message in the menu
  chat_message: \n  Some message in chat
```

Example using the message shorthand for both menu and chat messages:

```yaml
- type: command
  executor: console
  command: say leveled up!
  message: \n  Both a menu and chat message
```

### Default and automatically handled reward messages

Item rewards by default use the display name or localized name of the item in the format of the `rewards.item` section's message keys in the messages file. If the item does not have a display name, it will not have any messages by default.

Stat reward messages are automatically handled because it always uses the `menus.level_progression_menu.rewards_entry` message key for menu messages and the `leveler.stat_level` message key for chat messages. You should edit those values in the messages file to change what stat reward messages look like.

Money reward messages are automatically handled through the `menus.level_progression_menu.money_reward` message key for menu messages and the `leveler.money_reward` message key for chat messages. All money rewards amounts for a particular level are added together into a single message.
