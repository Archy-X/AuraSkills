---
description: Plugins that AuraSkills supports or support AuraSkills
---

# Compatible Plugins

## Introduction

This is a quick overview about the plugins that are compatible with AuraSkills. There are three main categories:

* Plugins that are supported by AuraSkills itself
* Plugins that hook into AuraSkills through its API
* Add-ons that are made specifically to work AuraSkills or extend its functionality

::: info
There may be even more plugins that are compatible with AuraSkills. These are just the ones that we know of.
:::

## Plugins that AuraSkills hooks into

### Vault

Enables [money rewards](rewards.md#money-rewards-money) and the [jobs](main-config/#jobs) feature. You will need an economy provider as well that hooks into Vault, like EssentialsX or CMI (or any other economy plugin).

### WorldGuard

Custom flags to control where can players gain XP. Also used to check whether a player can break/place blocks at a given location or not. You can setup regions to be ignored by the block tracker and/or you can tell the plugin in what regions should XP gaining totally prevented.

See the [main config WorldGuard section](main-config/#worldguard) for a list of options.

### LuckPerms

Grant [permission as rewards](rewards.md#permission-rewards-permission). Optionally it can improve permission multiplier lookup performance which is enabled by default and in most cases you shouldn't disable it. If you are having issues like incorrect multiplier calculations, then turn this off by setting `hooks.LuckPerms.use_permission_cache` to `false` in the main config file.

### PlaceholderAPI

AuraSkills provides a lot of placeholders through PlaceholderAPI. You can view the full list [here](placeholders.md).

### DecentHolograms and HolographicDisplays

[Damage indicator holograms](main-config/#damage-holograms) when you hit an entity.

### ProtocolLib

Allows the plugin to detect when other plugins try to use the action bar. When this happens, AuraSkills will pause its own action bar so other plugins can show messages there is well.

### Towny

Terraform ability won't break blocks in other people's towns.

### MythicMobs

Experience can be given with the `giveSkillXP` mechanic that AuraSkills provides. Lets say you have boss and you want to give fighting skill XP for the player who kills the boss. You can do something like this in your mob Skills config section:

```yaml
Skills:
  # xp argument supports placeholder math.
  # s (skill) argument can be any AuraSkills skill namespaced ID.
  - giveSkillXP{s=fighting;xp=1000} @trigger ~onDeath
```

You can create insanely powerful things with this using MythicMobs' scripting language.

We can't provide more support than this. If you are not sure about how Mythic triggers/targeters or conditions work, please learn about them in their [official wiki](https://git.mythiccraft.io/mythiccraft/MythicMobs/-/wikis/home) or ask for support on their Discord server.

### MythicCrucible

When you are using MythicCrucible as your primary item plugin, you could use the `takeMana` mechanic and the `hasMana` condition, that AuraSkills provides to use in mythic scripting language.

Example of creating a damage skill that uses mana:

```yaml
DamageWithMana:
  Cooldown: 1
  Conditions:
  - hasMana{m=20} true
  Skills:
  - takeMana{m=20}
  - damage{a=20}
```

With this skill the player will damage 20 if the player has 20 mana. 20 mana will be taken from the player.&#x20;

You can use this skill on an item like this:

```yaml
Skills:
  - skill{s=DamageWithMana} @target ~onInteract
```

This skill will damage the current target when the player right clicks with the item.

We can't provide more support than this. If you are not sure about how Mythic triggers/targeters or conditions work, please learn about them in their [official wiki](https://git.mythiccraft.io/mythiccraft/MythicMobs/-/wikis/home) or ask for support on their Discord server.

### Nexo

#### Items

Nexo items can be easily used as loot, source menu items, and as any menu item.

Example loot:

```yaml
loot:
- type: item
  key: nexo:your_item_id
```

Example source menu item:

```yaml
source_name:
  menu_item: nexo:your_item_id
```

Example menu item:

```yaml
some_menu_item:
  key: nexo:your_item_id
```

#### Custom blocks

Nexo custom blocks can be easily added as block sources to gain XP in skills such as Farming, Mining, etc.:

```yaml
source_name:
  block: nexo:your_block_id
  xp: 5
```

Nexo blocks will correctly register as placed blocks to prevent XP dupes. Luck traits (Farming Luck, etc.) will work for Nexo blocks added
as sources.

### Slimefun

Detect and add block to placed blocks from Slimefuns Block placer to prevent XP dupes.

## Known plugins that hook into AuraSkills

### [MMOItems](https://www.spigotmc.org/resources/mmoitems.39267/)

You can set your `preferred_rpg_provder` in MMOItems to `AURA_SKILLS`. This way you can create items that are using mana from this plugin. You can also make your items give AuraSkills stats and mana to players, or restrict their usage by skill levels. For a more detailed guide on how to set this up please refer to the official [MMOItems wiki](https://gitlab.com/phoenix-dvpmt/mmoitems/-/wikis/Supported%20Plugins#aureliumskills).

### [CustomFishing](https://polymart.org/resource/customfishing.2723)

You can give skill xp when fishing up something in CustomFishing and you can buff fishing rods and minigames based on skill levels or by using other AuraSkills placeholders.

Here is a **minimal** example how to give fishing skill XP when you catch a fish:

```yaml
tuna_fish_golden_star:
  # ...
  events:
    success:
      aurelium_xp:
        type: plugin-exp
        value:
          plugin: AuraSkills
          exp: 400
          target: fishing
```

For additional information check out CustomFishings wiki and their Discord support.&#x20;

### [Eco plugins](https://polymart.org/user/auxilor.1107)

Eco plugins are using the Libreforge yaml scripting language to create effects, conditions and other thing in every Eco plugin like EcoItems, EcoPets, Talismans etc.

Eco provides some useful effects and conditions to integrate with AuraSkills:

* [add\_stat](https://plugins.auxilor.io/effects/all-effects/add_stat#permanent-effect)
* [skill\_xp\_multiplier](https://plugins.auxilor.io/effects/all-effects/skill_xp_multiplier#permanent-effect)
* [mana\_cost](https://plugins.auxilor.io/effects/configuring-an-effect#mana_cost)
* [has\_mana](https://plugins.auxilor.io/effects/all-conditions/has_mana)
* [has\_skill\_level](https://plugins.auxilor.io/effects/all-conditions/has_skill_level)

### [BeeMinions](https://polymart.org/resource/beeminions.6048)

These minions can give skill XP after they produced some products. There isn't really any documentation about it at the time of writing but you should look around filters.

### [Aurora plugins](https://modrinth.com/user/erik_sz)

Aurora plugins, like AuroraLevels, AuroraCollections, and AuroraQuests are hooking into AuraSkills to provide auto-correcting stat rewards. This is useful if you want to spice up your RPG experience while you don't have to worry about nerfing/buffing the stat rewards later on.&#x20;

### [TreeAssist](https://www.spigotmc.org/resources/treeassist.67436/)

Can give AuraSkills foraging skill XP when chopping down trees.

### [ExecutableItems](https://www.spigotmc.org/resources/83070/)

Can require mana to let players use the items. Use [requiredMana](https://docs.ssomar.com/executableitems/configurations/activator-configuration/activators-features#requiredmana) for this to work.

### [UltraBoomerangs](https://www.spigotmc.org/resources/ultraboomerangs-create-custom-unqiue-boomerangs-mcmmo-auraskills-support.113150/)

Earn AuraSkills XP when killing mobs with a boomerang.

## Official add-ons

### [AuraMobs](https://www.spigotmc.org/resources/auramobs-a-mob-levels-add-on-for-auraskills.94168/)

Level your mobs based on player sum/average skill levels to make them harder to defeat. Increase their health and attack damage to complete the true RPG experience AuraSkills can provide. Fully integrated with AuraSkills XP gain and mob drop tables.&#x20;

## Unofficial add-ons

### [Level restrict](https://github.com/Fly1337/AuraSkillsLevelRestrict/releases)

By using this addon, you can prevent block breaking until the player reaches a specific skill level. This behavior is highly configurable.&#x20;

### [CannonsRPG](https://hangar.papermc.io/Intybyte/CannonsRPG)

A Cannons and AuraSkills addon that allows players to level up and unlock skills related to firing cannons.

### [Random Enchanted Rewards](https://modrinth.com/plugin/random-enchanted-rewards-for-auraskills)

Rewards players with randomly enchanted items as they level up their skills.

### [SkillTime](https://github.com/Intybyte/SkillTime/releases)

Define time ranges in which some skills will earn more/less XP.
