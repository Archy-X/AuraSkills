package dev.aurelium.auraskills.common.skillcoins;

import dev.aurelium.auraskills.common.user.User;

import java.util.UUID;

/**
 * Manages player currency balances (SkillCoins and SkillTokens)
 */
public interface EconomyProvider {
    
    /**
     * Get the balance of a specific currency type for a player
     * @param uuid Player UUID
     * @param type Currency type
     * @return Balance amount
     */
    double getBalance(UUID uuid, CurrencyType type);
    
    /**
     * Get the balance of a specific currency type for a user
     * @param user User object
     * @param type Currency type
     * @return Balance amount
     */
    default double getBalance(User user, CurrencyType type) {
        return getBalance(user.getUuid(), type);
    }
    
    /**
     * Set the balance of a specific currency type for a player
     * @param uuid Player UUID
     * @param type Currency type
     * @param amount New balance amount
     */
    void setBalance(UUID uuid, CurrencyType type, double amount);
    
    /**
     * Set the balance of a specific currency type for a user
     * @param user User object
     * @param type Currency type
     * @param amount New balance amount
     */
    default void setBalance(User user, CurrencyType type, double amount) {
        setBalance(user.getUuid(), type, amount);
    }
    
    /**
     * Add currency to a player's balance
     * @param uuid Player UUID
     * @param type Currency type
     * @param amount Amount to add
     * @return New balance
     */
    default double addBalance(UUID uuid, CurrencyType type, double amount) {
        double newBalance = getBalance(uuid, type) + amount;
        setBalance(uuid, type, newBalance);
        return newBalance;
    }
    
    /**
     * Add currency to a user's balance
     * @param user User object
     * @param type Currency type
     * @param amount Amount to add
     * @return New balance
     */
    default double addBalance(User user, CurrencyType type, double amount) {
        return addBalance(user.getUuid(), type, amount);
    }
    
    /**
     * Subtract currency from a player's balance
     * @param uuid Player UUID
     * @param type Currency type
     * @param amount Amount to subtract
     * @return New balance
     */
    default double subtractBalance(UUID uuid, CurrencyType type, double amount) {
        double newBalance = Math.max(0, getBalance(uuid, type) - amount);
        setBalance(uuid, type, newBalance);
        return newBalance;
    }
    
    /**
     * Subtract currency from a user's balance
     * @param user User object
     * @param type Currency type
     * @param amount Amount to subtract
     * @return New balance
     */
    default double subtractBalance(User user, CurrencyType type, double amount) {
        return subtractBalance(user.getUuid(), type, amount);
    }
    
    /**
     * Check if a player has enough currency
     * @param uuid Player UUID
     * @param type Currency type
     * @param amount Amount to check
     * @return true if player has enough currency
     */
    default boolean hasBalance(UUID uuid, CurrencyType type, double amount) {
        return getBalance(uuid, type) >= amount;
    }
    
    /**
     * Check if a user has enough currency
     * @param user User object
     * @param type Currency type
     * @param amount Amount to check
     * @return true if user has enough currency
     */
    default boolean hasBalance(User user, CurrencyType type, double amount) {
        return hasBalance(user.getUuid(), type, amount);
    }
    
    /**
     * Load currency data for a player
     * @param uuid Player UUID
     */
    void load(UUID uuid);
    
    /**
     * Save currency data for a player
     * @param uuid Player UUID
     */
    void save(UUID uuid);
    
    /**
     * Unload currency data for a player (when they disconnect)
     * @param uuid Player UUID
     */
    void unload(UUID uuid);
}
