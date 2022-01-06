package com.archyx.aureliumskills.menus;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.util.file.FileUtil;
import com.archyx.aureliumskills.util.misc.DataUtil;
import com.archyx.slate.menu.MenuManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
        try {
            migrateLegacyFiles();
        } catch (Exception e) {
            plugin.getLogger().warning("Error migrating legacy menus.yml file to new menu files. " +
                    "Any previous changes made in menus.yml may have to be transferred manually to the new files in the menus folder.");
            e.printStackTrace();
        }
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

    public void migrateLegacyFiles() throws IOException {
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
            // Migrate templates
            ConfigurationSection templatesSection = oldSection.getConfigurationSection("templates");
            ConfigurationSection newTemplatesSection = newConfig.getConfigurationSection("templates");
            if (templatesSection != null && newTemplatesSection != null) {
                migrateTemplates(templatesSection, newTemplatesSection);
            }

            // Save file
            newConfig.save(newFile);
        }

        String renamedName = FileUtil.renameNoDuplicates(legacyFile, "menus-OLD.yml", plugin.getDataFolder()); // Rename old file
        if (renamedName != null) {
            plugin.getLogger().info("Successfully migrated menus.yml to new files in the menus folder. " +
                    "menus.yml has been renamed to " + renamedName + " and should not be used anymore.");
        } else {
            plugin.getLogger().warning("Error renaming menus.yml to menus-OLD.yml");
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

            String oldMaterial = oldItem.getString("material");
            if (oldMaterial != null) {
                migrateBaseItem(newItem, oldMaterial); // Migrate base item including material and other item meta
            }

            String displayName = oldItem.getString("display_name");
            newItem.set("display_name", displayName);

            if (!itemName.equals("rank")) { // Migrate lore except the rank item
                List<String> lore = oldItem.getStringList("lore");
                newItem.set("lore", lore);
            }
        }
    }

    private void migrateTemplates(ConfigurationSection oldSection, ConfigurationSection newSection) {
        for (String templateName : oldSection.getKeys(false)) {
            ConfigurationSection oldTemplate = oldSection.getConfigurationSection(templateName);
            if (oldTemplate == null) continue;
            ConfigurationSection newTemplate = newSection.getConfigurationSection(templateName);
            if (newTemplate == null) continue;
            // Migrate materials
            Object materialObj = oldTemplate.get("material");
            if (materialObj instanceof String) { // Single material
                String oldMaterial = ((String) materialObj).toLowerCase(Locale.ROOT);
                migrateBaseItem(newTemplate, oldMaterial);
            } else if (materialObj instanceof List) { // Context dependent material
                List<String> oldMaterials = DataUtil.castStringList(materialObj);
                for (String oldMaterialEntry : oldMaterials) { // For each entry on the old list
                    String[] splitMaterial = oldMaterialEntry.split(" ", 2); // Split into context and rest of material
                    if (splitMaterial.length < 2) continue;
                    String contextString = splitMaterial[0].toLowerCase(Locale.ROOT);
                    // Get context section in new template
                    ConfigurationSection contextSection = newTemplate.getConfigurationSection(contextString);
                    if (contextSection == null) return;

                    String oldMaterial = splitMaterial[1];
                    migrateBaseItem(contextSection, oldMaterial);
                }
            }

            String displayName = oldTemplate.getString("display_name");
            newTemplate.set("display_name", displayName);
            List<String> lore = oldTemplate.getStringList("lore");
            newTemplate.set("lore", lore);
        }
    }

    private void migrateBaseItem(ConfigurationSection newItem, String oldMaterial) {
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
