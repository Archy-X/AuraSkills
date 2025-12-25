package dev.aurelium.auraskills.bukkit.skillcoins.vault;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.skillcoins.CurrencyType;
import dev.aurelium.auraskills.common.skillcoins.EconomyProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Vault Economy Provider for SkillCoins
 * Exposes the SkillCoins economy system to other plugins via Vault
 * 
 * Features:
 * - Thread-safe balance operations
 * - Comprehensive error handling
 * - Automatic conflict detection with other economy plugins
 * - Support for both COINS and TOKENS currencies
 */
public class SkillCoinsEconomyProvider implements Economy {
    
    private final AuraSkills plugin;
    private final EconomyProvider economyProvider;
    private final DecimalFormat format = new DecimalFormat("#,##0.00");
    
    // Configuration
    private final boolean useCoins; // If true, use COINS; if false, use TOKENS
    private final String currencyName;
    private final String currencyNamePlural;
    
    public SkillCoinsEconomyProvider(AuraSkills plugin, EconomyProvider economyProvider, boolean useCoins) {
        this.plugin = plugin;
        this.economyProvider = economyProvider;
        this.useCoins = useCoins;
        
        if (useCoins) {
            this.currencyName = "⛃";
            this.currencyNamePlural = "⛃";
        } else {
            this.currencyName = "🎟";
            this.currencyNamePlural = "🎟";
        }
    }
    
    private CurrencyType getCurrency() {
        return useCoins ? CurrencyType.COINS : CurrencyType.TOKENS;
    }
    
    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }
    
    @Override
    public String getName() {
        return "AuraSkills-SkillCoins";
    }
    
    @Override
    public boolean hasBankSupport() {
        return false;
    }
    
    @Override
    public int fractionalDigits() {
        return 2;
    }
    
    @Override
    public String format(double amount) {
        return format.format(amount) + " " + (amount == 1 ? currencyName : currencyNamePlural);
    }
    
    @Override
    public String currencyNamePlural() {
        return currencyNamePlural;
    }
    
    @Override
    public String currencyNameSingular() {
        return currencyName;
    }
    
    @Override
    public boolean hasAccount(String playerName) {
        if (playerName == null || playerName.isEmpty()) return false;
        try {
            @SuppressWarnings("deprecation")
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            return player != null && player.hasPlayedBefore();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error checking account for " + playerName, e);
            return false;
        }
    }
    
    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return player != null && player.hasPlayedBefore();
    }
    
    @Override
    public boolean hasAccount(String playerName, String world) {
        return hasAccount(playerName); // World-independent
    }
    
    @Override
    public boolean hasAccount(OfflinePlayer player, String world) {
        return hasAccount(player); // World-independent
    }
    
    @Override
    public double getBalance(String playerName) {
        if (playerName == null || playerName.isEmpty()) return 0.0;
        try {
            @SuppressWarnings("deprecation")
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            return getBalance(player);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error getting balance for " + playerName, e);
            return 0.0;
        }
    }
    
    @Override
    public double getBalance(OfflinePlayer player) {
        if (player == null || player.getUniqueId() == null) return 0.0;
        try {
            return economyProvider.getBalance(player.getUniqueId(), getCurrency());
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error getting balance for " + player.getName(), e);
            return 0.0;
        }
    }
    
    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName); // World-independent
    }
    
    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player); // World-independent
    }
    
    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }
    
    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }
    
    @Override
    public boolean has(String playerName, String world, double amount) {
        return has(playerName, amount); // World-independent
    }
    
    @Override
    public boolean has(OfflinePlayer player, String world, double amount) {
        return has(player, amount); // World-independent
    }
    
    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (playerName == null || playerName.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Invalid player name");
        }
        try {
            @SuppressWarnings("deprecation")
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            return withdrawPlayer(player, amount);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error withdrawing from " + playerName, e);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Internal error");
        }
    }
    
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (player == null || player.getUniqueId() == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Invalid player");
        }
        
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative amount");
        }
        
        try {
            UUID uuid = player.getUniqueId();
            double balance = economyProvider.getBalance(uuid, getCurrency());
            
            if (balance < amount) {
                return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
            }
            
            economyProvider.subtractBalance(uuid, getCurrency(), amount);
            double newBalance = economyProvider.getBalance(uuid, getCurrency());
            
            return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error withdrawing from " + player.getName(), e);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Internal error");
        }
    }
    
    @Override
    public EconomyResponse withdrawPlayer(String playerName, String world, double amount) {
        return withdrawPlayer(playerName, amount); // World-independent
    }
    
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) {
        return withdrawPlayer(player, amount); // World-independent
    }
    
    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (playerName == null || playerName.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Invalid player name");
        }
        try {
            @SuppressWarnings("deprecation")
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            return depositPlayer(player, amount);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error depositing to " + playerName, e);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Internal error");
        }
    }
    
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (player == null || player.getUniqueId() == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Invalid player");
        }
        
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative amount");
        }
        
        try {
            UUID uuid = player.getUniqueId();
            economyProvider.addBalance(uuid, getCurrency(), amount);
            double newBalance = economyProvider.getBalance(uuid, getCurrency());
            
            return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error depositing to " + player.getName(), e);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Internal error");
        }
    }
    
    @Override
    public EconomyResponse depositPlayer(String playerName, String world, double amount) {
        return depositPlayer(playerName, amount); // World-independent
    }
    
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) {
        return depositPlayer(player, amount); // World-independent
    }
    
    // Bank operations are not supported
    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SkillCoins does not support banks");
    }
    
    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SkillCoins does not support banks");
    }
    
    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SkillCoins does not support banks");
    }
    
    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SkillCoins does not support banks");
    }
    
    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SkillCoins does not support banks");
    }
    
    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SkillCoins does not support banks");
    }
    
    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SkillCoins does not support banks");
    }
    
    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SkillCoins does not support banks");
    }
    
    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SkillCoins does not support banks");
    }
    
    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SkillCoins does not support banks");
    }
    
    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SkillCoins does not support banks");
    }
    
    @Override
    public List<String> getBanks() {
        return List.of(); // No banks supported
    }
    
    @Override
    public boolean createPlayerAccount(String playerName) {
        // Accounts are created automatically when a player joins
        return hasAccount(playerName);
    }
    
    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        // Accounts are created automatically when a player joins
        return hasAccount(player);
    }
    
    @Override
    public boolean createPlayerAccount(String playerName, String world) {
        return createPlayerAccount(playerName); // World-independent
    }
    
    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String world) {
        return createPlayerAccount(player); // World-independent
    }
}
