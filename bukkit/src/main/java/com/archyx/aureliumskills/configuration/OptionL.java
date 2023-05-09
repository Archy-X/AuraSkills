package com.archyx.aureliumskills.configuration;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.mechanics.DamageType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class OptionL {

    private final AureliumSkills plugin;
    private static final Map<Option, OptionValue> options = new HashMap<>();

    public OptionL(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public void loadOptions() {
        Logger logger = plugin.getLogger();
        //Load the default options
        loadDefaultOptions();
        //Load the FileConfiguration
        FileConfiguration config = plugin.getConfig();
        //For every option
        int loaded = 0;
        long start = System.currentTimeMillis();
        for (Option option : Option.values()) {
            //Get the value from config
            Object value = config.get(option.getPath());
            //Check if value exists
            if (value != null) {
                //Add if supposed to be int and value is int
                if ((value instanceof Integer || value instanceof Double) && option.getType() == OptionType.INT) {
                    options.put(option, new OptionValue(value));
                    loaded++;
                }
                //Add if supposed to be double and value is double
                else if ((value instanceof Double || value instanceof Integer) && option.getType() == OptionType.DOUBLE) {
                    options.put(option, new OptionValue(value));
                    loaded++;
                }
                //Add if supposed to be boolean and value is boolean
                else if (value instanceof Boolean && option.getType() == OptionType.BOOLEAN) {
                    options.put(option, new OptionValue(value));
                    loaded++;
                }
                //Add if supposed to be string and value is string
                else if ((value instanceof String || value instanceof Integer || value instanceof Double || value instanceof Boolean) && option.getType() == OptionType.STRING) {
                    options.put(option, new OptionValue(String.valueOf(value)));
                    loaded++;
                }
                //Add if supposed to be list and value is list
                else if (value instanceof List && option.getType() == OptionType.LIST) {
                    options.put(option, new OptionValue(value));
                    loaded++;
                }
                else if (value instanceof String && option.getType() == OptionType.COLOR) {
                    options.put(option, new OptionValue(ChatColor.valueOf(String.valueOf(value))));
                    loaded++;
                }
                //Error
                else {
                    logger.warning("Incorrect type in config.yml: Option " + option.name() + " with path " + option.getPath() + " should be of type " + option.getType().name() + ", using default value instead!");
                }
            }
            else {
                logger.warning("Missing value in config.yml: Option " + option.name() + " with path " + option.getPath() + " was not found, using default value instead!");
            }
        }
        plugin.getHealth().loadHearts(config);
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
                }
                else if (option.getType() == OptionType.DOUBLE) {
                    options.put(option, new OptionValue(config.getDouble(option.getPath())));
                }
                else if (option.getType() == OptionType.BOOLEAN) {
                    options.put(option, new OptionValue(config.getBoolean(option.getPath())));
                }
            }
        }
    }

    public static double getDouble(Option option) {
        return options.get(option).asDouble();
    }

    public static int getInt(Option option) {
        return options.get(option).asInt();
    }

    public static boolean getBoolean(Option option) {
        return options.get(option).asBoolean();
    }

    public static String getString(Option option) {
        return options.get(option).asString();
    }

    public static List<String> getList(Option option) {
        return options.get(option).asList();
    }

    public static ChatColor getColor(Option option) {
        return options.get(option).asColor();
    }

    public static boolean isEnabled(Skill skill) {
        return getBoolean(Option.valueOf(skill.name() + "_ENABLED"));
    }

    public static int getMaxLevel(Skill skill) {
        return getInt(Option.valueOf(skill.name() + "_MAX_LEVEL"));
    }

    public int getHighestMaxLevel() {
        int highest = 96;
        for (Skill skill : plugin.getSkillRegistry().getSkills()) {
            int maxLevel = getInt(Option.valueOf(skill.name() + "_MAX_LEVEL"));
            if (maxLevel > highest) {
                highest = maxLevel;
            }
        }
        return highest;
    }

    public static boolean criticalEnabled(DamageType type) {
        return getBoolean(Option.valueOf("CRITICAL_ENABLED_" + type.name()));
    }

}
