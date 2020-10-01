package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.skills.abilities.mana_abilities.MAbility;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.ManaAbilityOption;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class AbilityOptionManager {

    private final Map<Ability, AbilityOption> abilityOptions;
    private final Map<MAbility, ManaAbilityOption> manaAbilityOptions;
    private final Plugin plugin;

    public AbilityOptionManager(Plugin plugin) {
        this.plugin = plugin;
        abilityOptions = new HashMap<>();
        manaAbilityOptions = new HashMap<>();
    }

    public void loadOptions() {
        FileConfiguration config = plugin.getConfig();
        //Loads ability options
        int amountLoaded = 0;
        int amountDisabled = 0;
        long startTime = System.currentTimeMillis();
        ConfigurationSection abilities = config.getConfigurationSection("abilities");
        if (abilities != null) {
            for (String ability : abilities.getKeys(false)) {
                boolean hasKey = false;
                for (Ability ability1 : Ability.values()) {
                    if (ability.toUpperCase().replace("-", "_").equals(ability1.name())) {
                        hasKey = true;
                        break;
                    }
                }
                if (hasKey) {
                    boolean enabled = config.getBoolean("abilities." + ability + ".enabled", true);
                    if (!enabled) {
                        amountDisabled++;
                    }
                    double baseValue = config.getDouble("abilities." + ability + ".base");
                    double valuePerLevel = config.getDouble("abilities." + ability + ".per-level");
                    AbilityOption option;
                    Ability ab = Ability.valueOf(ability.toUpperCase().replace("-", "_"));
                    //Checks if ability has 2 values
                    if (ab.hasTwoValues()) {
                        double baseValue2 = config.getDouble("abilities." + ability + ".base-2");
                        double valuePerLevel2 = config.getDouble("abilities." + ability + ".per-level-2");
                        option = new AbilityOption(enabled, baseValue, valuePerLevel, baseValue2, valuePerLevel2);
                    }
                    else {
                        option = new AbilityOption(enabled, baseValue, valuePerLevel);
                    }
                    abilityOptions.put(ab, option);
                    amountLoaded++;
                }
            }
        }
        if (config.getConfigurationSection("mana-abilities") != null) {
            ConfigurationSection manaAbilities = config.getConfigurationSection("mana-abilities");
            if (manaAbilities != null) {
                for (String manaAbility : manaAbilities.getKeys(false)){
                    boolean hasKey = false;
                    for (MAbility manaAbility1 : MAbility.values()) {
                        if (manaAbility.toUpperCase().replace("-", "_").equals(manaAbility1.name())) {
                            hasKey = true;
                            break;
                        }
                    }
                    if (hasKey) {
                        boolean enabled = config.getBoolean("mana-abilities." + manaAbility + ".enabled");
                        if (!enabled) {
                            amountDisabled++;
                        }
                        double baseValue = config.getDouble("mana-abilities." + manaAbility + ".base-value");
                        double valuePerLevel = config.getDouble("mana-abilities." + manaAbility + ".value-per-level");
                        int cooldown = config.getInt("mana-abilities." + manaAbility + ".cooldown");
                        int cooldownPerLevel = config.getInt("mana-abilities." + manaAbility + ".cooldown-per-level");
                        int manaCost = config.getInt("mana-abilities." + manaAbility + ".mana-cost");
                        int manaCostPerLevel = config.getInt("mana-abilities." + manaAbility + ".mana-cost-per-level");
                        ManaAbilityOption option = new ManaAbilityOption(enabled, baseValue, valuePerLevel, cooldown, cooldownPerLevel, manaCost, manaCostPerLevel);
                        manaAbilityOptions.put(MAbility.valueOf(manaAbility.toUpperCase().replace("-", "_")), option);
                        amountLoaded++;
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        Bukkit.getLogger().info("[AureliumSkills] Disabled " + amountDisabled + " Abilities");
        Bukkit.getLogger().info("[AureliumSkills] Loaded " + amountLoaded + " Ability Options in " + timeElapsed + "ms");
    }

    public AbilityOption getAbilityOption(Ability ability) {
        return abilityOptions.get(ability);
    }

    public boolean containsOption(Ability ability) {
        return abilityOptions.containsKey(ability);
    }

    public ManaAbilityOption getAbilityOption(MAbility mAbility) {
        return manaAbilityOptions.get(mAbility);
    }

    public boolean containsOption(MAbility mAbility) {
        return manaAbilityOptions.containsKey(mAbility);
    }

    public boolean isEnabled(Ability ability) {
        if (abilityOptions.containsKey(ability)) {
            return abilityOptions.get(ability).isEnabled();
        }
        return true;
    }

    public boolean isEnabled(MAbility mAbility) {
        if (manaAbilityOptions.containsKey(mAbility)) {
            return manaAbilityOptions.get(mAbility).isEnabled();
        }
        return true;
    }
}
