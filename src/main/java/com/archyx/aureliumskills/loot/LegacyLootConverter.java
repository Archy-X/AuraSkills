package com.archyx.aureliumskills.loot;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.util.file.FileUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class LegacyLootConverter {

    private final AureliumSkills plugin;

    public LegacyLootConverter(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public void convertLegacyFile() {
        File legacyFile = new File(plugin.getDataFolder(), "loot.yml");
        if (!legacyFile.exists()) { // Don't convert if doesn't exist
            return;
        }
        // Convert fishing loot tables
        FileConfiguration legacyConfig = YamlConfiguration.loadConfiguration(legacyFile);
        File fishingFile = new File(plugin.getDataFolder() + "/loot/fishing.yml");
        FileConfiguration fishingConfig = YamlConfiguration.loadConfiguration(fishingFile);
        // Fishing Rare
        List<String> fishingRareLoot = legacyConfig.getStringList("lootTables.fishing-rare");
        ConfigurationSection fishingRareSection = fishingConfig.getConfigurationSection("pools.rare");
        convertSection(fishingRareSection, fishingRareLoot);
        // Fishing Epic
        List<String> fishingEpicLoot = legacyConfig.getStringList("lootTables.fishing-epic");
        ConfigurationSection fishingEpicSection = fishingConfig.getConfigurationSection("pools.epic");
        convertSection(fishingEpicSection, fishingEpicLoot);
        // Convert excavation loot tables
        File excavationFile = new File(plugin.getDataFolder() + "/loot/excavation.yml");
        FileConfiguration excavationConfig = YamlConfiguration.loadConfiguration(excavationFile);
        // Excavation rare
        List<String> excavationRareLoot = legacyConfig.getStringList("lootTables.excavation-rare");
        ConfigurationSection excavationRareSection = excavationConfig.getConfigurationSection("pools.rare");
        convertSection(excavationRareSection, excavationRareLoot);
        // Excavation epic
        List<String> excavationEpicLoot = legacyConfig.getStringList("lootTables.excavation-epic");
        ConfigurationSection excavationEpicSection = excavationConfig.getConfigurationSection("pools.epic");
        convertSection(excavationEpicSection, excavationEpicLoot);

        renameLegacyFile(legacyFile);
        plugin.getLogger().info("Converted old loot.yml file to new files in the loot folder");
    }

    private void convertSection(ConfigurationSection section, List<String> legacyLoot) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (String entry : legacyLoot) {
            Map<String, Object> map = new HashMap<>();
            if (entry.startsWith("cmd:")) { // Legacy command loot
                map.put("type", "command");
                String commandString = TextUtil.replace(entry, "cmd:", "");
                map.put("command", commandString);
                map.put("executor", "console");
            } else { // Item loot
                map.put("type", "item");
                String[] split = entry.split(" ");
                if (split.length < 3) {
                    plugin.getLogger().warning("Failed to convert the following legacy loot entry because min, max, and material were not specified: " + entry);
                    continue;
                }
                int minAmount = NumberUtils.toInt(split[0]);
                int maxAmount = NumberUtils.toInt(split[1]);
                String materialName = split[2];
                String[] splitMaterial = materialName.split(":");
                Material material = Material.getMaterial(splitMaterial[0].toUpperCase(Locale.ROOT));
                short data = -1;
                if (splitMaterial.length > 1) {
                    data = NumberUtils.toShort(splitMaterial[1]);
                }
                if (material == null) {
                    plugin.getLogger().warning("Failed to convert a legacy loot entry, unknown material " + splitMaterial[0]);
                    continue;
                }
                if (data == -1) { // New version
                    map.put("item", material.toString().toLowerCase(Locale.ROOT));
                } else { // Legacy version
                    map.put("item", material.toString().toLowerCase(Locale.ROOT) + ":" + data);
                }
                if (minAmount == maxAmount) {
                    map.put("amount", minAmount);
                } else {
                    map.put("amount", minAmount + "-" + maxAmount);
                }
                // TODO Implement complex meta conversion
            }
            mapList.add(map);
        }
        section.set("loot", mapList);
    }

    private void renameLegacyFile(File file) {
        String renamedName = FileUtil.renameNoDuplicates(file, "loot-OLD.yml", plugin.getDataFolder());
        if (renamedName != null) {
            plugin.getLogger().info("Successfully renamed loot.yml to " + renamedName);
        } else {
            plugin.getLogger().warning("Failed to renamed old loot.yml file");
        }
    }

}
