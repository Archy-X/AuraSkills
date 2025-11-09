package dev.aurelium.auraskills.common.skillcoins;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the EconomyProvider
 * Stores currency balances in memory with file/database persistence
 */
public class SkillCoinsEconomy implements EconomyProvider {
    
    private final AuraSkillsPlugin plugin;
    private final Map<UUID, Map<CurrencyType, Double>> balances;
    private final SkillCoinsStorage storage;
    
    public SkillCoinsEconomy(AuraSkillsPlugin plugin, SkillCoinsStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
        this.balances = new ConcurrentHashMap<>();
    }
    
    @Override
    public double getBalance(UUID uuid, CurrencyType type) {
        return balances.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                .getOrDefault(type, 0.0);
    }
    
    @Override
    public void setBalance(UUID uuid, CurrencyType type, double amount) {
        balances.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                .put(type, Math.max(0, amount));
        // CRITICAL: Save immediately to disk on every change (prevents data loss)
        storage.save(uuid, type, amount);
    }
    
    @Override
    public void load(UUID uuid) {
        Map<CurrencyType, Double> playerBalances = storage.load(uuid);
        balances.put(uuid, new ConcurrentHashMap<>(playerBalances));
    }
    
    @Override
    public void save(UUID uuid) {
        Map<CurrencyType, Double> playerBalances = balances.get(uuid);
        if (playerBalances != null) {
            for (Map.Entry<CurrencyType, Double> entry : playerBalances.entrySet()) {
                storage.save(uuid, entry.getKey(), entry.getValue());
            }
        }
    }
    
    @Override
    public void unload(UUID uuid) {
        // Save before unloading
        save(uuid);
        balances.remove(uuid);
    }
    
    /**
     * Save all loaded player data
     */
    public void saveAll() {
        for (UUID uuid : balances.keySet()) {
            save(uuid);
        }
    }
}
