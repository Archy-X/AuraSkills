package io.github.archy_x.aureliumskills.loot;

import io.github.archy_x.aureliumskills.AureliumSkills;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class LootTableManager {

	private Map<String, LootTable> lootTables;
	private File file;
	private FileConfiguration config;
	private Plugin plugin;
	
	public LootTableManager(Plugin plugin) {
		this.plugin = plugin;
		lootTables = new HashMap<>();
		file = new File(plugin.getDataFolder(), "loot.yml");
		if (!file.exists()) {
			plugin.saveResource("loot.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(file);
		matchConfig(config, file);
		loadLootTables();
	}
	
	public void loadLootTables() {
		file = new File(plugin.getDataFolder(), "loot.yml");
		config = YamlConfiguration.loadConfiguration(file);
		int itemsLoaded = 0;
		int lootTablesLoaded = 0;
		for (String lootTableName : config.getConfigurationSection("lootTables").getKeys(false)) {
			List<Loot> loot = new ArrayList<Loot>();
			for (String itemString : config.getStringList("lootTables." + lootTableName)) {
				try {
					String[] splitString = itemString.split(" ", 3);
					int minAmount = Integer.parseInt(splitString[0]);
					int maxAmount = Integer.parseInt(splitString[1]);
					ItemStack item = parse(splitString[2]);
					if (item != null) {
						loot.add(new Loot(item, minAmount, maxAmount));
						itemsLoaded++;
					}
				}
				catch (Exception e) {
					Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.RED + "Error loading loot " + itemString + " from loot.yml. Try checking if minimum and maximum amount are specified!");
				}
			}
			LootTable lootTable = new LootTable(lootTableName, loot);
			lootTables.put(lootTableName, lootTable);
			lootTablesLoaded++;
		}
		Bukkit.getLogger().info(AureliumSkills.tag + ChatColor.AQUA + "Loaded " + ChatColor.GOLD + itemsLoaded + ChatColor.AQUA + " items in " + ChatColor.GOLD + lootTablesLoaded + ChatColor.AQUA + " loot tables.");
	}
	
	public LootTable getLootTable(String name) {
		return lootTables.get(name);
	}
	
	public void matchConfig(FileConfiguration config, File file) {
		config.options().copyDefaults(true);
		try {
			boolean changesMade = false;
			InputStream is = plugin.getResource(file.getName());
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
            for (String key : defConfig.getConfigurationSection("").getKeys(true)) {
                if (!config.contains(key)) {
                	config.set(key, defConfig.get(key));
                	if (changesMade == false) {
                		changesMade = true;
                	}
                }
            }
            if (changesMade) {
            	config.save(file);
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack parse(String text) {
		try {
			String[] textArray = text.split(" ");
			ItemStack item = null;
			if (textArray.length >= 1) {
				if (textArray[0].split(":").length == 1) {
					item = new ItemStack(Material.getMaterial(textArray[0].toUpperCase()), 1);
				} else {
					String itemId = textArray[0].split(":")[0];
					short dataValue = Short.parseShort(textArray[0].split(":")[1]);
					item = new ItemStack(Material.getMaterial(itemId.toUpperCase()), 1, dataValue);
				}
			}
			ItemMeta meta = null;
			if (item != null) {
				meta = item.getItemMeta();
			}
			for (int i = 1; i < textArray.length; i++) {
				if (textArray[i].split(":").length == 2) {
					String key = textArray[i].split(":")[0];
					String value = textArray[i].split(":")[1].replace("_", " ").replace("&", "§");
					if (key.equals("name")) {
						meta.setDisplayName(value);
					} else if (key.equals("lore")) {
						List<String> lore = new LinkedList<>();
						for (String s : value.split("\\|")) {
							lore.add(s);
						}
						meta.setLore(lore);
					} else if (key.equals("glow")) {
						if (Boolean.parseBoolean(value) == true) {
							meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
							meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						}
					}
				}
			}
			item.setItemMeta(meta);
			return item;
		}
		catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.RED + "Error loading item " + text + " in loot.yml. Try checking if the material is valid!");
			return null;
		}
	}
	
}
