---
description: Frequently-asked questions
---

# FAQ

### How do I remove the health and mana display above the hotbar?

Set `action_bar.idle` to false in config.yml.

### How do I prevent duplicated XP and items from placed blocks?

This shouldn't happen by default, but make sure the following config options are set properly:

1. In the `sources` folder, ensure that in the `mining.yml`, `excavation.yml`, and `foraging.yml` files, the option `default.check_replace` is set to true.
2. In `config.yml`, ensure `check_block_replace.enabled` is true. Also make sure your world names are **not** added to the blocked\_worlds list.
3. In `config.yml`, ensure `hooks.WorldGuard.blocked_check_replace_regions` does **not** contain your world name.

### How do I use mana or disable mana?

See [Mana Abilities](mana-abilities.md) or [disabling mana](mana-abilities.md#disabling-mana).

### Why does it say "Player does not have a Skills profile!" when trying to open the skills menu? / Why are the items in the skills menu missing?

Restart the server. You likely reloaded the server, which is not supported and will break things.

### Will you ever support 1.8?

No. Too much would have to be re-coded and features would be cut.

### Is there a developer API?

Yes, see [API](api.md).

### How do I uninstall the plugin and remove extra hearts

To uninstall, put the plugin back and run `skills resethealth` in the console when no players are online. Then immediately remove the plugin and restart.

### Why do I not have the right amount of extra hearts on my screen when my HP is high enough?

Visual hearts are scaled, meaning it takes more HP to get each additional heart. Your actual health matches the HP value on the action bar. This is enabled to prevent hearts from blocking the screen at high health stats. You can disable this by setting `health.health-scaling` to false in config.yml

### How do I migrate from YAML file to MySQL database storage?

See the [SQL page](main-config/sql.md#migrating-data-from-yaml) for migration steps.

### How do I add a skill XP multiplier?

You just need to give a player or group the `auraskills.multiplier.[percent]` permission, where percent is the percentage more XP that should be received. For example, adding a 2x multiplier to everyone using LuckPerms would be `/lp group default permission set auraskills.multiplier.100`.

See the [XP Multipliers page](skills/xp-multipliers.md) for more details.

### What server software is supported?

Only Spigot and Paper are officially supported, but many Paper forks like Purpur will likely work. CraftBukkit and modded hybrid servers (Arclight, Mohist, etc) are not supported.

### Why am I not appearing on the leaderboard? / How do I exclude op players from the leaderboard?

The `auraskills.leaderboard.exclude` permission controls whether a player is shown on the leaderboard. This is `false` by default, but if a player has `auraskills.*` then that player will be excluded. Simply set this permission individually to `false` (want the player to show) or `true` (do not want the player to show.
