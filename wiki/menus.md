---
description: Guide to configuring menus
---

# Menus

Menus are inventory GUIs that display information about skills to players in-game. The main skills menu is opened with the command `/skills`, with others accessible by clicking buttons in certain menus. The look and style of menus can be configured in the files in the `menus/` folder, while the plain text content is configured through messages.

::: info
The plugin uses the [Slate](../slate/index) library for menus; most of the information on configuring items, templates, and lore can be found there.
:::

## List of menus

The following is a list of menus in the plugin:

<table><thead><tr><th width="186">Menu</th><th>How to access</th><th>Description</th></tr></thead><tbody><tr><td>skills</td><td><code>/skills</code> command.</td><td>Overview and player progress in all skills.</td></tr><tr><td>stats</td><td><code>/stats</code> command or clicking the stats item (Player Head) in the skills menu.</td><td>Overview and player levels in all stats.</td></tr><tr><td>level_progression</td><td>Clicking a skill item in the skills menu or the command corresponding to the name of the skill (<code>/farming</code>, <code>/mining</code>, etc).</td><td>Shows a track of all levels in a skill and their rewards with player progress. Contains items to access other skill-specific menus.</td></tr><tr><td>sources</td><td>Clicking the sources item (Experience Bottle) in the level_progression menu.</td><td>Shows all the ways to earn XP in a certain skill and their XP value.</td></tr><tr><td>abilities</td><td>Clicking the abilities item (Light Blue Dye) in the level_progression menu.</td><td>Shows the abilities and mana abilities of a skill with the player's ability levels.</td></tr><tr><td>leaderboard</td><td>Clicking the rank item (Paper) in the level_progression menu.</td><td>Shows the top 10 players by level in a skill.</td></tr></tbody></table>

## File structure

The files for configuring menus are located in the `AuraSkills/menus` folder. Each menu has its own corresponding file used to configure that menu.

The following is an overview of keys and sections that are at left-most indent of the menu file:

* `title` - The text that will show up at the top of the menu when it is opened
* `size` - The number of rows the menu has, from 1-6 (each row has 9 slots)
* `fill` - A section used to define the menu background item to fill empty slots
* `items` - The main items section used for singular items
* `templates` - The section used to define items with custom contexts and multiple instances
* `components` - Lore snippets used inside some templates/items
* `formats` - Key-value pairs of formats used in some items
* `options` - A section found in some menu files that contains custom options and settings

### Fill

The `fill` section defines a background item to fill empty slots not used by other items. There are two required keys in the `fill` section:

* `enabled` - A true/false value of whether the fill item should be used
* `material` - The material of the fill item

The display name of a fill item is automatically turned into a space, so the name of the item is invisible. Additional keys can be added to customize item meta using the same format as other items.

### Items

> Main article: [Slate/Items](https://wiki.aurelium.dev/slate/items)

The `items` section defines single items in a menu. See the [main article](https://wiki.aurelium.dev/slate/items) for how to configure an item's material, nbt, lore, etc.

### Templates

> Main article: [Slate/Templates](https://wiki.aurelium.dev/slate/templates)

The `templates` section is used for items with a similar layout that appear multiple times in the same menu. See the [main article](https://wiki.aurelium.dev/slate/templates) for how to configure templates.

### Components

> Main article: [Slate/Components](https://wiki.aurelium.dev/slate/components)

The `components` section is used to configure sections of lore used in some templates. See the [main article](https://wiki.aurelium.dev/slate/components) for a full explanation.

### Formats

The `formats` section are key-value pairs used as formatting for lore in other items or templates. Usually, placeholders in items/templates that have square brackets denoting an array like `{entries[]}` use strings from the formats section to populate the array entries. Though the names of the formats don't necessarily match the placeholder, it's name should reflect the item or context it's being used in.

### Options

#### skills:

* `bar_length` - The total number of characters displayed in the XP bar. Also applies to XP bars in the level progression menu.
* `percent_format` - The decimal format for the `{percent}` placeholder in the progress component. Uses the Java DecimalFormat pattern.
* `current_xp_format` - The decimal format for the `{current_xp}` placeholder in the progress component. Uses the Java DecimalFormat pattern.

**level\_progression:**

* `use_level_as_amount` - When true, the level templates (unlocked, in\_progress, and locked) will have their item amount set to the level number they represent (at level 30 the item amount willl be 30). Above the max stack size of the item (usually 64), the value of `over_max_stack_amount` is used.
* `over_max_stack_amount` - The item amount to use for skill level items for levels above the item's maximum stack size. Only applies when `use_level_as_amount` is true.
* `items_per_page` - The number of skill level items (unlocked/in\_progress/locked) to show per menu page.
* `start_level` - The level shown on the first skill level item.
* `track` - A list of slot numbers ranged 0-54 for positioning skill level items. The index of the track matches the index of the skill level item within a specific page, so the length of track should match `items_per_page`.

#### sources:

* `source_start` - The upper left slot position of the rectangle formed by source items.
* `source_end` - The lower right slot position of source items.
* `items_per_page` - The number of source items to show per menu page.
* `use_track` - Whether to use the `track` list to determine source item positions instead of `source_start` and `source_end`.
* `track` - A list of slot numbers ranged 0-54 for positioning source items.
* `xp_format` - The decimal format for the `{source_xp}` placeholders in the source template and multiplied\_xp component. Uses the Java DecimalFormat pattern.

## Colors and formatting

The menu files use the [MiniMessage](https://docs.advntr.dev/minimessage/format.html) format by default. Styles are defined using tags with angle brackets (\<tag>) with optional closing tag (\</tag). The old Bukkit method of styling (such as \&a) is also supported if you want to use it, though MiniMessage is preferred due to its greater features.

The available color constant names are `black`, `dark_blue`, `dark_green`, `dark_aqua`, `dark_red`, `dark_purple`, `gold`, `gray`, `dark_gray`, `blue`, `green`, `aqua`, `red`, `light_purple`, `yellow`, or `white`.

Hex colors can be defined using `<#00ff00>`. See the [MiniMessage docs](https://docs.advntr.dev/minimessage/format.html) for the full format. However, some MiniMessage features like hover and click may not work.

## Placeholders

Placeholders are used in the menu configuration to allow the plugin to insert context-dependent messages and data. There are three general types of placeholders that serve different purposes:

* Single curly braces (`{example}`) - Replaced internally by the plugin for variable data such as player levels and XP. They only work within the same item they were originally defined in (or a component attached to the item), so it should not be moved outside the item.
* Double curly braces (`{{example}}`) - Message placeholders replaced by the message key defined in the messages file by the key `menus.[menu_name].[placeholder]` where `[menu_name]` is the name of the menu and `[placeholder]` is the text within the double curly braces. If this key doesn't exist, it will look at the key `menus.common.[placeholder]` for the message. The player's set language determines which message file the message will come from, allowing the single menu configuration to work with any language.
* PlaceholderAPI placeholders (`%example%`) - Placeholders within percent symbols are PlaceholderAPI placeholders. They can either be AuraSkills placeholders or for any other plugin. The PlaceholderAPI plugin must be installed for them to work (and necessary ecloud expansions for other plugins).

## Custom menus

Custom menus can be created by creating a file in the `menus` folder with a `.yml` extension. The name of the file is the name used to refer to it in commands and other menus. Custom menus use the same file format as the default menus, though the templates, components, formats, and options sections will not work. PlaceholderAPI placeholders and click actions are supported like existing menus.

Custom menus can be opened using the `/sk openmenu` command or through menu open click actions from other menus.&#x20;

An example custom menu named test created at `menus/test.yml`:&#x20;

```yaml
title: Test menu
fill:
  enabled: true
  material: black_stained_glass_pane
items:
  test:
    material: diamond
    display_name: <green>Test item
    lore:
      - <red>Test lore line
      - ' '
      - <yellow>Click to return to skills menu 
    on_click:
      - type: menu
        action: open
        menu: skills
```

This menu can be opened from an existing menu by adding click actions, such as the your\_skills item in the skills menu:

```yaml
items:
  your_skills:
    on_click:
      - type: menu
        action: open
        menu: test
```
