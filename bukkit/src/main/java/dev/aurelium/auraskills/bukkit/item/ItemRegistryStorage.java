package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ItemRegistryStorage {

    private final AuraSkills plugin;
    private final BukkitItemRegistry registry;

    public ItemRegistryStorage(AuraSkills plugin, BukkitItemRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "items.yml");
        // Don't load if file doesn't exist
        if (!file.exists()) {
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection == null) return;
        int itemsRegistered = 0;
        for (String key : itemsSection.getKeys(false)) {
            ItemStack item = itemsSection.getItemStack(key);
            if (item == null) {
                plugin.getLogger().warning("Failed to load item at path items." + key + " in items.yml, did you edit this file? " +
                        "This file should not be edited directly and items should only be added in game through the command /skills item register");
                continue;
            }
            NamespacedId id = NamespacedId.fromDefault(key);
            registry.register(id, item);
            itemsRegistered++;
        }
        if (itemsRegistered > 0) {
            plugin.getLogger().info("Registered " + itemsRegistered + " item" + (itemsRegistered > 1 ? "s" : ""));
        }
    }

    public void save() {
        File file = new File(plugin.getDataFolder(), "items.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("items", null); // Delete to account for unregistered items
        // Save each item
        for (Map.Entry<NamespacedId, ItemStack> entry : registry.getItems().entrySet()) {
            config.set("items." + entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save items.yml, see error below:");
            e.printStackTrace();
        }
    }

}
