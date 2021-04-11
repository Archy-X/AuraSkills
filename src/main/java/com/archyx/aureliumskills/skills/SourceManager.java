package com.archyx.aureliumskills.skills;

import com.archyx.aureliumskills.AureliumSkills;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class SourceManager {

    private final AureliumSkills plugin;
    private final Map<Source, Double> sources;
    private Map<Skill, Map<XMaterial, Double>> customBlocks;
    private Map<Skill, Map<String, Double>> customMobs;
    private Set<XMaterial> customBlockSet;
    private Set<String> customMobSet;

    public SourceManager(AureliumSkills plugin) {
        this.plugin = plugin;
        this.sources = new HashMap<>();
    }

    public void loadSources() {
        long start = System.currentTimeMillis();
        // Create file
        File file = new File(plugin.getDataFolder(), "sources_config.yml");
        if (!file.exists()) {
            plugin.saveResource("sources_config.yml", false);
        }
        // Load FileConfigurations
        FileConfiguration config = updateFile(file, YamlConfiguration.loadConfiguration(file));
        // Load sources
        int sourcesLoaded = 0;
        for (Source source : Source.values()) {
            String path = source.getPath();
            // Add if exists
            if (config.contains("sources." + path)) {
                sources.put(source, config.getDouble("sources." + path));
                sourcesLoaded++;
            }
            // Otherwise add default value and write to config
            else {
                Bukkit.getLogger().warning("[AureliumSkills] sources_config.yml is missing source of path sources." + path + ", value has been set to 0");
                sources.put(source, 0.0);
            }
        }
        // Load custom blocks
        customBlocks = new HashMap<>();
        customBlockSet = new HashSet<>();
        Skill[] customBlockSkills = new Skill[] {Skills.FARMING, Skills.FORAGING, Skills.MINING, Skills.EXCAVATION};
        for (Skill skill : customBlockSkills) {
            ConfigurationSection section = config.getConfigurationSection("sources." + skill.toString().toLowerCase(Locale.ENGLISH) + ".custom");
            if (section != null) {
                Map<XMaterial, Double> blockMap = new HashMap<>();
                for (String key : section.getKeys(false)) {
                    double value = section.getDouble(key);
                    Optional<XMaterial> optionalMaterial = XMaterial.matchXMaterial(key.toUpperCase());
                    if (optionalMaterial.isPresent()) {
                        XMaterial material = optionalMaterial.get();
                        blockMap.put(material, value);
                        customBlockSet.add(material);
                        sourcesLoaded++;
                    }
                    else {
                        Bukkit.getLogger().warning("[AureliumSkills] Custom block " + key + " is not a valid block!");
                    }
                }
                customBlocks.put(skill, blockMap);
            }
        }
        // Load custom mobs
        customMobs = new HashMap<>();
        customMobSet = new HashSet<>();
        Skill[] customMobSkills = new Skill[] {Skills.FIGHTING, Skills.ARCHERY};
        for (Skill skill : customMobSkills) {
            ConfigurationSection section = config.getConfigurationSection("sources." + skill.toString().toLowerCase(Locale.ENGLISH) + ".custom");
            if (section != null) {
                Map<String, Double> mobMap = new HashMap<>();
                for (String key : section.getKeys(false)) {
                    double value = section.getDouble(key);
                    mobMap.put(key, value);
                    customMobSet.add(key);
                    sourcesLoaded++;
                }
                customMobs.put(skill, mobMap);
            }
        }
        Bukkit.getLogger().info("[AureliumSkills] Loaded " + sourcesLoaded + " sources in " + (System.currentTimeMillis() - start) + "ms");
    }

    private FileConfiguration updateFile(File file, FileConfiguration config) {
        if (config.contains("file_version")) {
            InputStream stream = plugin.getResource("sources_config.yml");
            if (stream != null) {
                int currentVersion = config.getInt("file_version");
                FileConfiguration imbConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
                int imbVersion = imbConfig.getInt("file_version");
                //If versions do not match
                if (currentVersion != imbVersion) {
                    try {
                        ConfigurationSection configSection = imbConfig.getConfigurationSection("");
                        int keysAdded = 0;
                        if (configSection != null) {
                            for (String key : configSection.getKeys(true)) {
                                if (!config.contains(key)) {
                                    config.set(key, imbConfig.get(key));
                                    keysAdded++;
                                }
                            }
                        }
                        config.set("file_version", imbVersion);
                        config.save(file);
                        Bukkit.getLogger().info("[AureliumSkills] sources_config.yml was updated to a new file version, " + keysAdded + " new keys were added.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public double getXp(Source source) {
        return sources.get(source);
    }

    public Map<XMaterial, Double> getCustomBlocks(Skill skill) {
        return customBlocks.get(skill);
    }

    public Set<XMaterial> getCustomBlockSet() {
        return customBlockSet;
    }

    public Map<String, Double> getCustomMobs(Skill skill) {
        return customMobs.get(skill);
    }

    public Set<String> getCustomMobSet() {
        return customMobSet;
    }

}
