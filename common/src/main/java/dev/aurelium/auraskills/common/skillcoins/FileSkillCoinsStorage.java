package dev.aurelium.auraskills.common.skillcoins;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * File-based storage implementation for SkillCoins data
 * Stores currency balances in YAML files
 */
public class FileSkillCoinsStorage implements SkillCoinsStorage {
    
    private final AuraSkillsPlugin plugin;
    private final File dataFolder;
    private final Map<UUID, Map<CurrencyType, Double>> pendingSaves;
    
    public FileSkillCoinsStorage(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getPluginFolder(), "skillcoins");
        this.pendingSaves = new ConcurrentHashMap<>();
    }
    
    @Override
    public void initialize() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }
    
    @Override
    public Map<CurrencyType, Double> load(UUID uuid) {
        Map<CurrencyType, Double> balances = new HashMap<>();
        File playerFile = getPlayerFile(uuid);
        
        if (!playerFile.exists()) {
            // Initialize with default values
            for (CurrencyType type : CurrencyType.values()) {
                balances.put(type, 0.0);
            }
            return balances;
        }
        
        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .file(playerFile)
                    .build();
            ConfigurationNode root = loader.load();
            
            for (CurrencyType type : CurrencyType.values()) {
                double balance = root.node(type.name().toLowerCase()).getDouble(0.0);
                balances.put(type, balance);
            }
        } catch (IOException e) {
            plugin.logger().severe("Failed to load SkillCoins data for " + uuid);
            e.printStackTrace();
            // Return default values on error
            for (CurrencyType type : CurrencyType.values()) {
                balances.put(type, 0.0);
            }
        }
        
        return balances;
    }
    
    @Override
    public void save(UUID uuid, CurrencyType type, double amount) {
        File playerFile = getPlayerFile(uuid);
        
        try {
            if (!playerFile.exists()) {
                playerFile.getParentFile().mkdirs();
                playerFile.createNewFile();
            }
            
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .file(playerFile)
                    .build();
            ConfigurationNode root = loader.load();
            
            root.node(type.name().toLowerCase()).set(amount);
            
            loader.save(root);
        } catch (IOException e) {
            plugin.logger().severe("Failed to save SkillCoins data for " + uuid);
            e.printStackTrace();
        }
    }
    
    @Override
    public void saveAsync(UUID uuid, CurrencyType type, double amount) {
        // Store in pending saves map
        pendingSaves.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(type, amount);
        
        // Execute save asynchronously
        plugin.getScheduler().executeAsync(() -> {
            save(uuid, type, amount);
            // Remove from pending after save
            Map<CurrencyType, Double> playerPending = pendingSaves.get(uuid);
            if (playerPending != null) {
                playerPending.remove(type);
                if (playerPending.isEmpty()) {
                    pendingSaves.remove(uuid);
                }
            }
        });
    }
    
    @Override
    public void close() {
        // Save any pending data
        for (Map.Entry<UUID, Map<CurrencyType, Double>> entry : pendingSaves.entrySet()) {
            UUID uuid = entry.getKey();
            for (Map.Entry<CurrencyType, Double> balanceEntry : entry.getValue().entrySet()) {
                save(uuid, balanceEntry.getKey(), balanceEntry.getValue());
            }
        }
        pendingSaves.clear();
    }
    
    private File getPlayerFile(UUID uuid) {
        return new File(dataFolder, uuid.toString() + ".yml");
    }
}
