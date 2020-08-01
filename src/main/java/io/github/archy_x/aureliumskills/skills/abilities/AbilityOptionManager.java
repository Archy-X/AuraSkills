package io.github.archy_x.aureliumskills.skills.abilities;

import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.skills.abilities.mana_abilities.MAbility;
import io.github.archy_x.aureliumskills.skills.abilities.mana_abilities.ManaAbilityOption;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class AbilityOptionManager {

    private Map<Ability, AbilityOption> abilityOptions;
    private Map<MAbility, ManaAbilityOption> manaAbilityOptions;
    private FileConfiguration config;
    private Plugin plugin;

    public AbilityOptionManager(Plugin plugin) {
        this.plugin = plugin;
        abilityOptions = new HashMap<>();
        manaAbilityOptions = new HashMap<>();
    }

    public void loadOptions() {
        config = plugin.getConfig();
        //Loads ability options
        int amountLoaded = 0;
        long startTime = System.currentTimeMillis();
        if (config.getConfigurationSection("abilities") != null) {
            for (String ability : config.getConfigurationSection("abilities").getKeys(false)) {
                boolean hasKey = false;
                for (Ability ability1 : Ability.values()) {
                    if (ability.toUpperCase().replace("-", "_").equals(ability1.name())) {
                        hasKey = true;
                        break;
                    }
                }
                if (hasKey) {
                    double baseValue = config.getDouble("abilities." + ability + ".base");
                    double valuePerLevel = config.getDouble("abilities." + ability + ".per-level");
                    AbilityOption option = new AbilityOption(baseValue, valuePerLevel);
                    abilityOptions.put(Ability.valueOf(ability.toUpperCase().replace("-", "_")), option);
                    amountLoaded++;
                }
            }
        }
        if (config.getConfigurationSection("mana-abilities") != null) {
            for (String manaAbility : config.getConfigurationSection("mana-abilities").getKeys(false)) {
                boolean hasKey = false;
                for (MAbility manaAbility1 : MAbility.values()) {
                    if (manaAbility.toUpperCase().replace("-", "_").equals(manaAbility1.name())) {
                        hasKey = true;
                        break;
                    }
                }
                if (hasKey) {
                    double baseValue = config.getDouble("mana-abilities." + manaAbility + ".base-value");
                    double valuePerLevel = config.getDouble("mana-abilities." + manaAbility + ".value-per-level");
                    int cooldown = config.getInt("mana-abilities." + manaAbility + ".cooldown");
                    int cooldownPerLevel = config.getInt("mana-abilities." + manaAbility + ".cooldown-per-level");
                    int manaCost = config.getInt("mana-abilities." + manaAbility + ".mana-cost");
                    int manaCostPerLevel = config.getInt("mana-abilities." + manaAbility + ".mana-cost-per-level");
                    AbilityOption abilityOption = new AbilityOption(baseValue, valuePerLevel);
                    abilityOptions.put(Ability.valueOf(manaAbility.toUpperCase().replace("-", "_")), abilityOption);
                    ManaAbilityOption option = new ManaAbilityOption(baseValue, valuePerLevel, cooldown, cooldownPerLevel, manaCost, manaCostPerLevel);
                    manaAbilityOptions.put(MAbility.valueOf(manaAbility.toUpperCase().replace("-", "_")), option);
                    amountLoaded++;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.AQUA + "Loaded " + ChatColor.GOLD + amountLoaded + ChatColor.AQUA + " Ability Options in " + ChatColor.GOLD + amountLoaded + "ms");
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
}
