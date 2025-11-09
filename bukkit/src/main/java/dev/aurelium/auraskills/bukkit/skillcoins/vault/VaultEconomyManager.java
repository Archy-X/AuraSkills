package dev.aurelium.auraskills.bukkit.skillcoins.vault;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.skillcoins.EconomyProvider;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.util.Collection;
import java.util.logging.Level;

/**
 * Manages the registration of SkillCoins as a Vault economy provider
 * Includes conflict detection and error handling
 */
public class VaultEconomyManager {
    
    private final AuraSkills plugin;
    private final EconomyProvider economyProvider;
    private SkillCoinsEconomyProvider skillCoinsEconomy;
    private boolean registered = false;
    
    // Configuration
    private boolean enabled = true;
    private boolean useCoins = true; // true = expose COINS, false = expose TOKENS
    private boolean forceOverride = false; // If true, override other economy plugins
    private boolean throwErrorOnConflict = true; // If true, throw error when detecting conflicts
    
    public VaultEconomyManager(AuraSkills plugin, EconomyProvider economyProvider) {
        this.plugin = plugin;
        this.economyProvider = economyProvider;
    }
    
    /**
     * Configure the economy manager
     * @param enabled Whether to enable Vault integration
     * @param useCoins If true, expose COINS; if false, expose TOKENS
     * @param forceOverride If true, override other economy plugins
     * @param throwErrorOnConflict If true, throw errors when conflicts detected
     */
    public void configure(boolean enabled, boolean useCoins, boolean forceOverride, boolean throwErrorOnConflict) {
        this.enabled = enabled;
        this.useCoins = useCoins;
        this.forceOverride = forceOverride;
        this.throwErrorOnConflict = throwErrorOnConflict;
    }
    
    /**
     * Register SkillCoins as a Vault economy provider
     * @return true if registration successful, false otherwise
     */
    public boolean register() {
        if (!enabled) {
            plugin.getLogger().info("SkillCoins Vault integration is disabled in config");
            return false;
        }
        
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
            plugin.getLogger().warning("Vault not found! SkillCoins economy provider will not be registered.");
            plugin.getLogger().warning("Install Vault to allow other plugins to use SkillCoins as an economy.");
            return false;
        }
        
        try {
            // Check for existing economy providers
            ServicesManager sm = plugin.getServer().getServicesManager();
            Collection<RegisteredServiceProvider<Economy>> providers = sm.getRegistrations(Economy.class);
            
            if (!providers.isEmpty()) {
                handleExistingProviders(providers);
            }
            
            // Create and register our economy provider
            skillCoinsEconomy = new SkillCoinsEconomyProvider(plugin, economyProvider, useCoins);
            
            ServicePriority priority = forceOverride ? ServicePriority.Highest : ServicePriority.Normal;
            sm.register(Economy.class, skillCoinsEconomy, plugin, priority);
            
            registered = true;
            
            String currencyType = useCoins ? "COINS" : "TOKENS";
            plugin.getLogger().info("╔════════════════════════════════════════════════════════════╗");
            plugin.getLogger().info("║  SkillCoins Vault Economy Provider REGISTERED             ║");
            plugin.getLogger().info("╠════════════════════════════════════════════════════════════╣");
            plugin.getLogger().info("║  Currency Type: " + String.format("%-41s", currencyType) + "║");
            plugin.getLogger().info("║  Priority: " + String.format("%-46s", priority.name()) + "║");
            plugin.getLogger().info("║  Other plugins can now use SkillCoins as economy!         ║");
            plugin.getLogger().info("╚════════════════════════════════════════════════════════════╝");
            
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to register SkillCoins economy provider!", e);
            return false;
        }
    }
    
    /**
     * Handle detection of existing economy providers
     */
    private void handleExistingProviders(Collection<RegisteredServiceProvider<Economy>> providers) {
        StringBuilder message = new StringBuilder();
        message.append("\n╔════════════════════════════════════════════════════════════╗\n");
        message.append("║  ⚠ WARNING: ECONOMY PROVIDER CONFLICT DETECTED ⚠          ║\n");
        message.append("╠════════════════════════════════════════════════════════════╣\n");
        message.append("║  Found existing economy provider(s):                       ║\n");
        
        for (RegisteredServiceProvider<Economy> provider : providers) {
            Plugin providerPlugin = provider.getPlugin();
            Economy economy = provider.getProvider();
            String pluginName = providerPlugin != null ? providerPlugin.getName() : "Unknown";
            String economyName = economy != null ? economy.getName() : "Unknown";
            
            message.append("║    • Plugin: ").append(String.format("%-43s", pluginName)).append("║\n");
            message.append("║      Economy: ").append(String.format("%-42s", economyName)).append("║\n");
        }
        
        message.append("╠════════════════════════════════════════════════════════════╣\n");
        
        if (forceOverride) {
            message.append("║  SkillCoins will OVERRIDE the existing provider(s)        ║\n");
            message.append("║  Priority: HIGHEST                                         ║\n");
            message.append("║  Other plugins will use SkillCoins instead!                ║\n");
            message.append("╚════════════════════════════════════════════════════════════╝");
            plugin.getLogger().warning(message.toString());
        } else {
            message.append("║  SkillCoins will register with NORMAL priority            ║\n");
            message.append("║  The existing provider may take precedence                 ║\n");
            message.append("║  Set 'force-override: true' to override other providers    ║\n");
            message.append("╚════════════════════════════════════════════════════════════╝");
            
            if (throwErrorOnConflict) {
                plugin.getLogger().severe(message.toString());
                throw new IllegalStateException(
                    "Economy provider conflict detected! " +
                    "Another economy plugin is already registered. " +
                    "Set 'throw-error-on-conflict: false' in config to allow this, " +
                    "or set 'force-override: true' to override the existing provider."
                );
            } else {
                plugin.getLogger().warning(message.toString());
            }
        }
    }
    
    /**
     * Unregister the economy provider
     */
    public void unregister() {
        if (!registered || skillCoinsEconomy == null) {
            return;
        }
        
        try {
            plugin.getServer().getServicesManager().unregister(Economy.class, skillCoinsEconomy);
            registered = false;
            plugin.getLogger().info("SkillCoins economy provider unregistered");
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error unregistering economy provider", e);
        }
    }
    
    /**
     * Check if the economy provider is registered
     */
    public boolean isRegistered() {
        return registered;
    }
    
    /**
     * Get the registered economy provider
     */
    public SkillCoinsEconomyProvider getEconomyProvider() {
        return skillCoinsEconomy;
    }
}
