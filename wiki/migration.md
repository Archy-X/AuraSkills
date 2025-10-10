---
description: How to migrate from AureliumSkills Beta 1.3 to AuraSkills 2.0
---

# Migration

With the 2.0 update, the plugin itself has been renamed from AureliumSkills to AuraSkills. This means that the name of the plugin folder is now `/AuraSkills` instead of `/AureliumSkills`.

::: info
Don't know what's new in 2.0? Read the [release notes](release-notes/2.0.md).
:::

While it is recommended to start fresh to get the improved default configs, you can migrate old config options to the new folder automatically. However, not everything will be migrated, so manual work will still be required depending on how much you modified the configs.

If you want to migrate, keep your `/AureliumSkills` folder when running the new jar for the first time, so that when the files in `/AuraSkills` are generated, it can use the files in the old folder to migrate from.

::: danger
Backup your `/AureliumSkills` folder and your database (if using MySQL for data storage) before migrating.
:::

## Should you migrate?

If you are planning on starting a new season/world/server, you do not need to migrate. The new default config options are more balanced and recommended. Only migrate if you want to keep the abilities/stats/xp source values exactly the same as before. Messages and menus won't migrate (see below), so if you only changed those, you will still have to manually change them back.

## What will break

* Compatibilty with plugins that hooked into AureliumSkills before, since the plugin renaming means those plugins will no longer enable their hooks. There are many breaking API changes, so these hooks will have to be recoded anyway. Check with any plugins that hooked into AureliumSkills to see if there is an updated version with AuraSkills support.
* Your previous permissions for the plugin set into plugins like LuckPerms won't work, since permission nodes have been renamed.

## What will not migrate

* Messages (significant format changes)
* Menus (significant format changes)
* PlaceholderAPI placeholders for %aureliumskills\_...% will still work for now, but you should change them to %auraskills\_...% eventually
* Custom XP sources added in the custom section of the old sources\_config.yml
* Items registered in the item registry using `/sk item register` will be reset. You will have to re-register your items for them to work in rewards/loot because of NBT format changes to modifiers.

## What will migrate automatically

* Most options in `config.yml` (skill-specific options have been moved to `skills.yml` and stat-specific options are in `stats.yml`)
* Rewards and loot tables (format mostly unchanged)
* Player data (now in `userdata` folder for YAML, new tables will be created for MySQL)
* XP values in `sources_config.yml` (moved to `sources` folder)
* `xp_requirements.yml` (format unchanged)
* `abilities_config.yml` (separated into `abilities.yml` and `mana_abilities.yml`)
* Item/armor modifiers when held

## Skill merger

In the default configs, the number of skills has been reduced from 15 to 11. However, if you migrate from Beta, you will still have the existing 15 skills and their abilities. But if you want to migrate user data and merge the skills like the new default configs, you will need to do some manual work:

* Delete the skills.yml, abilities.yml, menus folder, rewards folder, and sources folder to let them regenerate. If you modified rewards, abilities\_config.yml, or the sources\_config.yml in Beta, you will have to manually add your changes back.
* Run the following commands in the console which set every user's skill level for the merged skills to the higher out of the two previous skills. Make sure no players are online when you run this.
  * `skills storage mergeskills auraskills/endurance auraskills/agility`
  * `skills storage mergeskills auraskills/healing auraskills/alchemy`
  * `skills storage mergeskills auraskills/forging auraskills/enchanting`
