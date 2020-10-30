package com.archyx.aureliumskills.loot;

import com.archyx.aureliumskills.configuration.ASLogger;
import com.archyx.aureliumskills.configuration.LogType;
import com.archyx.aureliumskills.util.LoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
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

	private final Map<String, LootTable> lootTables;
	private File file;
	private FileConfiguration config;
	private final Plugin plugin;
	
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
		int commandsLoaded = 0;
		int lootTablesLoaded = 0;
		ConfigurationSection configurationSection = config.getConfigurationSection("lootTables");
		if (configurationSection != null) {
			for (String lootTableName : configurationSection.getKeys(false)) {
				List<Loot> loot = new ArrayList<>();
				for (String itemString : config.getStringList("lootTables." + lootTableName)) {
					try {
						if (itemString.startsWith("cmd:")) {
							String commandString = LoreUtil.replace(itemString, "cmd:", "");
							loot.add(new Loot(commandString));
							commandsLoaded++;
						}
						else {
							String[] splitString = itemString.split(" ", 3);
							int minAmount = Integer.parseInt(splitString[0]);
							int maxAmount = Integer.parseInt(splitString[1]);
							ItemStack item = parse(splitString[2]);
							if (item != null) {
								loot.add(new Loot(item, minAmount, maxAmount));
								itemsLoaded++;
							}
						}
					} catch (Exception e) {
						Bukkit.getLogger().warning("[AureliumSkills] Error loading loot " + itemString + " from loot.yml. Try checking if minimum and maximum amount are specified!");
					}
				}
				LootTable lootTable = new LootTable(lootTableName, loot);
				lootTables.put(lootTableName, lootTable);
				lootTablesLoaded++;
			}
		}
		Bukkit.getLogger().info("[AureliumSkills] Loaded " + itemsLoaded + " items in " + lootTablesLoaded + " loot tables.");
	}
	
	public LootTable getLootTable(String name) {
		return lootTables.get(name);
	}
	
	public void matchConfig(FileConfiguration config, File file) {
		config.options().copyDefaults(true);
		try {
			boolean changesMade = false;
			InputStream is = plugin.getResource(file.getName());
			if (is != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
				ConfigurationSection configurationSection = defConfig.getConfigurationSection("");
				if (configurationSection != null) {
					for (String key : configurationSection.getKeys(true)) {
						if (!config.contains(key)) {
							config.set(key, defConfig.get(key));
							if (!changesMade) {
								changesMade = true;
							}
						}
					}
					if (changesMade) {
						config.save(file);
					}
				}
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
					Material material = Material.getMaterial(textArray[0].toUpperCase());
					if (material != null) {
						item = new ItemStack(material, 1);
					}
				} else {
					String itemId = textArray[0].split(":")[0];
					short dataValue = Short.parseShort(textArray[0].split(":")[1]);
					Material material = Material.getMaterial(itemId.toUpperCase());
					if (material != null) {
						item = new ItemStack(material, 1, dataValue);
					}
				}
			}
			ItemMeta meta;
			if (item != null) {
				meta = item.getItemMeta();
				if (meta != null) {
					for (int i = 1; i < textArray.length; i++) {
						if (textArray[i].split(":", 2).length == 2) {
							String key = textArray[i].split(":")[0];
							String value = textArray[i].split(":")[1].replace("_", " ").replace("&", "§");
							switch (key) {
								case "name":
									meta.setDisplayName(value);
									break;
								case "lore":
									List<String> lore = new LinkedList<>(Arrays.asList(value.split("\\|")));
									meta.setLore(lore);
									break;
								case "glow":
									if (Boolean.parseBoolean(value)) {
										meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
										meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
									}
									break;
								case "enchantments":
									List<String> enchantments = new ArrayList<>(Arrays.asList(value.split("\\n")));
									for (String enchantString : enchantments) {
										String[] splitEnchantString = enchantString.split(":", 2);
										String enchantName = enchantString.split(":")[0];
										int enchantLevel = 1;
										if (splitEnchantString.length == 2) {
											enchantLevel = Integer.parseInt(splitEnchantString[1]);
										}
										Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantName));
										if (enchantment != null) {
											meta.addEnchant(enchantment, enchantLevel, true);
										}
										else {
											ASLogger.logWarn(LogType.UNKNOWN_ENCHANTMENT, "Enchantment " + enchantName + " invalid for item with input " + text);
										}
									}
									break;
							}
						}
					}
				}
				item.setItemMeta(meta);
			}
			return item;
		}
		catch (Exception e) {
			Bukkit.getLogger().warning("[AureliumSkills] Error loading item " + text + " from loot.yml. Try checking if the material is valid!");
			return null;
		}
	}
	
}
