package dev.aurelium.auraskills.bukkit.skillcoins.shop;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads shop sections and items from configuration files
 */
public class ShopLoader {
    
    private final AuraSkills plugin;
    private final File sectionsFolder;
    private final File shopsFolder;
    private final List<ShopSection> sections;
    
    public ShopLoader(AuraSkills plugin) {
        this.plugin = plugin;
        this.sectionsFolder = new File(plugin.getDataFolder(), "SkillCoinsShop/sections");
        this.shopsFolder = new File(plugin.getDataFolder(), "SkillCoinsShop/shops");
        this.sections = new ArrayList<>();
    }
    
    /**
     * Load all shop sections and their items
     */
    public void load() {
        sections.clear();
        
        // Create folders and copy default files if they don't exist
        if (!sectionsFolder.exists()) {
            sectionsFolder.mkdirs();
            copyDefaultConfigs();
        }
        if (!shopsFolder.exists()) {
            shopsFolder.mkdirs();
        }
        
        // Copy default files if folders are empty
        File[] existingSections = sectionsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (existingSections == null || existingSections.length == 0) {
            copyDefaultConfigs();
        }
        
        // Load sections first
        File[] sectionFiles = sectionsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (sectionFiles == null) {
            plugin.getLogger().warning("No section files found!");
            return;
        }
        
        for (File sectionFile : sectionFiles) {
            try {
                ShopSection section = loadSection(sectionFile);
                if (section != null) {
                    // Load items for this section
                    loadItemsForSection(section);
                    sections.add(section);
                    plugin.getLogger().info("Loaded shop section: " + section.getDisplayName() + 
                            " with " + section.getItemCount() + " items");
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load section from " + sectionFile.getName());
                e.printStackTrace();
            }
        }
        
        plugin.getLogger().info("Loaded " + sections.size() + " shop sections");
    }
    
    private ShopSection loadSection(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        if (!config.getBoolean("enable", true)) {
            return null;
        }
        
        String id = file.getName().replace(".yml", "");
        int slot = config.getInt("slot", 0);
        
        String materialName = config.getString("material", "STONE");
        Material icon = Material.getMaterial(materialName);
        if (icon == null) {
            plugin.getLogger().warning("Invalid material " + materialName + " for section " + id);
            icon = Material.STONE;
        }
        
        String displayName = config.getString("displayname", id);
        
        return new ShopSection(id, displayName, icon, slot);
    }
    
    private void loadItemsForSection(ShopSection section) {
        File shopFile = new File(shopsFolder, section.getId() + ".yml");
        if (!shopFile.exists()) {
            plugin.getLogger().warning("Shop file not found for section: " + section.getId());
            return;
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(shopFile);
        ConfigurationSection pagesSection = config.getConfigurationSection("pages");
        
        if (pagesSection == null) {
            plugin.getLogger().warning("No pages section found in " + shopFile.getName());
            return;
        }
        
        for (String pageKey : pagesSection.getKeys(false)) {
            ConfigurationSection itemsSection = pagesSection.getConfigurationSection(pageKey + ".items");
            if (itemsSection == null) continue;
            
            for (String itemKey : itemsSection.getKeys(false)) {
                try {
                    ShopItem item = loadShopItem(itemsSection.getConfigurationSection(itemKey));
                    if (item != null) {
                        section.addItem(item);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load item " + itemKey + " in section " + section.getId());
                    e.printStackTrace();
                }
            }
        }
    }
    
    private ShopItem loadShopItem(ConfigurationSection section) {
        if (section == null) return null;
        
        String materialName = section.getString("material");
        if (materialName == null) return null;
        
        Material material = Material.getMaterial(materialName);
        if (material == null) {
            plugin.getLogger().warning("Invalid material: " + materialName);
            return null;
        }
        
        double buyPrice = section.getDouble("buy", -1);
        double sellPrice = section.getDouble("sell", -1);
        
        // Determine currency type
        dev.aurelium.auraskills.common.skillcoins.CurrencyType currency = 
                dev.aurelium.auraskills.common.skillcoins.CurrencyType.COINS;
        String currencyString = section.getString("currency", "coins");
        if ("tokens".equalsIgnoreCase(currencyString)) {
            currency = dev.aurelium.auraskills.common.skillcoins.CurrencyType.TOKENS;
        }
        
        // Determine item type
        ShopItem.ItemType type = ShopItem.ItemType.REGULAR;
        String skillName = null;
        int tokenAmount = 0;
        
        if (section.contains("skill")) {
            type = ShopItem.ItemType.SKILL_LEVEL;
            skillName = section.getString("skill");
        } else if (section.contains("tokens")) {
            type = ShopItem.ItemType.TOKEN_EXCHANGE;
            tokenAmount = section.getInt("tokens");
        }
        
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        if (section.contains("enchantments")) {
            List<String> enchantList = section.getStringList("enchantments");
            for (String enchantString : enchantList) {
                try {
                    // Format: ENCHANTMENT_NAME:level
                    String[] parts = enchantString.split(":");
                    if (parts.length == 2) {
                        String enchantName = parts[0].trim();
                        int level = Integer.parseInt(parts[1].trim());
                        
                        // Try to get enchantment by name
                        Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(enchantName.toLowerCase()));
                        if (enchant != null) {
                            enchantments.put(enchant, level);
                        } else {
                            plugin.getLogger().warning("Unknown enchantment: " + enchantName);
                        }
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to parse enchantment: " + enchantString);
                }
            }
        }
        
        return new ShopItem(material, buyPrice, sellPrice, enchantments, type, skillName, tokenAmount, currency);
    }
    
    public List<ShopSection> getSections() {
        return new ArrayList<>(sections);
    }
    
    public ShopSection getSection(String id) {
        return sections.stream()
                .filter(s -> s.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Copy default shop configuration files from plugin resources
     */
    private void copyDefaultConfigs() {
        String[] sectionFiles = {"Combat.yml", "Enchantments.yml", "Resources.yml", "Tools.yml", 
                                 "Food.yml", "Blocks.yml", "Farming.yml", "Potions.yml", "Redstone.yml", "Miscellaneous.yml",
                                 "SkillLevels.yml", "TokenExchange.yml", "Tokens.yml"};
        String[] shopFiles = {"Combat.yml", "Enchantments.yml", "Resources.yml", "Tools.yml",
                             "Food.yml", "Blocks.yml", "Farming.yml", "Potions.yml", "Redstone.yml", "Miscellaneous.yml",
                             "SkillLevels.yml", "TokenExchange.yml", "Tokens.yml"};
        
        plugin.getLogger().info("Creating default SkillCoins shop configuration files...");
        
        // Copy section files
        for (String fileName : sectionFiles) {
            File targetFile = new File(sectionsFolder, fileName);
            if (!targetFile.exists()) {
                plugin.saveResource("SkillCoinsShop/sections/" + fileName, false);
            }
        }
        
        // Copy shop files
        for (String fileName : shopFiles) {
            File targetFile = new File(shopsFolder, fileName);
            if (!targetFile.exists()) {
                plugin.saveResource("SkillCoinsShop/shops/" + fileName, false);
            }
        }
        
        plugin.getLogger().info("Default shop files created successfully!");
    }
}
