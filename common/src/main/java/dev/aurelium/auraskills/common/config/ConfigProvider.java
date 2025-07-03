package dev.aurelium.auraskills.common.config;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.hooks.EconomyHook;
import dev.aurelium.auraskills.common.message.PlatformLogger;
import dev.aurelium.auraskills.common.skill.LoadedSkill;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a provider for the plugin's main configuration.
 */
public abstract class ConfigProvider {

    private final AuraSkillsPlugin plugin;
    protected final Map<Option, OptionValue> options = new HashMap<>();
    private Map<Option, Object> overrides;

    public ConfigProvider(AuraSkillsPlugin plugin, Map<Option, Object> overrides) {
        this.plugin = plugin;
        this.overrides = overrides;
    }

    public abstract void registerHooks(ConfigurationNode config);

    public abstract Object parseColor(String value);

    public void loadOptions() {
        PlatformLogger logger = plugin.logger();
        ConfigurateLoader loader = new ConfigurateLoader(plugin, TypeSerializerCollection.builder().build());
        try {
            // Load embedded file
            ConfigurationNode embedded = loader.loadEmbeddedFile("config.yml");
            // Load user file
            ConfigurationNode user = loader.loadUserFile("config.yml");
            // Merge embedded and user nodes to ensure config has all options
            ConfigurationNode config = loader.mergeNodes(embedded, user);

            // Load regular options
            int loaded = 0;
            long start = System.currentTimeMillis();
            for (Option option : Option.values()) {
                // Get the value from config
                Object value = config.node(FileUtil.toPath(option.getPath())).raw();
                // Check if value exists
                if (value == null) {
                    logger.warn("Missing value in config.yml: Option " + option.name() + " with path " + option.getPath() + " was not found, using default value instead!");
                    continue;
                }
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
                    options.put(option, new OptionValue(parseColor(String.valueOf(value))));
                    loaded++;
                } else {
                    logger.warn("Incorrect type in config.yml: Option " + option.name() + " with path " + option.getPath() + " should be of type " + option.getType().name() + ", using default value instead!");
                }
            }
            registerHooks(config);

            File file = new File(plugin.getPluginFolder(), "config.yml");
            loader.saveConfigIfUpdated(file, embedded, user, config);

            for (Entry<Option, Object> entry : overrides.entrySet()) {
                options.put(entry.getKey(), new OptionValue(entry.getValue()));
            }

            long end = System.currentTimeMillis();
            logger.info("Loaded " + loaded + " config options in " + (end - start) + " ms");
        } catch (IOException e) {
            plugin.logger().severe("Failed to load config.yml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean getBoolean(Option option) {
        return options.get(option).asBoolean();
    }

    public int getInt(Option option) {
        return options.get(option).asInt();
    }

    public double getDouble(Option option) {
        return options.get(option).asDouble();
    }

    public String getString(Option option) {
        return options.get(option).asString();
    }

    public List<String> getStringList(Option option) {
        return options.get(option).asList();
    }

    public boolean isEnabled(Skill skill) {
        return skill.isEnabled();
    }

    public int getMaxLevel(Skill skill) {
        if (!skill.isEnabled()) {
            return 0;
        }
        return plugin.getSkillManager().getSkill(skill).options().maxLevel();
    }

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

    public int getStartLevel() {
        return options.get(Option.START_LEVEL).asInt();
    }

    public boolean jobSelectionEnabled() {
        boolean economyEnabled = plugin.getHookManager().isRegistered(EconomyHook.class);
        boolean selectionEnabled = plugin.configBoolean(Option.JOBS_SELECTION_REQUIRE_SELECTION);
        return plugin.configBoolean(Option.JOBS_ENABLED) && economyEnabled && selectionEnabled;
    }

    public void setOverrides(Map<Option, Object> overrides) {
        this.overrides = overrides;
    }

}
