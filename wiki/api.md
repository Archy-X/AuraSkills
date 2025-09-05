---
description: Guide to the AuraSkills API
---

# API

The AuraSkills API allows developers to interact with the plugin, integrate with an existing plugin, or register content (custom skills, stats, abilities).

The API consists of just the `api` and `api-bukkit` sub-modules. This allows API methods to be stable across versions and prevents the use of unstable internal methods.

Javadocs are available [here](https://aurelium.dev/javadocs/auraskills-api-bukkit/).

## Getting Started

Release versions are published to the Maven central repository.

### Maven

```xml
<dependency>
    <groupId>dev.aurelium</groupId>
    <artifactId>auraskills-api-bukkit</artifactId>
    <version>2.2.4</version>
    <scope>provided</scope>
</dependency>
```

### Gradle

Groovy DSL:

```groovy
repositories {
     mavenCentral()
}

dependencies {
     compileOnly 'dev.aurelium:auraskills-api-bukkit:2.2.4'
}
```

Kotlin DSL:

```groovy
repositories {
     mavenCentral()
}

dependencies {
     compileOnly("dev.aurelium:auraskills-api-bukkit:2.2.4")
}
```

## Getting the API instance

Most API classes are accessible through the AuraSkillsApi interface accessed using the following static method.

```java
AuraSkillsApi auraSkills = AuraSkillsApi.get();
```

In very limited situations where the API requires Bukkit classes, such as the ItemManager and Regions API, you will have to obtain an instance of the AuraSkillsBukkit interface:

```java
AuraSkillsBukkit auraSkillsBukkit = AuraSkillsBukkit.get();
```

::: warning
Any use of the `auraSkills` variable name below refers to the `AuraSkillsApi` instance.
:::

## Interacting with players

Player skill information is available through the `SkillsUser` interface, which is obtained from the API instance:

```java
SkillsUser user = auraSkills.getUser(player.getUniqueId());
```

While the method accepts a uuid, only online players will have their skill data loaded. Offline players will still return a `SkillsUser` object, but will have all values set to default ones and modifying values will not work.

### Skills

You can get and set a user's skill level using the user methods and the `Skills` enum for default skills. For example:

```java
// Gets the user's Farming skill level. Use the Skills enum for all default skills.
int level = user.getSkillLevel(Skills.FARMING);

// Set the Fighting skill to level 10
user.setSkillLevel(Skills.FIGHTING, 10); 
```

Getting and adding skill XP is very similar:

```java
double xp = user.getSkillXp(Skills.FARMING);

user.addSkillXp(Skills.FARMING, 20.0);

user.addSkillXpRaw(Skills.FARMING, 15.0); // Ignores any XP multipliers

// Sets XP to 0, resetting progress for only the current skill level
user.setSkillXp(Skills.FARMING, 0.0);
```

### Stats

Getting a player's stat level is simple using the method and the `Stats` enum.

```java
// Gets the user's strength stat level. Use the Stats enum for all default stats.
double level = user.getStatLevel(Stats.STRENGTH);

// Gets the stat level only from permanent skill rewards (without modifiers).
double baseLevel = user.getBaseStatLevel(Stats.HEALTH);
```

Adding stat levels is more complex, since stat levels aren't stored directly. Instead, there a system of stat modifiers which associates a unique name to a stat increase so it can be tracked and removed later.

```java
// Adding a Wisdom stat modifier of value 10. The String argument should be a unique
// name that identifies the purpose of the modifier.
user.addStatModifier(new StatModifier("my_modifier_name", Stats.WISDOM, 10.0));

// Remove the modifier with just the name
user.removeStatModifier("my_modifier_name");
```

### Mana

Getting and setting mana is very simple:

```java
double mana = user.getMana();

// Check that the user has enough mana before removing it
if (mana >= 10.0) {
    user.setMana(mana - 10.0);
} else {
    // Handle not enough mana case
}

// Gets the user's max mana (determined by the Wisdom stat)
double maxMana = user.getMaxMana();
```

An easier way to consume mana for things like activating mana abilities or casting spells is using the `consumeMana` method. This returns true if the user has enough mana and mana was successfully removed, and false if mana could not be removed. It also automatically sends a "Not enough mana" message to the action bar.

```java
if (user.consumeMana(15.0)) {
    // You have to send a success message yourself
    // Implement mana ability functionality here
}
```

## Events

The API has many events to interact with the plugin. These are registered just like regular Bukkit events.

### List

The following is a list of available events. This may not always be up-to-date, so see the source code for all the events.

* `LootDropEvent` - Calls when the plugin drops/modifies item loot. Includes both multiplier abilities and Fishing/Excavation loot tables. Use `getCause()` to get the reason for the drop.
* `SkillsLoadEvent` - Calls when the plugin has fully finished loading, usually on the first tick of the server. Use this event when accessing anything related to skills, stats, etc on server startup instead of in `onEnable`, since skills won't be loaded yet then.
* `ManaAbilityActivateEvent` - Calls when a player activates a mana ability.
* `ManaAbilityRefreshEvent` - Calls when a player is able to use a mana ability again (cooldown reaches 0)
* `ManaRegenerateEvent` - Calls when a player regenerates mana naturally.
* `SkillLevelUpEvent` - Calls when a player levels up a skill.
* `XpGainEvent` - Calls when a player gains skill XP.
* `CustomRegenEvent` - Calls when a player regenerates health if custom regen mechanics are enabled.

## Global Registry

The global registry is used to get any Skill, Stat, Ability, etc instance by its NamespacedId. This allows you to parse content from a name that supports custom content. Use `AuraSkillsApi#getGlobalRegistry` to get the registry. This is a read-only interface, to register your own custom content, see [#custom-content](api.md#custom-content "mention").

```java
GlobalRegistry registry = auraSkills.getGlobalRegistry();

Skill skill = registry.getSkill(NamespacedId.of("pluginname", "skillname");
// To get default skills, using the Skills enum is much easier
```

## Custom content

To register custom content, you must first obtain a NamespacedRegistry that identifies your plugin and the directory from which to load content files (including skills.yml, rewards, sources, etc).

```java
AuraSkillsApi auraSkills = AuraSkillsApi.get(); // Get the API instance

// Getting the NamespacedRegistry with your plugin name and the plugin's folder
NamespacedRegistry registry = auraSkills.useRegistry("pluginname", getDataFolder());
```

The first argument of `useRegistry` is the name of your plugin registering the custom content (it will be forced to lowercase). This is the namespace part of all content that is identified by a NamespacedId, and is used in config files like skills.yml, abilities.yml, etc.

### Content directory

The second argument of `useRegistry` is a Java File object representing the content directory. Some things like sources and rewards cannot be registered through code alone and must be done through config files that are automatically detected and loaded from the content directory. A good choice is just the plugin's data folder, which is accessed from `Plugin#getDataFolder()`.

The format for content files is the same as the main AuraSkills plugin, as your content files are merged together with the main plugin's config files when loading skills, stats, and abilities. For example, you can reference `auraskills/` namespaced abilities in your skills.yml file, and `yourplugin/` namespaced abilities can be used in the AuraSkills skills.yml file.

### Stats

To create a custom stat, use the `CustomStat.builder` static method to get an instance of  `CustomStatBuilder`. You must pass in a `NamespacedId` using `NamespacedId.of` to identify the stat. Then register the stat with the `NamespacedRegistry`.

Stats on their own don't represent any game mechanics or attributes. Every stat must also have at least one trait that actually implements functionality. Use the `CustomTrait.builder` to get a `CustomTraitBuilder` to create a trait. This `CustomTrait` is then passed into `CustomStatBuilder#trait` to link it to the stat. You can also pass in a default trait from the `Traits` enum if you want to simply split up stats.

The example below shows creating a new dexterity stat with a dodge chance trait adding functionality to evade attacks. We create new classes and assign the trait and stat to static constants for easier access.

```java
public class CustomTraits {
    
    public static final CustomTrait DODGE_CHANCE = CustomTrait
            .builder(NamespacedId.of("pluginname", "dodge_chance")
            .displayName("Dodge Chance")
            .build();
        
}

public class CustomStats {

    public static final CustomStat DEXTERITY = CustomStat
            .builder(NamespacedId.of("pluginname", "dexterity")
            .trait(CustomTraits.DODGE_CHANCE, 0.5) // Dodge chance will increase by 0.5 per dexterity level
            .displayName("Dexterity")
            .description("Dexterity increases the chance to dodge attacks.")
            .color("<green>")
            .symbol("")
            .item(ItemContext.builder()
                    .material("lime_stained_glass_pane")
                    .group("lower") // A group defined in AuraSkills/menus/stats.yml
                    .order(2) // The position within that group
                    .build())
            .build();

}
```

You must then register the CustomTrait and CustomStat in your plugin's onEnable:

```java
AuraSkillsApi auraSkills = AuraSkillsApi.get();

NamespacedRegistry registry = auraSkills.useRegistry("pluginnanme", getDataFolder());

registry.registerTrait(CustomTraits.DODGE_CHANCE);
registry.registerStat(CustomStats.DEXTERITY);
```

::: warning
Replace "pluginname" with the name of your plugin in lowercase. The name passed into `NamespacedId.of` must match the one passed in `AuraSkillsApi#useRegistry`.
:::

#### Trait handlers

The above code only registers the existence and display elements of the stat without any gameplay functionality. If your stat only uses existing traits, this is all you need. However, if are creating a new trait like the example above, you must add additional code to actually implement your trait's functionality.

To implement your trait functionality, create a new class that implements `BukkitTraitHandler`:

```java
public class DodgeChanceTrait implements BukkitTraitHandler, Listener {

    private final AuraSkillsApi auraSkills;
    
    // Inject API dependency in constructor
    public DodgeChanceTrait(AuraSkillsApi auraSkills) {
        this.auraSkills = auraSkills;
    }

    @Override
    public Trait[] getTraits() {
        // An array containing your CustomTrait instance
        return new Trait[] {CustomTraits.DODGE_CHANCE};
    }
    
    @Override
    public double getBaseLevel(Player player, Trait trait) {
        // The base value of your trait when its stat is at level 0, could be a
        // Minecraft default value or values from other plugins
        return 0;
    }
    
    @Override
    public void onReload(Player player, SkillsUser user, Trait trait) {
        // Method called when the value of the trait's parent stat changes
    }
    
    // Example implementation of the trait's functionality (not complete)
    @EventHandler(ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceOf Player)) return;
        
        Player player = event.getEntity();
        SkillsUser user = auraSkills.getUser(player.getUniqueId());
        
        // Gets the user's trait level
        double dodgeChance = user.getEffectiveTraitLevel(Traits.DODGE_CHANCE);
        
        if (ThreadLocalRandom.current().nextDouble() < dodgeChance) {
            // Dodge activated
            event.setCancelled(true);
            player.sendMessage("Dodged attack");
        }
    }

}
```

Finally, register your `BukkitTraitHandler` in onEnable:

```java
AuraSkillsApi auraSkills = AuraSkillsApi.get();

auraSkills.getHandlers().registerTraitHandler(new DodgeChanceTrait(auraSkills));
```

::: info
Calling registerTraitHandler automatically registers any Bukkit events if the class implements Listener.
:::

#### Configuration

To make your stat and trait configurable by users of your plugin, create a `stats.yml` file in your plugin's [content directory](api.md#content-directory) that you linked to the `NamespacedRegistry` used to register your traits/stats.

Here is an example `stats.yml` for the Dexterity/Dodge Chance example above:

```yaml
stats:
  pluginnamne/dexterity:
    enabled: true
    traits:
      pluginname/dodge_chance:
        modifier: 0.5 # Overrides the value passed in with CustomStatBuilder#trait
traits:
  pluginname/dodge_chance:
    enabled: true
    # Add configurable options here accessed using Trait#option... methods  
```

::: warning
You are responsible for generating the `stats.yml` file in the user's folder using `Plugin#saveResource`.
:::

### Skills

To create a custom skill, get an instance of the builder using `CustomSkill.builder` and pass in a `NamespacedId` using `NamespacedId.of`. Then register the skill with the `NamspacedRegistry`.

The following is an example for creating a Trading skill that gives XP when trading with villagers. We create a new `CustomSkills` class with a static constant to hold the reference to the skill instance for easier access.

```java
public class CustomSkills {

    public static final CustomSkill TRADING = CustomSkill
            .builder(NamespacedId.of("pluginname", "trading"))
            .displayName("Trading")
            .description("Trade with villagers to gain Trading XP")
            .item(ItemContext.builder()
                    .material("emerald")
                    .pos("4,4")
                    .build())
            .build();
    
}
```

Then, register the skill in your plugin's onEnable:

```java
AuraSkillsApi auraSkills = AuraSkillsApi.get();

NamespacedRegistry registry = auraSkills.useRegistry("pluginnanme", getDataFolder());

registry.registerSkill(CustomSkills.TRADING);
```

#### Adding XP sources

To add ways to gain XP for a custom skill, create a sources file with the same format as the AuraSkills sources files. For our example, create a file at `sources/trading.yml`.

If your custom skill's XP sources use existing source types available in default skills (such as breaking blocks, killing a mob, tracking a statistic, etc), then you just need to add configured sources of the existing types to the file. See [Sources](sources.md) for how to add existing source types.

In our case, there is no existing source type that handles trading with villagers, so we will need to create our own using the API.

First, create a class that extends `CustomSource` to hold the configurable data of your source. In this case for simplicty, our only parameter will be the number of emeralds transacted in the trade, so we just need to add a configurable multiplier for this value. Keep the SourceValues constructor argument in your constructor and add your parameters to the end of it.

```java
public class TradingSource extends CustomSource {

    private final double multiplier;
    
    public TradingSource(SourceValues values, double multiplier) {
        super(values);
        this.multiplier = multiplier;
    }
    
    public double getMultiplier() {
        return multiplier;
    }

}
```

Then, you need to register your source type and create a parser to deserialize the source from configuration. Use `NamespacedRegistry#registerSourceType` to register the name and parser of your source.&#x20;

The first argument, name, is a lowercase name for your source used to construct its NamspacedId used as the `type` key in the sources config. If you pass in `trading` as the name, you would use `type: pluginname/trading` in the sources config.

The second argument accepts an instance of `XpSourceParser` where the type parameter is your `CustomSource` class. You can create a new class, or use a lambda like in this example:

```java
SourceType trading = registry.registerSourceType("trading", (XpSourceParser<TradingSource>) (source, context) -> {
    double multiplier = source.node("multiplier").getDouble(1.0);
    return new TradingSource(context.parseValues(source), multiplier);
}
```

The `source` argument of the lambda is a `ConfigurationNode` from [Configurate](https://github.com/SpongePowered/Configurate/wiki/Node) that contains the keys and values from the configuration section of the source to load. The `context` argument is a `SourceContext` that contains useful parsing methods for enforcing required keys or getting pluralized values. You also need to use `context.parseValues(source)` to get the `SourceValues` object to pass as the first argument of your source type constructor. This parses things like the name, xp, and display\_name of the source for you, so you only need to implement parsing of your custom options.

To make your source actually give XP, you need to implement a leveler that listens to the proper Bukkit events or implements some other mechanism for gaining XP. You can create an instance of the `LevelerContext` class to help you create a leveler. It contains useful methods like checking blocked locations and players. To create an instance, you need to pass an instance of the API and the `SourceType` returned when you registered it.

After you register the source type, you still have to create a sources file for your skill to actually define the XP source. In this example, we create a `sources/trading.yml` file:

```yaml
sources:
  trade:
    type: pluginname/trading
    multiplier: 5
```

::: warning
You are responsible for generating the sources file in the user's folder using `Plugin#saveResource`.
:::

#### Adding rewards

Rewards are added by creating a rewards file in the same format as the AuraSkills rewards files. For this example, create a file at `rewards/trading.yml`. We make Trading give the Luck stat every skill level and the Dexterity stat created above every two levels:

```yaml
patterns:
  - type: stat
    stat: luck
    value: 1
    pattern:
      interval: 1
  - type: stat
    stat: pluginname/dexterity
    value: 1
    pattern:
      interval: 2
```

### Abilities

To add a custom ability, use the `CustomAbility.builder` static method to get an instance of `CustomAbilityBuilder`. This example creates an ability called Magic Archer that gives additional Archery XP when using spectral or tipped arrows. We create a new class to hold static constant references to our abilities for easier access.

```java
public class CustomAbilities {

    public static final CustomAbility MAGIC_ARCHER = CustomAbility
            .builder(NamespacedId.of("pluginname", "magic_archer")
            .displayName("Magic Archer")
            .description("Gain {value}% more XP when using spectral or tipped arrows.")
            .info("+{value}% Special Arrow XP ")
            .baseValue(20) // Value when at level 1
            .valuePerLevel(10) // Value added per ability level
            .unlock(6) // Skill level ability unlocks at
            .levelUp(5) // Skill level interval between ability level ups
            .maxLevel(0) // 0 = unlimited max level, but capper by the max skill level
            .build();          

}
```

Then, register your `CustomAbility` using the `NamespacedRegistry` in your plugin's onEnable.

```java
AuraSkillsApi auraSkills = AuraSkillsApi.get();

NamespacedRegistry registry = auraSkills.useRegistry("pluginnanme", getDataFolder());

registry.registerAbility(CustomAbilities.MAGIC_ARCHER);
```

You now need to actually implement your ability's functionality. This will vary depending on the mechanics of your ability, but will typically follow this general flow:

* Listen to some Bukkit event
* Perform some checks confirming the ability can actually be used. You can create an instance of the`AbilityContext` class and use methods like `isDisabled` and `failsChecks` to return early.
* [Get the `SkillsUser` object of the player](api.md#interacting-with-players)
* Get the player's ability value using `Ability#getValue(SkillsUser#getAbilityLevel)`
* Use the value to modify gameplay mechanics

The implementation of the example ability won't be shown, but it would be done by listening to the `EntityXpGainEvent` and using `setAmount` to change the XP gained.

#### Adding to a skill

For your ability to work, it must be linked to a skill. You can do this by calling the `ability` or `abilities` methods on `CustomSkillBuilder` when building your `CustomSkill`. You can also add it to the `abilities` list of a skill in any `skills.yml` configuration file, including for existing default skills. Make sure to use the full NamespacedId to reference your ability in the config in the format `pluginname/abilityname`.

#### Configuration

The example above hardcoded the values for baseValue, unlock, levelUp, etc. If you want to make these values configurable by the user, create an `abilities.yml` file in your content directory. The format is the same as the AuraSkills file.

```yaml
abilities:
  pluginname/magic_archer:
    enabled: true
    base_value: 15
    value_per_level: 10
    unlock: 6
    level_up: 5
    max_level: 0
```

::: warning
You are responsible for generating `abilities.yml` in your user's plugin folder using `Plugin#saveResource`.
:::

### Mana Abilities

Adding a custom mana ability is similar to creating a normal ability. Use the builder to create a `CustomManaAbility` instance. This example creates a mana ability called Leap for Agility that launches the player forward when right clicking a feather. We create a new class to hold a static constant reference to our mana abillity for easier access.

```java
public class CustomManaAbilities {

    public static final CustomManaAbility LEAP = CustomManaAbility
            .builder(NamespacedId.of("pluginname", "leap"))
            .displayName("Leap")
            .description("Instantly launch yourself forward [Right click feather to activate]")
            .build();

}
```

To define the values for your mana ability like base\_value, value\_per\_level, base\_cooldown, base\_mana\_cost, etc., you can either set them directly using the builder methods or create a `mana_abilities.yml` file in your content directory in the same format as the AuraSkills file, but with the mana ability name being your mana ability's NamespacedId of course. You should define all the necessary options using one of the two methods, otherwise they will use arbitrary default values.

Make sure to register your `CustomManaAbility` in your plugin's onEnable.

```java
AuraSkillsApi auraSkills = AuraSkillsApi.get();

NamespacedRegistry registry = auraSkills.useRegistry("pluginnanme", getDataFolder());

registry.registerManaAbility(CustomManaAbilities.LEAP);
```

You must then implement the functionality for your mana ability yourself using Bukkit events and other API calls to get the mana ability values. For example, to get the value for a user, get the mana ability level using `SkillsUser#getManaAbilityLevel` and pass it into `ManaAbility#getValue`:

```java
SkillsUser user = auraSkills.getUser(player.getUniqueId());
ManaAbility manaAbility = CustomManaAbilities.LEAP;
int level = user.getManaAbilityLevel(manaAbility);

double value = manaAbility.getValue(level);
double cooldown = manaAbility.getCooldown(level);
double manaCost = manaAbility.getManaCost(level);
```
