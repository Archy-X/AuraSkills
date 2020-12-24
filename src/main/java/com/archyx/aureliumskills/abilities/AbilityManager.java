package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityOption;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AbilityManager {

    private final Map<Ability, AbilityOption> abilityOptions;
    private final Map<MAbility, ManaAbilityOption> manaAbilityOptions;
    private final Plugin plugin;

    public AbilityManager(Plugin plugin) {
        this.plugin = plugin;
        abilityOptions = new HashMap<>();
        manaAbilityOptions = new HashMap<>();
    }

    public void loadOptions() {
        File file = new File(plugin.getDataFolder(), "abilities_config.yml");
        if (!file.exists()) {
            plugin.saveResource("abilities_config.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        //Loads ability options
        int amountLoaded = 0;
        int amountDisabled = 0;
        long startTime = System.currentTimeMillis();
        ConfigurationSection abilities = config.getConfigurationSection("abilities");
        if (abilities != null) {
            for (Skill skill : Skill.values()) {
                String skillName = skill.name().toLowerCase();
                ConfigurationSection skillAbilities = abilities.getConfigurationSection(skillName);
                if (skillAbilities != null) {
                    for (String abilityName : skillAbilities.getKeys(false)) {
                        // Check if ability is valid
                        boolean hasKey = false;
                        for (Ability ability : Ability.values()) {
                            if (abilityName.toUpperCase().equals(ability.name())) {
                                hasKey = true;
                                break;
                            }
                        }
                        if (hasKey) {
                            String path = "abilities." + skillName + "." + abilityName + ".";
                            Ability ability = Ability.valueOf(abilityName.toUpperCase());
                            boolean enabled = config.getBoolean(path + "enabled", true);
                            if (!enabled) {
                                amountDisabled++;
                            }
                            double baseValue = config.getDouble(path + "base", ability.getDefaultBaseValue());
                            double valuePerLevel = config.getDouble(path + "per_level", ability.getDefaultValuePerLevel());
                            // Get default unlock value
                            int defUnlock = 2;
                            for (int i = 0; i < skill.getAbilities().size(); i++) {
                                if (skill.getAbilities().get(i).get() == ability) {
                                    defUnlock += i;
                                    break;
                                }
                            }
                            int unlock = config.getInt(path + "unlock", defUnlock);
                            int levelUp = config.getInt(path + "level_up", 5);
                            int maxLevel = config.getInt(path + "max_level", 0);
                            AbilityOption option;
                            //Checks if ability has 2 values
                            if (ability.hasTwoValues()) {
                                double baseValue2 = config.getDouble(path + "base_2", ability.getDefaultBaseValue2());
                                double valuePerLevel2 = config.getDouble(path + "per_level_2", ability.getDefaultValuePerLevel2());
                                option = new AbilityOption(enabled, baseValue, valuePerLevel, baseValue2, valuePerLevel2, unlock, levelUp, maxLevel);
                            }
                            else {
                                option = new AbilityOption(enabled, baseValue, valuePerLevel, unlock, levelUp, maxLevel);
                            }
                            abilityOptions.put(ability, option);
                            amountLoaded++;
                        }
                    }
                }
            }
        }
        ConfigurationSection manaAbilities = config.getConfigurationSection("mana_abilities");;
        if (manaAbilities != null) {
            for (String manaAbilityName : manaAbilities.getKeys(false)){
                boolean hasKey = false;
                for (MAbility manaAbility : MAbility.values()) {
                    if (manaAbilityName.toUpperCase().equals(manaAbility.name())) {
                        hasKey = true;
                        break;
                    }
                }
                if (hasKey) {
                    String path = "mana_abilities." + manaAbilityName + ".";
                    boolean enabled = config.getBoolean(path + "enabled");
                    if (!enabled) {
                        amountDisabled++;
                    }
                    double baseValue = config.getDouble(path + "base_value");
                    double valuePerLevel = config.getDouble(path + "value_per_level");
                    double cooldown = config.getDouble(path + "cooldown");
                    double cooldownPerLevel = config.getDouble(path + "cooldown-per-level");
                    int manaCost = config.getInt(path + "mana_cost");
                    int manaCostPerLevel = config.getInt(path + "mana_cost_per_level");
                    int unlock = config.getInt(path + "unlock", 7);
                    int levelUp = config.getInt(path + "level_up", 7);
                    int maxLevel = config.getInt(path + "max_level", 0);
                    ManaAbilityOption option = new ManaAbilityOption(enabled, baseValue, valuePerLevel, cooldown, cooldownPerLevel, manaCost, manaCostPerLevel, unlock, levelUp, maxLevel);
                    manaAbilityOptions.put(MAbility.valueOf(manaAbilityName.toUpperCase()), option);
                    amountLoaded++;
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
