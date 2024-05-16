package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.api.ApiAuraSkills;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class MenuFileManager {

    private final AuraSkills plugin;
    public static final String[] MENU_NAMES = {"abilities", "leaderboard", "level_progression", "skills", "sources", "stats"};

    public MenuFileManager(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void generateDefaultFiles() {
        for (String menuName : MENU_NAMES) {
            File file = new File(plugin.getDataFolder() + "/menus", menuName + ".yml");
            if (!file.exists()) {
                plugin.saveResource("menus/" + menuName + ".yml", false);
            }
        }
    }

    public void loadMenus() {
        // Add menu directories as merge directories in Slate
        var api = (ApiAuraSkills) plugin.getApi();
        for (NamespacedRegistry registry : api.getNamespacedRegistryMap().values()) {
            registry.getMenuDirectory().ifPresent(dir -> plugin.getSlate().addMergeDirectory(dir));
        }

        int menusLoaded = plugin.getSlate().loadMenus();
        plugin.getLogger().info("Loaded " + menusLoaded + " menus");
    }

    public void updateMenus() {
        for (String menuName : MENU_NAMES) {
            File userFile = new File(plugin.getDataFolder() + "/menus", menuName + ".yml");
            if (!userFile.exists()) continue;

            try {
                ConfigurationNode embeddedConfig = FileUtil.loadEmbeddedYamlFile("menus/" + menuName + ".yml", plugin);
                ConfigurationNode userConfig = FileUtil.loadYamlFile(userFile);

                updateAndSave(embeddedConfig, userConfig, userFile);
            } catch (IOException e) {
                plugin.logger().warn("Error updating menu file " + userFile.getName());
                e.printStackTrace();
            }
        }
    }

    private void updateAndSave(ConfigurationNode embedded, ConfigurationNode user, File userFile) throws SerializationException {
        int changed = 0;
        changed += updateConfigSection("items", embedded, user);
        changed += updateConfigSection("templates", embedded, user);
        changed += updateConfigSection("components", embedded, user);
        changed += updateStringSection("formats", embedded, user);

        if (changed <= 0) {
            return;
        }
        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .file(userFile)
                    .nodeStyle(NodeStyle.BLOCK)
                    .indent(2)
                    .build();
            loader.save(user);

            plugin.logger().info("Menu file " + userFile.getName() + " was updated: " + changed + " new sections added");
        } catch (IOException e) {
            plugin.logger().warn("Error saving menu file " + userFile.getName());
            e.printStackTrace();
        }
    }

    private int updateConfigSection(String name, ConfigurationNode embedded, ConfigurationNode user) throws SerializationException {
        int changed = 0;
        if (!embedded.node(name).virtual() && !user.node(name).virtual()) {
            for (ConfigurationNode embSec : embedded.node(name).childrenMap().values()) {
                String key = (String) embSec.key();
                if (key == null) continue;
                if (!embSec.isMap()) continue;
                // User file does not have embedded key
                if (user.node(name).node(key).virtual()) {
                    user.node(name).node(key).set(embSec);
                    changed++;
                }
            }
        }
        return changed;
    }

    private int updateStringSection(String name, ConfigurationNode embedded, ConfigurationNode user) throws SerializationException {
        int changed = 0;
        if (!embedded.node(name).virtual() && !user.node(name).virtual()) {
            for (ConfigurationNode embSec : embedded.node(name).childrenMap().values()) {
                String key = (String) embSec.key();
                if (key == null) continue;

                String value = embSec.getString();
                if (value == null) continue;
                // User file does not have embedded key
                if (user.node(name).node(key).virtual()) {
                    user.node(name).node(key).set(value);
                    changed++;
                }
            }
        }
        return changed;
    }

}
