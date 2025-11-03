package dev.aurelium.auraskills.common.skillcoins;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;

import java.util.Map;
import java.util.UUID;

/**
 * Interface for persisting currency data to storage
 */
public interface SkillCoinsStorage {
    
    /**
     * Load all currency balances for a player
     * @param uuid Player UUID
     * @return Map of currency types to balances
     */
    Map<CurrencyType, Double> load(UUID uuid);
    
    /**
     * Save a specific currency balance for a player
     * @param uuid Player UUID
     * @param type Currency type
     * @param amount Balance amount
     */
    void save(UUID uuid, CurrencyType type, double amount);
    
    /**
     * Save a specific currency balance asynchronously
     * @param uuid Player UUID
     * @param type Currency type
     * @param amount Balance amount
     */
    void saveAsync(UUID uuid, CurrencyType type, double amount);
    
    /**
     * Initialize the storage (create tables/files if needed)
     */
    void initialize();
    
    /**
     * Close the storage connection
     */
    void close();
}
