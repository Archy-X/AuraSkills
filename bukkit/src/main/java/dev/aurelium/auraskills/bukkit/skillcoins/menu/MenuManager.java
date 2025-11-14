package dev.aurelium.auraskills.bukkit.skillcoins.menu;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Centralized menu event handler with enhanced error handling and thread-safety.
 * This singleton class manages ALL menu interactions through a single event listener.
 * 
 * Features:
 * - Thread-safe menu registration using ConcurrentHashMap
 * - Comprehensive null checking and error logging
 * - Defensive programming to prevent crashes
 * - Proper event priority handling
 * - Graceful error recovery
 */
public class MenuManager implements Listener {
    
    private static MenuManager instance;
    private final AuraSkills plugin;
    
    // Thread-safe maps for tracking active menus by player UUID
    private final Map<UUID, ShopMainMenu> mainMenus = new ConcurrentHashMap<>();
    private final Map<UUID, ShopSectionMenu> sectionMenus = new ConcurrentHashMap<>();
    private final Map<UUID, TransactionMenu> transactionMenus = new ConcurrentHashMap<>();
    private final Map<UUID, TokenExchangeMenu> tokenMenus = new ConcurrentHashMap<>();
    private final Map<UUID, SkillLevelPurchaseMenu> skillMenus = new ConcurrentHashMap<>();
    private final Map<UUID, SellMenu> sellMenus = new ConcurrentHashMap<>();
    
    private MenuManager(AuraSkills plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("AuraSkills plugin instance cannot be null");
        }
        this.plugin = plugin;
    }
    
    /**
     * Get or create the singleton MenuManager instance (thread-safe)
     * @param plugin The AuraSkills plugin instance
     * @return The MenuManager singleton
     */
    public static synchronized MenuManager getInstance(AuraSkills plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Cannot get MenuManager instance with null plugin");
        }
        
        if (instance == null) {
            instance = new MenuManager(plugin);
            plugin.getServer().getPluginManager().registerEvents(instance, plugin);
            plugin.getLogger().info("MenuManager initialized successfully");
        }
        return instance;
    }
    
    /**
     * Register menus for a player (with validation)
     */
    public void registerMainMenu(Player player, ShopMainMenu menu) {
        if (player == null || menu == null) {
            plugin.getLogger().warning("Attempted to register null main menu or player");
            return;
        }
        mainMenus.put(player.getUniqueId(), menu);
    }
    
    public void registerSectionMenu(Player player, ShopSectionMenu menu) {
        if (player == null || menu == null) {
            plugin.getLogger().warning("Attempted to register null section menu or player");
            return;
        }
        sectionMenus.put(player.getUniqueId(), menu);
    }
    
    public void registerTransactionMenu(Player player, TransactionMenu menu) {
        if (player == null || menu == null) {
            plugin.getLogger().warning("Attempted to register null transaction menu or player");
            return;
        }
        transactionMenus.put(player.getUniqueId(), menu);
    }
    
    public void registerTokenMenu(Player player, TokenExchangeMenu menu) {
        if (player == null || menu == null) {
            plugin.getLogger().warning("Attempted to register null token menu or player");
            return;
        }
        tokenMenus.put(player.getUniqueId(), menu);
    }
    
    public void registerSkillMenu(Player player, SkillLevelPurchaseMenu menu) {
        if (player == null || menu == null) {
            plugin.getLogger().warning("Attempted to register null skill menu or player");
            return;
        }
        skillMenus.put(player.getUniqueId(), menu);
    }
    
    public void registerSellMenu(Player player, SellMenu menu) {
        if (player == null || menu == null) {
            plugin.getLogger().warning("Attempted to register null sell menu or player");
            return;
        }
        sellMenus.put(player.getUniqueId(), menu);
    }
    
    /**
     * Unregister all menus for a player (thread-safe cleanup)
     * @param playerId The player's UUID
     */
    public void unregisterPlayer(UUID playerId) {
        if (playerId == null) return;
        
        mainMenus.remove(playerId);
        sectionMenus.remove(playerId);
        transactionMenus.remove(playerId);
        tokenMenus.remove(playerId);
        skillMenus.remove(playerId);
        sellMenus.remove(playerId);
    }
    
    /**
     * Main click handler with comprehensive error handling
     * Uses HIGHEST priority to ensure we catch events before other plugins
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        try {
            // Validate event and player
            if (event == null || !(event.getWhoClicked() instanceof Player)) {
                return;
            }
            
            Player player = (Player) event.getWhoClicked();
            if (player == null) return;
            
            UUID playerId = player.getUniqueId();
            if (playerId == null) return;
            
            // Get inventory title safely
            String title = null;
            try {
                title = event.getView().getTitle();
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to get inventory title", e);
                return;
            }
            
            if (title == null) return;
            
            // Delegate to appropriate menu handler (order matters - check most specific first)
            if (handleTransactionMenu(playerId, title, event)) return;
            if (handleTokenMenu(playerId, title, event)) return;
            if (handleSkillMenu(playerId, title, event)) return;
            if (handleSectionMenu(playerId, title, event)) return;
            if (handleMainMenu(playerId, title, event)) return;
            if (handleSellMenu(playerId, title, event)) return;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Unexpected error in menu click handler", e);
            // Don't cancel event on unknown errors to prevent inventory lockup
        }
    }
    
    /**
     * Main close handler with comprehensive error handling
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        try {
            // Validate event and player
            if (event == null || !(event.getPlayer() instanceof Player)) {
                return;
            }
            
            Player player = (Player) event.getPlayer();
            if (player == null) return;
            
            UUID playerId = player.getUniqueId();
            if (playerId == null) return;
            
            // Get inventory title safely
            String title = null;
            try {
                title = event.getView().getTitle();
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to get inventory title on close", e);
                // Clean up all menus for safety
                unregisterPlayer(playerId);
                return;
            }
            
            if (title == null) {
                unregisterPlayer(playerId);
                return;
            }
            
            // Delegate to appropriate menu handler and cleanup
            handleTransactionClose(playerId, title, event);
            handleTokenClose(playerId, title, event);
            handleSkillClose(playerId, title, event);
            handleSectionClose(playerId, title, event);
            handleMainClose(playerId, title, event);
            handleSellClose(playerId, title, event);
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Unexpected error in menu close handler", e);
        }
    }
    
    // Helper methods for delegation with error handling
    
    private boolean handleTransactionMenu(UUID playerId, String title, InventoryClickEvent event) {
        try {
            TransactionMenu menu = transactionMenus.get(playerId);
            if (menu != null && menu.isMenuTitle(title)) {
                menu.handleClick(event);
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error in transaction menu click", e);
            transactionMenus.remove(playerId); // Remove broken menu
        }
        return false;
    }
    
    private boolean handleTokenMenu(UUID playerId, String title, InventoryClickEvent event) {
        try {
            TokenExchangeMenu menu = tokenMenus.get(playerId);
            if (menu != null && menu.isMenuTitle(title)) {
                menu.handleClick(event);
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error in token menu click", e);
            tokenMenus.remove(playerId);
        }
        return false;
    }
    
    private boolean handleSkillMenu(UUID playerId, String title, InventoryClickEvent event) {
        try {
            SkillLevelPurchaseMenu menu = skillMenus.get(playerId);
            if (menu != null && menu.isMenuTitle(title)) {
                menu.handleClick(event);
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error in skill menu click", e);
            skillMenus.remove(playerId);
        }
        return false;
    }
    
    private boolean handleSectionMenu(UUID playerId, String title, InventoryClickEvent event) {
        try {
            ShopSectionMenu menu = sectionMenus.get(playerId);
            if (menu != null && menu.isMenuTitle(title)) {
                menu.handleClick(event);
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error in section menu click", e);
            sectionMenus.remove(playerId);
        }
        return false;
    }
    
    private boolean handleMainMenu(UUID playerId, String title, InventoryClickEvent event) {
        try {
            ShopMainMenu menu = mainMenus.get(playerId);
            if (menu != null && menu.isMenuTitle(title)) {
                menu.handleClick(event);
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error in main menu click", e);
            mainMenus.remove(playerId);
        }
        return false;
    }
    
    private boolean handleSellMenu(UUID playerId, String title, InventoryClickEvent event) {
        try {
            SellMenu menu = sellMenus.get(playerId);
            if (menu != null && menu.isMenuTitle(title)) {
                menu.handleClick(event);
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error in sell menu click", e);
            sellMenus.remove(playerId);
        }
        return false;
    }
    
    private void handleTransactionClose(UUID playerId, String title, InventoryCloseEvent event) {
        try {
            TransactionMenu menu = transactionMenus.get(playerId);
            if (menu != null && menu.isMenuTitle(title)) {
                menu.handleClose(event);
                transactionMenus.remove(playerId);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error closing transaction menu", e);
            transactionMenus.remove(playerId);
        }
    }
    
    private void handleTokenClose(UUID playerId, String title, InventoryCloseEvent event) {
        try {
            TokenExchangeMenu menu = tokenMenus.get(playerId);
            if (menu != null && menu.isMenuTitle(title)) {
                menu.handleClose(event);
                tokenMenus.remove(playerId);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error closing token menu", e);
            tokenMenus.remove(playerId);
        }
    }
    
    private void handleSkillClose(UUID playerId, String title, InventoryCloseEvent event) {
        try {
            SkillLevelPurchaseMenu menu = skillMenus.get(playerId);
            if (menu != null && menu.isMenuTitle(title)) {
                menu.handleClose(event);
                // DON'T remove here - let the menu's handleClose decide when to cleanup
                // skillMenus.remove(playerId); // REMOVED - causes menu to disappear when navigating between screens
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error closing skill menu", e);
            skillMenus.remove(playerId);
        }
    }
    
    private void handleSectionClose(UUID playerId, String title, InventoryCloseEvent event) {
        try {
            ShopSectionMenu menu = sectionMenus.get(playerId);
            if (menu != null && menu.isMenuTitle(title)) {
                menu.handleClose(event);
                sectionMenus.remove(playerId);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error closing section menu", e);
            sectionMenus.remove(playerId);
        }
    }
    
    private void handleMainClose(UUID playerId, String title, InventoryCloseEvent event) {
        try {
            ShopMainMenu menu = mainMenus.get(playerId);
            if (menu != null && menu.isMenuTitle(title)) {
                menu.handleClose(event);
                mainMenus.remove(playerId);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error closing main menu", e);
            mainMenus.remove(playerId);
        }
    }
    
    private void handleSellClose(UUID playerId, String title, InventoryCloseEvent event) {
        try {
            SellMenu menu = sellMenus.get(playerId);
            if (menu != null && menu.isMenuTitle(title)) {
                menu.handleClose(event);
                sellMenus.remove(playerId);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error closing sell menu", e);
            sellMenus.remove(playerId);
        }
    }
}
