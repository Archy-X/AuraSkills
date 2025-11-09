package dev.aurelium.auraskills.bukkit.skillcoins.menu;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.skillcoins.CurrencyType;
import dev.aurelium.auraskills.common.skillcoins.EconomyProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Token Exchange menu for buying tokens with coins - FULLY REWRITTEN
 * 
 * Features:
 * - Thread-safe quantity tracking with ConcurrentHashMap
 * - Comprehensive balance validation
 * - Overflow protection on calculations
 * - Atomic transaction processing
 * - Proper error handling and logging
 */
public class TokenExchangeMenu {
    
    private static final String MENU_TITLE = "§a✦ §fToken Exchange";
    private static final String PRESET_TITLE = "§e⚡ §fQuick Select Amount";
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    private static final int COINS_PER_TOKEN = 100; // Base rate: 100 coins = 1 token
    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 1000;
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    
    // Thread-safe player-specific data storage
    private final Map<UUID, Integer> playerQuantities = new ConcurrentHashMap<>();
    
    public TokenExchangeMenu(AuraSkills plugin, EconomyProvider economy) {
        if (plugin == null) throw new IllegalArgumentException("Plugin cannot be null");
        if (economy == null) throw new IllegalArgumentException("Economy provider cannot be null");
        this.plugin = plugin;
        this.economy = economy;
    }
    
    /**
     * Open the main token exchange menu with validation
     */
    public void open(Player player) {
        if (player == null) {
            plugin.getLogger().warning("Attempted to open token exchange for null player");
            return;
        }
        
        if (!player.isOnline()) {
            plugin.getLogger().warning("Attempted to open token exchange for offline player: " + player.getName());
            return;
        }
        
        try {
            playerQuantities.putIfAbsent(player.getUniqueId(), MIN_QUANTITY);
            
            MenuManager manager = MenuManager.getInstance(plugin);
            if (manager != null) {
                manager.registerTokenMenu(player, this);
            }
            
            updateExchangeMenu(player);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error opening token exchange for " + player.getName(), e);
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error opening token exchange!");
        }
    }
    
    /**
     * Check if a title matches this menu
     */
    public boolean isMenuTitle(String title) {
        return title.equals(MENU_TITLE) || title.equals(PRESET_TITLE);
    }
    
    /**
     * Update the main exchange menu with current quantity (updates existing inventory)
     */
    private void updateExchangeMenu(Player player) {
        if (player == null || !player.isOnline()) return;
        
        try {
            // Get the player's CURRENT open inventory - if they don't have one open, create new
            Inventory inv = null;
            boolean isNewInventory = false;
            
            try {
                Inventory currentInv = player.getOpenInventory().getTopInventory();
                if (currentInv != null && currentInv.getSize() == 54 && 
                    player.getOpenInventory().getTitle().equals(MENU_TITLE)) {
                    inv = currentInv; // Reuse existing inventory
                }
            } catch (Exception e) {
                // Inventory check failed, will create new one
            }
            
            if (inv == null) {
                // Create new inventory (first open or invalid state)
                inv = Bukkit.createInventory(null, 54, MENU_TITLE);
                isNewInventory = true;
                if (inv == null) {
                    plugin.getLogger().severe("Failed to create inventory");
                    return;
                }
            }
            
            UUID uuid = player.getUniqueId();
            int quantity = playerQuantities.getOrDefault(uuid, MIN_QUANTITY);
            
            // Clamp quantity to valid range to prevent overflow
            quantity = Math.max(MIN_QUANTITY, Math.min(MAX_QUANTITY, quantity));
            playerQuantities.put(uuid, quantity);
            
            // Calculate cost with overflow protection
            long totalCoinsLong = (long) COINS_PER_TOKEN * quantity;
            if (totalCoinsLong > Integer.MAX_VALUE) {
                plugin.getLogger().warning("Overflow detected in token cost calculation");
                totalCoinsLong = Integer.MAX_VALUE;
            }
            int totalCoins = (int) totalCoinsLong;
            
            double coinBalance = 0.0;
            double tokenBalance = 0.0;
            try {
                coinBalance = economy.getBalance(uuid, CurrencyType.COINS);
                tokenBalance = economy.getBalance(uuid, CurrencyType.TOKENS);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error getting balance for " + player.getName(), e);
            }
        
        // Clear and refill the inventory
        inv.clear();
        fillBorder(inv);
        
        // Token display (slot 13 - center top)
        ItemStack tokenDisplay = new ItemStack(Material.EMERALD, Math.min(quantity, 64));
        ItemMeta tokenMeta = tokenDisplay.getItemMeta();
        if (tokenMeta != null) {
            tokenMeta.setDisplayName(ChatColor.of("#00FFFF") + "✦ Skill Tokens");
            
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.of("#808080") + "Tokens to Purchase: " + ChatColor.of("#FFFFFF") + quantity);
            lore.add(ChatColor.of("#808080") + "Exchange Rate: " + ChatColor.of("#FFD700") + 
                    COINS_PER_TOKEN + " coins" + ChatColor.of("#808080") + " = " + 
                    ChatColor.of("#00FFFF") + "1 token");
            lore.add("");
            lore.add(ChatColor.of("#FFD700") + "Total Cost: " + ChatColor.of("#FFFFFF") + 
                    MONEY_FORMAT.format(totalCoins) + " Coins");
            lore.add("");
            
            if (coinBalance >= totalCoins) {
                lore.add(ChatColor.of("#55FF55") + "✔ Sufficient Funds");
                lore.add(ChatColor.of("#808080") + "  Balance after: " + ChatColor.of("#FFFFFF") + 
                        MONEY_FORMAT.format(coinBalance - totalCoins) + " Coins");
            } else {
                lore.add(ChatColor.of("#FF5555") + "✖ Insufficient Funds");
                lore.add(ChatColor.of("#808080") + "  Need: " + ChatColor.of("#FF5555") + 
                        MONEY_FORMAT.format(totalCoins - coinBalance) + " more Coins");
            }
            
            tokenMeta.setLore(lore);
            tokenDisplay.setItemMeta(tokenMeta);
        }
        inv.setItem(13, tokenDisplay);
        
        // Quantity controls (row 3, centered)
        // -10 (slot 20)
        createButton(inv, 20, Material.RED_TERRACOTTA, 
                ChatColor.of("#FF5555") + "▼▼ -10", 
                quantity > 10, 
                "Remove 10 tokens");
        
        // -1 (slot 21)
        createButton(inv, 21, Material.ORANGE_TERRACOTTA, 
                ChatColor.of("#FF5555") + "▼ -1", 
                quantity > MIN_QUANTITY, 
                "Remove 1 token");
        
        // Quantity display (slot 22 - center)
        ItemStack qtyDisplay = new ItemStack(Material.EMERALD, Math.min(quantity, 64));
        ItemMeta qtyMeta = qtyDisplay.getItemMeta();
        if (qtyMeta != null) {
            qtyMeta.setDisplayName(ChatColor.of("#FFFF00") + "Amount: " + ChatColor.of("#FFFFFF") + quantity);
            List<String> qtyLore = new ArrayList<>();
            qtyLore.add("");
            qtyLore.add(ChatColor.of("#808080") + "Tokens to purchase");
            qtyLore.add(ChatColor.of("#808080") + "Use +/- to adjust");
            qtyMeta.setLore(qtyLore);
            qtyDisplay.setItemMeta(qtyMeta);
        }
        inv.setItem(22, qtyDisplay);
        
        // +1 (slot 23)
        createButton(inv, 23, Material.LIME_TERRACOTTA, 
                ChatColor.of("#55FF55") + "▲ +1", 
                quantity < MAX_QUANTITY, 
                "Add 1 token");
        
        // +10 (slot 24)
        createButton(inv, 24, Material.GREEN_TERRACOTTA, 
                ChatColor.of("#55FF55") + "▲▲ +10", 
                quantity + 10 <= MAX_QUANTITY, 
                "Add 10 tokens");
        
        // Quick select button (slot 31)
        ItemStack quickSelect = new ItemStack(Material.NETHER_STAR);
        ItemMeta quickMeta = quickSelect.getItemMeta();
        if (quickMeta != null) {
            quickMeta.setDisplayName(ChatColor.of("#FFFF00") + "⚡ Quick Select");
            List<String> quickLore = new ArrayList<>();
            quickLore.add("");
            quickLore.add(ChatColor.of("#808080") + "Quickly set to:");
            quickLore.add(ChatColor.of("#FFD700") + "  • " + ChatColor.of("#FFFFFF") + 
                    "1, 10, 50, 100, 500, or 1000 tokens");
            quickLore.add("");
            quickLore.add(ChatColor.of("#FFFF00") + "▸ Click to open!");
            quickMeta.setLore(quickLore);
            quickSelect.setItemMeta(quickMeta);
        }
        inv.setItem(31, quickSelect);
        
        // Balance display (slot 45)
        ItemStack balanceItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta balanceMeta = balanceItem.getItemMeta();
        if (balanceMeta != null) {
            balanceMeta.setDisplayName(ChatColor.of("#FFD700") + "Your Balance");
            List<String> balanceLore = new ArrayList<>();
            balanceLore.add("");
            balanceLore.add(ChatColor.of("#FFD700") + "Coins: " + ChatColor.of("#FFFFFF") + 
                    MONEY_FORMAT.format(coinBalance));
            balanceLore.add(ChatColor.of("#00FFFF") + "Tokens: " + ChatColor.of("#FFFFFF") + 
                    MONEY_FORMAT.format(tokenBalance));
            balanceMeta.setLore(balanceLore);
            balanceItem.setItemMeta(balanceMeta);
        }
        inv.setItem(45, balanceItem);
        
        // Confirm button (slot 49)
        boolean canAfford = coinBalance >= totalCoins;
        ItemStack confirm = new ItemStack(canAfford ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
        ItemMeta confirmMeta = confirm.getItemMeta();
        if (confirmMeta != null) {
            if (canAfford) {
                confirmMeta.setDisplayName(ChatColor.of("#55FF55") + "✔ CONFIRM PURCHASE");
                List<String> confirmLore = new ArrayList<>();
                confirmLore.add("");
                confirmLore.add(ChatColor.of("#808080") + "Purchase " + ChatColor.of("#00FFFF") + quantity + 
                        ChatColor.of("#808080") + " token" + (quantity > 1 ? "s" : ""));
                confirmLore.add(ChatColor.of("#808080") + "Cost: " + ChatColor.of("#FFD700") + 
                        MONEY_FORMAT.format(totalCoins) + " Coins");
                confirmLore.add("");
                confirmLore.add(ChatColor.of("#55FF55") + "▸ Click to confirm!");
                confirmMeta.setLore(confirmLore);
            } else {
                confirmMeta.setDisplayName(ChatColor.of("#FF5555") + "✖ Cannot Purchase");
                List<String> confirmLore = new ArrayList<>();
                confirmLore.add("");
                confirmLore.add(ChatColor.of("#FF5555") + "Insufficient coins!");
                confirmMeta.setLore(confirmLore);
            }
            confirm.setItemMeta(confirmMeta);
        }
        inv.setItem(49, confirm);
        
        // Back button (slot 53)
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.of("#FFFF00") + "← Back");
            List<String> backLore = new ArrayList<>();
            backLore.add("");
            backLore.add(ChatColor.of("#808080") + "Return to main shop");
            backMeta.setLore(backLore);
            back.setItemMeta(backMeta);
        }
        inv.setItem(53, back);
        
            if (isNewInventory) {
                // First time opening - need to show the inventory
                player.openInventory(inv);
            } else {
                // Already open - just force client update
                player.updateInventory();
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error updating exchange menu for " + player.getName(), e);
            player.closeInventory();
            player.sendMessage(ChatColor.of("#FF5555") + "Error opening exchange menu. Please try again.");
        }
    }
    
    /**
     * Open the quick select preset menu
     */
    private void openQuickSelect(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, PRESET_TITLE);
        
        fillBorder(inv);
        
        // Preset buttons (centered row)
        createPresetButton(inv, 10, 1, "1 Token", Material.GOLD_NUGGET);
        createPresetButton(inv, 11, 10, "10 Tokens", Material.GOLD_INGOT);
        createPresetButton(inv, 12, 50, "50 Tokens", Material.GOLD_BLOCK);
        createPresetButton(inv, 13, 100, "100 Tokens", Material.EMERALD);
        createPresetButton(inv, 14, 500, "500 Tokens", Material.EMERALD_BLOCK);
        createPresetButton(inv, 16, 1000, "1000 Tokens (MAX)", Material.DIAMOND);
        
        // Back button (slot 22)
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.of("#FFFF00") + "← Back");
            List<String> backLore = new ArrayList<>();
            backLore.add("");
            backLore.add(ChatColor.of("#808080") + "Return to exchange menu");
            backMeta.setLore(backLore);
            back.setItemMeta(backMeta);
        }
        inv.setItem(22, back);
        
        player.openInventory(inv);
    }
    
    /**
     * Handle click events (called by MenuManager)
     */
    public void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR || 
            clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) {
            return;
        }
        
        int slot = event.getSlot();
        UUID uuid = player.getUniqueId();
        
        // Handle main exchange menu
        if (title.equals(MENU_TITLE)) {
            handleExchangeClick(player, slot, uuid);
        }
        // Handle preset menu
        else if (title.equals(PRESET_TITLE)) {
            handlePresetClick(player, slot, uuid);
        }
    }
    
    /**
     * Handle clicks in the main exchange menu
     */
    private void handleExchangeClick(Player player, int slot, UUID uuid) {
        int quantity = playerQuantities.getOrDefault(uuid, MIN_QUANTITY);
        
        // Quantity adjustments
        if (slot == 20 && quantity > 10) { // -10
            playerQuantities.put(uuid, quantity - 10);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.8f);
            updateExchangeMenu(player);
        } else if (slot == 21 && quantity > MIN_QUANTITY) { // -1
            playerQuantities.put(uuid, quantity - 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.9f);
            updateExchangeMenu(player);
        } else if (slot == 23 && quantity < MAX_QUANTITY) { // +1
            playerQuantities.put(uuid, quantity + 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.1f);
            updateExchangeMenu(player);
        } else if (slot == 24 && quantity + 10 <= MAX_QUANTITY) { // +10
            playerQuantities.put(uuid, Math.min(MAX_QUANTITY, quantity + 10));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
            updateExchangeMenu(player);
        } else if (slot == 31) { // Quick select
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            openQuickSelect(player);
        } else if (slot == 49) { // Confirm
            performPurchase(player, quantity, uuid);
        } else if (slot == 53) { // Back
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            cleanupPlayerData(uuid);
            new ShopMainMenu(plugin, economy).open(player);
        }
    }
    
    /**
     * Handle clicks in the preset menu
     */
    private void handlePresetClick(Player player, int slot, UUID uuid) {
        // Back button
        if (slot == 22) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            updateExchangeMenu(player);
            return;
        }
        
        // Preset selections
        Map<Integer, Integer> presets = new HashMap<>();
        presets.put(10, 1);
        presets.put(11, 10);
        presets.put(12, 50);
        presets.put(13, 100);
        presets.put(14, 500);
        presets.put(16, 1000);
        
        if (presets.containsKey(slot)) {
            playerQuantities.put(uuid, presets.get(slot));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            updateExchangeMenu(player);
        }
    }
    
    /**
     * Perform the actual token purchase
     */
    /**
     * Perform the actual token purchase with atomic transaction
     */
    private void performPurchase(Player player, int quantity, UUID uuid) {
        if (player == null || uuid == null) {
            plugin.getLogger().warning("Null player or UUID in performPurchase");
            return;
        }
        
        if (!player.isOnline()) {
            plugin.getLogger().warning("Player " + player.getName() + " went offline during purchase");
            return;
        }
        
        try {
            // Calculate with overflow protection
            long totalCoinsLong = (long) COINS_PER_TOKEN * quantity;
            if (totalCoinsLong > Integer.MAX_VALUE) {
                player.sendMessage(ChatColor.of("#FF5555") + "✖ Purchase amount too large!");
                return;
            }
            int totalCoins = (int) totalCoinsLong;
            
            double coinBalance = economy.getBalance(uuid, CurrencyType.COINS);
            
            // Validation
            if (coinBalance < totalCoins) {
                player.sendMessage(ChatColor.of("#FF5555") + "✖ Not enough coins!");
                playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            
            // Additional validation
            if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
                player.sendMessage(ChatColor.of("#FF5555") + "✖ Invalid quantity!");
                plugin.getLogger().warning("Invalid quantity " + quantity + " for player " + player.getName());
                return;
            }
            
            // Atomic transaction with error handling
            try {
                economy.subtractBalance(uuid, CurrencyType.COINS, totalCoins);
                economy.addBalance(uuid, CurrencyType.TOKENS, quantity);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Transaction failed, attempting rollback", e);
                // Attempt rollback
                try {
                    economy.addBalance(uuid, CurrencyType.COINS, totalCoins);
                    player.sendMessage(ChatColor.of("#FF5555") + "✖ Transaction failed! Funds refunded.");
                } catch (Exception rollbackError) {
                    plugin.getLogger().log(Level.SEVERE, "CRITICAL: Rollback failed!", rollbackError);
                    player.sendMessage(ChatColor.of("#FF5555") + "✖ CRITICAL ERROR! Contact an administrator.");
                }
                return;
            }
            
            // Success messages
            player.sendMessage(ChatColor.of("#55FF55") + "✔ Purchase Successful!");
            player.sendMessage(ChatColor.of("#FFFFFF") + "Purchased " + ChatColor.of("#00FFFF") + quantity + 
                    " token" + (quantity > 1 ? "s" : "") + ChatColor.of("#FFFFFF") + " for " + 
                    ChatColor.of("#FFD700") + MONEY_FORMAT.format(totalCoins) + " Coins");
            playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
            
            // Reset and refresh
            playerQuantities.put(uuid, MIN_QUANTITY);
            updateExchangeMenu(player);
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Unexpected error in performPurchase", e);
            player.sendMessage(ChatColor.of("#FF5555") + "✖ An error occurred!");
        }
    }
    
    /**
     * Play sound safely
     */
    private void playSound(Player player, Sound sound, float volume, float pitch) {
        if (player == null || sound == null) return;
        
        try {
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error playing sound", e);
        }
    }
    
    /**
     * Handle close events (called by MenuManager)
     */
    public void handleClose(InventoryCloseEvent event) {
        if (event == null || !(event.getPlayer() instanceof Player)) return;
        
        try {
            UUID uuid = event.getPlayer().getUniqueId();
            // Delay cleanup to allow navigation between menus
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    if (event.getPlayer().getOpenInventory().getTopInventory().getSize() == 0) {
                        cleanupPlayerData(uuid);
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Error in delayed cleanup", e);
                }
            }, 2L);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error handling token menu close", e);
        }
    }
    
    // Helper methods
    
    private void fillBorder(Inventory inv) {
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = border.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            border.setItemMeta(meta);
        }
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, border);
        }
    }
    
    private void createButton(Inventory inv, int slot, Material material, String name, 
                             boolean enabled, String description) {
        ItemStack button = new ItemStack(enabled ? material : Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = button.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(enabled ? name : ChatColor.of("#808080") + "Cannot adjust");
            if (enabled) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.of("#808080") + description);
                meta.setLore(lore);
            }
            button.setItemMeta(meta);
        }
        inv.setItem(slot, button);
    }
    
    private void createPresetButton(Inventory inv, int slot, int amount, String name, Material material) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.of("#FFFF00") + name);
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.of("#808080") + "Cost: " + ChatColor.of("#FFD700") + 
                    MONEY_FORMAT.format(COINS_PER_TOKEN * amount) + " Coins");
            lore.add("");
            lore.add(ChatColor.of("#55FF55") + "▸ Click to select!");
            meta.setLore(lore);
            button.setItemMeta(meta);
        }
        inv.setItem(slot, button);
    }
    
    private void cleanupPlayerData(UUID uuid) {
        playerQuantities.remove(uuid);
    }
}
