package dev.aurelium.auraskills.bukkit.config;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.HookRegistrar;
import dev.aurelium.auraskills.common.config.*;
import dev.aurelium.auraskills.common.message.PlatformLogger;
import dev.aurelium.auraskills.common.skill.LoadedSkill;
import org.bukkit.ChatColor;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BukkitConfigProvider implements ConfigProvider {

    private final AuraSkills plugin;
    private final Map<Option, OptionValue> options = new HashMap<>();

    public BukkitConfigProvider(AuraSkills plugin) {
        this.plugin = plugin;
    }

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
                Object value = config.node(toPath(option.getPath())).raw();
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
                    options.put(option, new OptionValue(ChatColor.valueOf(String.valueOf(value).toUpperCase(Locale.ROOT))));
                    loaded++;
                } else {
                    logger.warn("Incorrect type in config.yml: Option " + option.name() + " with path " + option.getPath() + " should be of type " + option.getType().name() + ", using default value instead!");
                }
            }
            // Load hooks
            HookRegistrar hookRegistrar = new HookRegistrar(plugin, plugin.getHookManager());
            hookRegistrar.registerHooks(config.node("hooks"));

            saveConfigIfUpdated(embedded, user, config);

            long end = System.currentTimeMillis();
            logger.info("Loaded " + loaded + " config options in " + (end - start) + " ms");
        } catch (IOException e) {
            plugin.logger().severe("Failed to load config.yml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveConfigIfUpdated(ConfigurationNode embedded, ConfigurationNode user, ConfigurationNode merged) throws ConfigurateException {
        File file = new File(plugin.getPluginFolder(), "config.yml");
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(file.toPath())
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .build();

        // Save if the number of config values in embedded is greater than user file
        int embeddedCount = countChildren(embedded);
        int userCount = countChildren(user);
        if (countChildren(embedded) > countChildren(user)) {
            loader.save(merged);
            plugin.logger().info("Updated config.yml with " + (embeddedCount - userCount) + " new keys");
        }
    }

    private int countChildren(ConfigurationNode root) {
        int count = 0;
        Stack<ConfigurationNode> stack = new Stack<>();
        stack.addAll(root.childrenMap().values());
        while (!stack.isEmpty()) {
            ConfigurationNode node = stack.pop();
            if (node.isMap()) { // A section node, push children to search
                stack.addAll(node.childrenMap().values());
            } else {
                count++;
            }
        }
        return count;
    }

    private NodePath toPath(String path) {
        String[] split = path.split("\\.");
        return NodePath.of(split);
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
        return options.get(Option.START_LEVEL).asInt();
    }
}
