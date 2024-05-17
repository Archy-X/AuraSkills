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
import java.util.ArrayList;
import java.util.List;

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

                updateAndSave(menuName, embeddedConfig, userConfig, userFile);
            } catch (IOException e) {
                plugin.logger().warn("Error updating menu file " + userFile.getName());
                e.printStackTrace();
            }
        }
    }

    private void updateAndSave(String menuName, ConfigurationNode embedded, ConfigurationNode user, File userFile) throws SerializationException {
        // Files that don't have updating enabled yet, since they haven't had changes
        if (embedded.node("file_version").virtual()) return;

        int embVersion = embedded.node("file_version").getInt();
        int userVersion = user.node("file_version").getInt(0);

        // User file is already up-to-date
        if (userVersion >= embVersion) {
            return;
        }

        List<MenuFileUpdates> updates = MenuFileUpdates.getUpdates(menuName, userVersion, embVersion);
        if (updates.isEmpty()) return;

        int changed = 0;
        for (MenuFileUpdates update : updates) {
            List<String> addedItems = update.getAddedKeys().getOrDefault("items", new ArrayList<>());
            changed += updateConfigSection("items", embedded, user, addedItems);
            List<String> addedTemplates = update.getAddedKeys().getOrDefault("templates", new ArrayList<>());
            changed += updateConfigSection("templates", embedded, user, addedTemplates);
            List<String> addedComponents = update.getAddedKeys().getOrDefault("components", new ArrayList<>());
            changed += updateConfigSection("components", embedded, user, addedComponents);
            List<String> addedFormats = update.getAddedKeys().getOrDefault("formats", new ArrayList<>());
            changed += updateStringSection("formats", embedded, user, addedFormats);
        }

        user.node("file_version").set(embVersion);

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

    private int updateConfigSection(String name, ConfigurationNode embedded, ConfigurationNode user, List<String> keys) throws SerializationException {
        if (keys.isEmpty()) return 0;
        int changed = 0;
        if (!embedded.node(name).virtual() && !user.node(name).virtual()) {
            for (ConfigurationNode embSec : embedded.node(name).childrenMap().values()) {
                String key = (String) embSec.key();
                if (key == null) continue;
                if (!keys.contains(key)) continue; // Only update sections passed in the keys list
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

    private int updateStringSection(String name, ConfigurationNode embedded, ConfigurationNode user, List<String> keys) throws SerializationException {
        if (keys.isEmpty()) return 0;
        int changed = 0;
        if (!embedded.node(name).virtual() && !user.node(name).virtual()) {
            for (ConfigurationNode embSec : embedded.node(name).childrenMap().values()) {
                String key = (String) embSec.key();
                if (key == null) continue;
                if (!keys.contains(key)) continue;

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
