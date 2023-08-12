package dev.aurelium.auraskills.bukkit.config;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.ConfigProvider;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.config.OptionType;
import dev.aurelium.auraskills.common.config.OptionValue;
import dev.aurelium.auraskills.common.skill.LoadedSkill;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BukkitConfigProvider implements ConfigProvider {

    private final AuraSkills plugin;
    private final Map<Option, OptionValue> options = new HashMap<>();

    public BukkitConfigProvider(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void loadOptions() {
        Logger logger = plugin.getLogger();
        // Save the config to file if not exist
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
        // Load the default options
        loadDefaultOptions();
        // Load the FileConfiguration
        FileConfiguration config = plugin.getConfig();
        // For every option
        int loaded = 0;
        long start = System.currentTimeMillis();
        for (Option option : Option.values()) {
            // Get the value from config
            Object value = config.get(option.getPath());
            // Check if value exists
            if (value != null) {
                // Add if supposed to be int and value is int
                if ((value instanceof Integer || value instanceof Double) && option.getType() == OptionType.INT) {
                    options.put(option, new OptionValue(value));
                    loaded++;
                } else if ((value instanceof Double || value instanceof Integer) && option.getType() == OptionType.DOUBLE) {
                    options.put(option, new OptionValue(value));
                    loaded++;
                } else if (value instanceof Boolean && option.getType() == OptionType.BOOLEAN) {
                    options.put(option, new OptionValue(value));
                    loaded++;
                } else if ((value instanceof String || value instanceof Integer || value instanceof Double || value instanceof Boolean) && option.getType() == OptionType.STRING) {
                    options.put(option, new OptionValue(String.valueOf(value)));
                    loaded++;
                } else if (value instanceof List && option.getType() == OptionType.LIST) {
                    options.put(option, new OptionValue(value));
                    loaded++;
                } else if (value instanceof String && option.getType() == OptionType.COLOR) {
                    options.put(option, new OptionValue(ChatColor.valueOf(String.valueOf(value))));
                    loaded++;
                } else {
                    logger.warning("Incorrect type in config.yml: Option " + option.name() + " with path " + option.getPath() + " should be of type " + option.getType().name() + ", using default value instead!");
                }
            }
            else {
                logger.warning("Missing value in config.yml: Option " + option.name() + " with path " + option.getPath() + " was not found, using default value instead!");
            }
        }

        long end = System.currentTimeMillis();
        logger.info("Loaded " + loaded + " config options in " + (end - start) + " ms");
    }

    private void loadDefaultOptions() {
        InputStream inputStream = plugin.getResource("config.yml");
        if (inputStream != null) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
            for (Option option : Option.values()) {
                if (option.getType() == OptionType.INT) {
                    options.put(option, new OptionValue(config.getInt(option.getPath())));
                } else if (option.getType() == OptionType.DOUBLE) {
                    options.put(option, new OptionValue(config.getDouble(option.getPath())));
                } else if (option.getType() == OptionType.BOOLEAN) {
                    options.put(option, new OptionValue(config.getBoolean(option.getPath())));
                }
            }
        }
    }

    @Override
    public boolean getBoolean(Option option) {
        return options.get(option).asBoolean();
    }

    @Override
    public int getInt(Option option) {
        return options.get(option).asInt();
    }

    @Override
    public double getDouble(Option option) {
        return options.get(option).asDouble();
    }

    @Override
    public String getString(Option option) {
        return options.get(option).asString();
    }

    @Override
    public List<String> getStringList(Option option) {
        return options.get(option).asList();
    }

    @Override
    public boolean isEnabled(Skill skill) {
        return skill.isEnabled();
    }

    @Override
    public int getMaxLevel(Skill skill) {
        if (!skill.isEnabled()) {
            return 0;
        }
        return plugin.getSkillManager().getSkill(skill).options().maxLevel();
    }

    @Override
    public int getHighestMaxLevel() {
        int highest = 0;
        for (LoadedSkill skill : plugin.getSkillManager().getSkills()) {
            int maxLevel = skill.options().maxLevel();
            if (maxLevel > highest) {
                highest = maxLevel;
            }
        }
        return highest;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }
}
