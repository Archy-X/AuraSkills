package com.archyx.aureliumskills.lang;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LangLoader {

    private final Plugin plugin;
    private Map<String, String> messages;
    private Map<String, ItemStack> items;

    public LangLoader(Plugin plugin) {
        this.plugin = plugin;
        messages = new HashMap<>();
        items = new HashMap<>();
    }

    public void loadDefaultMessages() {
        //Loads embedded resource
        InputStream stream = plugin.getResource("lang.yml");
        if (stream == null) {
            return;
        }
        FileConfiguration lang = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
        //Load configuration section
        ConfigurationSection config = lang.getConfigurationSection("messages");
        if (config == null) {
            return;
        }
        //Loops through keys
        for (String path : config.getKeys(true)) {
            if (!config.isConfigurationSection(path)) {
                String value = config.getString(path);
                messages.put(path, value);
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + path + " -> " + value);
            }
            else if (path.contains("item-")) {
                try {
                    ConfigurationSection itemConfig = config.getConfigurationSection(path);
                    if (itemConfig != null) {
                        items.put(path, parseItem(itemConfig));
                    }
                }
                catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error loading " + path);
                    e.printStackTrace();
                }
            }
        }
    }

    private ItemStack parseItem(ConfigurationSection config) throws Exception{
        //Parse item and material
        ItemStack item;
        String materialName = config.getString("item");
        if (materialName != null) {
            XMaterial xMaterial = XMaterial.valueOf(materialName.toUpperCase());
            item = xMaterial.parseItem();
        }
        else {
            item = new ItemStack(Material.DIRT);
        }
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String name = config.getString("name");
                if (name != null && !name.equals("")) {
                    meta.setDisplayName(name.replace('&', '§'));
                }
                List<String> lore = config.getStringList("lore");
                if (lore.size() > 0) {
                    for (int i = 0; i < lore.size(); i++) {
                        lore.set(i, lore.get(i).replace('&', '§'));
                    }
                    meta.setLore(lore);
                }
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
