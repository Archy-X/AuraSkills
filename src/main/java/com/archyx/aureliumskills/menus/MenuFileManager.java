package com.archyx.aureliumskills.menus;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.slate.menu.MenuManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Locale;

public class MenuFileManager {

    private final AureliumSkills plugin;
    private final MenuManager manager;

    public MenuFileManager(AureliumSkills plugin) {
        this.plugin = plugin;
        this.manager = plugin.getMenuManager();
    }

    public void generateDefaultFiles() {
        for (String menuName : manager.getMenuProviderNames()) {
            File file = new File(plugin.getDataFolder() + "/menus", menuName + ".yml");
            if (!file.exists()) {
                plugin.saveResource("menus/" + menuName + ".yml", false);
            }
        }
    }

    public void loadMenus() {
        int menusLoaded = 0;
        for (String menuName : manager.getMenuProviderNames()) {
            File file = new File(plugin.getDataFolder() + "/menus", menuName + ".yml");
            if (file.exists()) {
                try {
                    manager.loadMenu(file);
                    menusLoaded++;
                } catch (Exception e) {
                    plugin.getLogger().warning("Error loading menu " + menuName);
                    e.printStackTrace();
                }
            }
        }
        plugin.getLogger().info("Loaded " + menusLoaded + " menus");
    }

    public void migrateLegacyFiles() {
        File legacyFile = new File(plugin.getDataFolder(), "menus.yml");
        if (!legacyFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(legacyFile);
        String[] legacyNames = new String[] {"skills_menu", "stats_menu", "level_progression_menu"};
        for (String legacyName : legacyNames) {
            ConfigurationSection oldSection = config.getConfigurationSection(legacyName);
            if (oldSection == null) continue;

            String menuName = StringUtils.removeEnd(legacyName, "_menu");
            // Load new file configuration
            File newFile = new File(plugin.getDataFolder(), "menus/" + menuName + ".yml");
            if (!newFile.exists()) continue;
            FileConfiguration newConfig = YamlConfiguration.loadConfiguration(newFile);

            newConfig.set("title", oldSection.getString("title"));
            newConfig.set("size", oldSection.getInt("rows"));
            // Migrate fill settings
            newConfig.set("fill.enabled", oldSection.getBoolean("fill.enabled"));
            newConfig.set("fill.material", oldSection.getString("fill.material", "black_stained_glass_pane").toLowerCase(Locale.ROOT));
            // Migrate items
            ConfigurationSection itemsSection = oldSection.getConfigurationSection("items");
            ConfigurationSection newItemsSection = newConfig.getConfigurationSection("items");
            if (itemsSection != null && newItemsSection != null) {
                migrateItems(itemsSection, newItemsSection);
            }
        }
    }

    private void migrateItems(ConfigurationSection oldSection, ConfigurationSection newSection) {
        for (String itemName : oldSection.getKeys(false)) {
            // Get the configuration sections of new and old items
            ConfigurationSection oldItem = oldSection.getConfigurationSection(itemName);
            if (oldItem == null) continue;
            ConfigurationSection newItem = newSection.getConfigurationSection(itemName);
            if (newItem == null) continue;
            // Migrate row and column to position
            int row = oldItem.getInt("row");
            int column = oldItem.getInt("column");
            String pos = row + "," + column;
            newItem.set("pos", pos);

            migrateBaseItem(oldItem, newItem); // Migrate base item including material and other item meta

            // TODO migrate display name and lore (rank item should be exempt)
        }
    }

    private void migrateBaseItem(ConfigurationSection oldItem, ConfigurationSection newItem) {
        String oldMaterial = oldItem.getString("material");
        if (oldMaterial == null) return;
        String[] tokens = oldMaterial.split(" ", 2);
        if (tokens.length == 0) return;
        // Migrate material
        String material = tokens[0].toLowerCase(Locale.ROOT);
        newItem.set("material", material);
        // Migrate legacy meta
        if (tokens.length < 2) return;
        String[] args = tokens[1].split(" ");
        if (args.length == 0) return;
        for (String argument : args) {
            String[] splitArg = argument.split(":");
            if (splitArg.length < 2) continue;
            String key = splitArg[0];
            String value = splitArg[1];
            switch (key.toLowerCase(Locale.ROOT)) {
                case "potion_type":
                    newItem.set("potion_data.type", value.toLowerCase(Locale.ROOT));
                    break;
                case "glow":
                    newItem.set("glow", value);
                    break;
                case "custom_model_data":
                    newItem.set("nbt.CustomModelData", Integer.parseInt(value));
                    break;
            }
        }
    }

}
