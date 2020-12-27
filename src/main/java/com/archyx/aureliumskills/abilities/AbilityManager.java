package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionValue;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityOption;
import com.archyx.aureliumskills.skills.Skill;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

public class AbilityManager {

    private final Map<Ability, AbilityOption> abilityOptions;
    private final Map<MAbility, ManaAbilityOption> manaAbilityOptions;
    private final AureliumSkills plugin;

    public AbilityManager(AureliumSkills plugin) {
        this.plugin = plugin;
        abilityOptions = new HashMap<>();
        manaAbilityOptions = new HashMap<>();
    }

    public void loadOptions() {
        File file = new File(plugin.getDataFolder(), "abilities_config.yml");
        if (!file.exists()) {
            plugin.saveResource("abilities_config.yml", false);
        }
        FileConfiguration config = updateFile(file, YamlConfiguration.loadConfiguration(file));
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
                            // Load options
                            Set<String> optionKeys = getOptionKeys(ability);
                            Map<String, OptionValue> options = null;
                            if (optionKeys != null) {
                                options = new HashMap<>();
                                for (String key : optionKeys) {
                                    options.put(key, new OptionValue(config.get(path + key)));
                                }
                            }
                            AbilityOption option;
                            //Checks if ability has 2 values
                            if (ability.hasTwoValues()) {
                                double baseValue2 = config.getDouble(path + "base_2", ability.getDefaultBaseValue2());
                                double valuePerLevel2 = config.getDouble(path + "per_level_2", ability.getDefaultValuePerLevel2());
                                if (options != null) {
                                    option = new AbilityOption(enabled, baseValue, valuePerLevel, baseValue2, valuePerLevel2, unlock, levelUp, maxLevel, options);
                                } else {
                                    option = new AbilityOption(enabled, baseValue, valuePerLevel, baseValue2, valuePerLevel2, unlock, levelUp, maxLevel);
                                }
                            }
                            else {
                                if (options != null) {
                                    option = new AbilityOption(enabled, baseValue, valuePerLevel, unlock, levelUp, maxLevel, options);
                                } else {
                                    option = new AbilityOption(enabled, baseValue, valuePerLevel, unlock, levelUp, maxLevel);
                                }
                            }
                            abilityOptions.put(ability, option);
                            amountLoaded++;
                        }
                    }
                }
            }
        }
        ConfigurationSection manaAbilities = config.getConfigurationSection("mana_abilities");
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
                    MAbility mAbility = MAbility.valueOf(manaAbilityName.toUpperCase());
                    String path = "mana_abilities." + manaAbilityName + ".";
                    boolean enabled = config.getBoolean(path + "enabled");
                    if (!enabled) {
                        amountDisabled++;
                    }
                    double baseValue = config.getDouble(path + "base_value");
                    double valuePerLevel = config.getDouble(path + "value_per_level");
                    double cooldown = config.getDouble(path + "cooldown");
                    double cooldownPerLevel = config.getDouble(path + "cooldown-per-level");
                    double manaCost = config.getDouble(path + "mana_cost");
                    double manaCostPerLevel = config.getDouble(path + "mana_cost_per_level");
                    int unlock = config.getInt(path + "unlock", 7);
                    int levelUp = config.getInt(path + "level_up", 7);
                    int maxLevel = config.getInt(path + "max_level", 0);
                    // Load options
                    Set<String> optionKeys = plugin.getManaAbilityManager().getOptionKeys(mAbility);
                    Map<String, OptionValue> options = null;
                    if (optionKeys != null) {
                        options = new HashMap<>();
                        for (String key : optionKeys) {
                            options.put(key, new OptionValue(config.get(path + key)));
                        }
                    }
                    ManaAbilityOption option;
                    if (options != null) {
                        option = new ManaAbilityOption(enabled, baseValue, valuePerLevel, cooldown, cooldownPerLevel, manaCost, manaCostPerLevel, unlock, levelUp, maxLevel, options);
                    } else {
                        option = new ManaAbilityOption(enabled, baseValue, valuePerLevel, cooldown, cooldownPerLevel, manaCost, manaCostPerLevel, unlock, levelUp, maxLevel);
                    }
                    manaAbilityOptions.put(mAbility, option);
                    amountLoaded++;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        Bukkit.getLogger().info("[AureliumSkills] Disabled " + amountDisabled + " Abilities");
        Bukkit.getLogger().info("[AureliumSkills] Loaded " + amountLoaded + " Ability Options in " + timeElapsed + "ms");
    }

    private FileConfiguration updateFile(File file, FileConfiguration config) {
        if (config.contains("file_version")) {
            InputStream stream = plugin.getResource("abilities_config.yml");
            if (stream != null) {
                int currentVersion = config.getInt("file_version");
                FileConfiguration imbConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
                int imbVersion = imbConfig.getInt("file_version");
                //If versions do not match
                if (currentVersion != imbVersion) {
                    try {
                        ConfigurationSection configSection = imbConfig.getConfigurationSection("");
                        if (configSection != null) {
                            for (String key : configSection.getKeys(true)) {
                                if (!configSection.isConfigurationSection(key)) {
                                    if (!config.contains(key)) {
                                        config.set(key, imbConfig.get(key));
                                    }
                                }
                            }
                        }
                        config.set("file_version", imbVersion);
                        config.save(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public AbilityOption getAbilityOption(Ability ability) {
        return abilityOptions.get(ability);
    }

    public ManaAbilityOption getAbilityOption(MAbility mAbility) {
        return manaAbilityOptions.get(mAbility);
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

    public double getValue(Ability ability, int level) {
        return getBaseValue(ability) + (getValuePerLevel(ability) * (level - 1));
    }

    public double getBaseValue(Ability ability) {
        AbilityOption option = getAbilityOption(ability);
        if (option != null) {
            return option.getBaseValue();
        }
        return ability.getDefaultBaseValue();
    }

    public double getValuePerLevel(Ability ability) {
        AbilityOption option = getAbilityOption(ability);
        if (option != null) {
            return option.getValuePerLevel();
        }
        return ability.getDefaultValuePerLevel();
    }

    public double getValue2(Ability ability, int level) {
        return getBaseValue2(ability) + (getValuePerLevel2(ability) * (level - 1));
    }

    public double getBaseValue2(Ability ability) {
        AbilityOption option = getAbilityOption(ability);
        if (option != null) {
            return option.getBaseValue2();
        }
        return ability.getDefaultBaseValue2();
    }

    public double getValuePerLevel2(Ability ability) {
        AbilityOption option = getAbilityOption(ability);
        if (option != null) {
            return option.getValuePerLevel2();
        }
        return ability.getDefaultValuePerLevel2();
    }

    public int getUnlock(Ability ability) {
        AbilityOption option = getAbilityOption(ability);
        if (option != null) {
            return option.getUnlock();
        }
        int defUnlock = 2;
        Skill skill = ability.getSkill();
        for (int i = 0; i < skill.getAbilities().size(); i++) {
            if (skill.getAbilities().get(i).get() == ability) {
                defUnlock += i;
                break;
            }
        }
        return defUnlock;
    }

    public int getLevelUp(Ability ability) {
        AbilityOption option = getAbilityOption(ability);
        if (option != null) {
            return option.getLevelUp();
        }
        return 5;
    }

    public int getMaxLevel(Ability ability) {
        AbilityOption option = getAbilityOption(ability);
        if (option != null) {
            return option.getMaxLevel();
        }
        return 0;
    }

    /**
     * Gets a list of abilities unlocked or leveled up at a certain level
     * @param skill The skill
     * @param level The skill level
     * @return A list of abilities
     */
    public List<Ability> getAbilities(Skill skill, int level) {
        ImmutableList<Supplier<Ability>> skillAbilities = skill.getAbilities();
        List<Ability> abilities = new ArrayList<>();
        for (Supplier<Ability> abilitySupplier : skillAbilities) {
            Ability ability = abilitySupplier.get();
            if (level >= getUnlock(ability) && (level - getUnlock(ability)) % getLevelUp(ability) == 0) {
                abilities.add(ability);
            }
        }
        return abilities;
    }

    @Nullable
    public OptionValue getOption(Ability ability, String key) {
        AbilityOption option = getAbilityOption(ability);
        if (option != null) {
            return option.getOption(key);
        } else {
            return ability.getDefaultOptions().get(key);
        }
    }

    public boolean getOptionAsBooleanElseTrue(Ability ability, String key) {
        OptionValue value = getOption(ability, key);
        if (value != null) {
            return value.asBoolean();
        }
        return true;
    }

    @Nullable
    public Set<String> getOptionKeys(Ability ability) {
        if (ability.getDefaultOptions() != null) {
            return ability.getDefaultOptions().keySet();
        }
        return null;
    }

}
