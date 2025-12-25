package dev.aurelium.auraskills.bukkit.skillcoins.menu;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skillcoins.shop.ShopItem;
import dev.aurelium.auraskills.bukkit.skillcoins.shop.ShopSection;
import dev.aurelium.auraskills.common.skillcoins.CurrencyType;
import dev.aurelium.auraskills.common.skillcoins.EconomyProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Section menu showing all items in a category with pagination - FULLY REWRITTEN
 * 
 * Features:
 * - Bulletproof pagination with bounds checking
 * - Thread-safe page tracking
 * - Comprehensive null checking
 * - Graceful handling of empty sections
 * - Safe item loading and display
 */
public class ShopSectionMenu {
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    private final ShopSection section;
    private final SharedNavbarManager navbarManager;
    private String menuTitle;
    private static final int ITEMS_PER_PAGE = 45; // 5 rows of 9
    private static final ConcurrentHashMap<UUID, Integer> playerPages = new ConcurrentHashMap<>();
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    
    public ShopSectionMenu(AuraSkills plugin, EconomyProvider economy, ShopSection section) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        if (economy == null) {
            throw new IllegalArgumentException("Economy provider cannot be null");
        }
        if (section == null) {
            throw new IllegalArgumentException("Section cannot be null");
        }
        
        this.plugin = plugin;
        this.economy = economy;
        this.section = section;
        this.navbarManager = new SharedNavbarManager(plugin, economy);
        this.menuTitle = generateMenuTitle(section);
    }
    
    /**
     * Generate menu title with validation
     */
    private String generateMenuTitle(ShopSection section) {
        try {
            String sectionName = section.getDisplayName();
            if (sectionName == null || sectionName.isEmpty()) {
                sectionName = section.getId();
            }
            
            if (sectionName == null) {
                sectionName = "Shop";
            }
            
            // Color-coded titles based on section
            if (sectionName.contains("Combat")) {
                return ChatColor.of("#FF5555") + "⚔ Combat Shop";
            } else if (sectionName.contains("Enchantments")) {
                return ChatColor.of("#FF55FF") + "✦ Enchantments Shop";
            } else if (sectionName.contains("Resources")) {
                return ChatColor.of("#55FF55") + "❖ Resources Shop";
            } else if (sectionName.contains("Tools")) {
                return ChatColor.of("#5555FF") + "⚒ Tools Shop";
            } else {
                // Ensure section menus include the word 'Shop'
                if (sectionName.toLowerCase().contains("shop")) {
                    return ChatColor.of("#00FFFF") + sectionName;
                } else {
                    return ChatColor.of("#00FFFF") + sectionName + ChatColor.of("#FFFFFF") + " Shop";
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error generating menu title", e);
            return ChatColor.of("#00FFFF") + "Shop";
        }
    }
    
    /**
     * Open menu with comprehensive validation
     */
    public void open(Player player, int page) {
        open(player, page, false);
    }
    
    /**
     * Open menu with comprehensive validation
     * @param updateOnly If true, updates existing inventory instead of creating new one
     */
    private void open(Player player, int page, boolean updateOnly) {
        if (player == null) {
            plugin.getLogger().warning("Attempted to open section menu for null player");
            return;
        }
        
        if (!player.isOnline()) {
            plugin.getLogger().warning("Attempted to open section menu for offline player: " + player.getName());
            return;
        }
        
        // Validate page number
        List<ShopItem> allItems = section.getItems();
        if (allItems == null) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error loading shop items!");
            plugin.getLogger().severe("Section " + section.getId() + " has null items list");
            return;
        }
        
        if (allItems.isEmpty()) {
            player.sendMessage(ChatColor.of("#FFFF00") + "⚠ This section has no items!");
            plugin.getLogger().warning("Section " + section.getId() + " has no items");
            new ShopMainMenu(plugin, economy).open(player);
            return;
        }
        
        int maxPage = Math.max(0, (allItems.size() - 1) / ITEMS_PER_PAGE);
        int safePage = Math.max(0, Math.min(page, maxPage)); // Clamp page to valid range
        
        playerPages.put(player.getUniqueId(), safePage);
        
        try {
            if (updateOnly) {
                // Update existing inventory instead of creating new one
                Inventory inv = player.getOpenInventory().getTopInventory();
                if (inv != null && inv.getSize() == 54) {
                    updateInventoryContents(player, inv, safePage, allItems);
                    player.updateInventory();
                    return;
                }
            }
            
            // Register with MenuManager
            MenuManager manager = MenuManager.getInstance(plugin);
            if (manager != null) {
                manager.registerSectionMenu(player, this);
            }
            
            // Create and open inventory
            Inventory inv = createInventory(player, safePage, allItems);
            if (inv == null) {
                player.sendMessage(ChatColor.of("#FF5555") + "✖ Error creating shop menu!");
                return;
            }
            
            player.openInventory(inv);
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error opening section menu for " + player.getName(), e);
            player.sendMessage(ChatColor.of("#FF5555") + "✖ An error occurred opening the shop!");
        }
    }
    
    /**
     * Update existing inventory contents (for pagination)
     */
    private void updateInventoryContents(Player player, Inventory inv, int page, List<ShopItem> allItems) {
        try {
            inv.clear();
            
            int maxPage = (allItems.size() - 1) / ITEMS_PER_PAGE;
            int startIndex = page * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allItems.size());
            
            // Get player balance safely
            double coins = 0.0;
            try {
                coins = economy.getBalance(player.getUniqueId(), CurrencyType.COINS);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error getting balance for " + player.getName(), e);
            }
            
            // Fill border first
            fillBorder(inv);
            
            // Add items for this page (slots 0-44)
            addPageItems(inv, allItems, startIndex, endIndex);
            
            // Add navigation and info bar (slots 45-53)
            navbarManager.addNavbar(inv, "shop_section", page, maxPage, player);
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error updating section inventory", e);
        }
    }
    
    /**
     * Create inventory with full validation
     */
    private Inventory createInventory(Player player, int page, List<ShopItem> allItems) {
        try {
            Inventory inv = Bukkit.createInventory(null, 54, menuTitle);
            if (inv == null) {
                plugin.getLogger().severe("Bukkit.createInventory returned null!");
                return null;
            }
            
            int maxPage = (allItems.size() - 1) / ITEMS_PER_PAGE;
            int startIndex = page * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allItems.size());
            
            // Get player balance safely
            double coins = 0.0;
            try {
                coins = economy.getBalance(player.getUniqueId(), CurrencyType.COINS);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error getting balance for " + player.getName(), e);
            }
            
            // Fill border first
            fillBorder(inv);
            
            // Add items for this page (slots 0-44)
            addPageItems(inv, allItems, startIndex, endIndex);
            
            // Add navigation and info bar (slots 45-53)
            navbarManager.addNavbar(inv, "shop_section", page, maxPage, player);
            
            return inv;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error creating section inventory", e);
            return null;
        }
    }
    
    /**
     * Fill border with validation - updated layout
     */
    private void fillBorder(Inventory inv) {
        if (inv == null) return;
        
        try {
            ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = border.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(" ");
                border.setItemMeta(meta);
            }
            
            // Fill bottom row with black glass (slots 45-53)
            for (int i = 45; i < 54; i++) {
                inv.setItem(i, border);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error filling border", e);
        }
    }
    
    /**
     * Add page items with validation
     */
    private void addPageItems(Inventory inv, List<ShopItem> allItems, int startIndex, int endIndex) {
        if (inv == null || allItems == null) return;
        
        try {
            int slot = 0;
            for (int i = startIndex; i < endIndex && i < allItems.size(); i++) {
                if (slot >= ITEMS_PER_PAGE) break;
                
                ShopItem shopItem = allItems.get(i);
                if (shopItem == null) {
                    plugin.getLogger().warning("Null shop item at index " + i);
                    continue;
                }
                
                try {
                    ItemStack display = createItemDisplay(shopItem);
                    if (display != null) {
                        inv.setItem(slot, display);
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Error creating display for item at index " + i, e);
                }
                
                slot++;
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error adding page items", e);
        }
    }
    
    /**
     * Create item display with lore
     */
    private ItemStack createItemDisplay(ShopItem shopItem) {
        if (shopItem == null) return null;
        
        try {
            ItemStack display = shopItem.createItemStack(1);
            if (display == null) {
                plugin.getLogger().warning("ShopItem.createItemStack returned null");
                return null;
            }
            
            ItemMeta meta = display.getItemMeta();
            if (meta != null) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                
                // Buy info
                if (shopItem.canBuy()) {
                    double buyPrice = shopItem.getBuyPrice();
                    lore.add(ChatColor.of("#55FF55") + "● Buy: " + ChatColor.of("#FFFFFF") + 
                            MONEY_FORMAT.format(buyPrice) + ChatColor.of("#FFFF00") + " Coins");
                    lore.add(ChatColor.of("#808080") + "  └ " + ChatColor.of("#FFFF00") + "Left Click" + 
                            ChatColor.of("#808080") + " to purchase");
                } else {
                    lore.add(ChatColor.of("#FF5555") + "✖ Cannot buy");
                }
                
                lore.add("");
                
                // Sell info
                if (shopItem.canSell()) {
                    double sellPrice = shopItem.getSellPrice();
                    lore.add(ChatColor.of("#FFD700") + "● Sell: " + ChatColor.of("#FFFFFF") + 
                            MONEY_FORMAT.format(sellPrice) + ChatColor.of("#FFFF00") + " Coins");
                    lore.add(ChatColor.of("#808080") + "  └ " + ChatColor.of("#FFFF00") + "Right Click" + 
                            ChatColor.of("#808080") + " to sell");
                } else {
                    lore.add(ChatColor.of("#FF5555") + "✖ Cannot sell");
                }
                
                meta.setLore(lore);
                display.setItemMeta(meta);
            }
            
            return display;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error creating item display", e);
            return null;
        }
    }
    
    /**
     * Add page info display - slot 45
     */
    private void addPageInfo(Inventory inv, int page, int maxPage, int startIndex, int endIndex, int totalItems) {
        if (inv == null) return;
        
        try {
            ItemStack pageInfo = new ItemStack(Material.BOOK);
            ItemMeta meta = pageInfo.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.of("#00FFFF") + "Page " + (page + 1) + " of " + (maxPage + 1));
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.of("#808080") + "Showing items " + 
                        ChatColor.of("#FFFFFF") + (startIndex + 1) + "-" + endIndex + 
                        ChatColor.of("#808080") + " of " + ChatColor.of("#FFFFFF") + totalItems);
                meta.setLore(lore);
                pageInfo.setItemMeta(meta);
            }
            inv.setItem(45, pageInfo);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding page info", e);
        }
    }
    
    /**
     * Add balance display - slot 46
     */
    private void addBalanceDisplay(Inventory inv, double coins) {
        if (inv == null) return;
        
        try {
            ItemStack balanceItem = new ItemStack(Material.GOLD_NUGGET);
            ItemMeta meta = balanceItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.of("#FFFF00") + "Your Coins: " + 
                        ChatColor.of("#FFFFFF") + MONEY_FORMAT.format(coins));
                balanceItem.setItemMeta(meta);
            }
            inv.setItem(46, balanceItem);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding balance", e);
        }
    }
    
    /**
     * Add navigation buttons - updated layout
     */
    private void addNavigationButtons(Inventory inv, int page, int maxPage) {
        if (inv == null) return;
        
        try {
            // Previous page button (slot 48) - only when you can go back
            if (page > 0) {
                ItemStack prevPage = new ItemStack(Material.ARROW);
                ItemMeta meta = prevPage.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ChatColor.of("#FFFF00") + "← Previous Page");
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add(ChatColor.of("#808080") + "Go to page " + ChatColor.of("#FFFFFF") + page);
                    meta.setLore(lore);
                    prevPage.setItemMeta(meta);
                }
                inv.setItem(48, prevPage);
            }
            // If no previous page, slot 48 remains black glass
            
            // Next page button (slot 50) - yellow text only
            if (page < maxPage) {
                ItemStack nextPage = new ItemStack(Material.ARROW);
                ItemMeta meta = nextPage.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ChatColor.of("#FFFF00") + "Next Page →");
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add(ChatColor.of("#808080") + "Go to page " + ChatColor.of("#FFFFFF") + (page + 2));
                    meta.setLore(lore);
                    nextPage.setItemMeta(meta);
                }
                inv.setItem(50, nextPage);
            }
            // If no next page, slot 50 remains black glass
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding navigation buttons", e);
        }
    }
    
    /**
     * Add back button - slot 53
     */
    private void addBackButton(Inventory inv) {
        if (inv == null) return;
        
        try {
            ItemStack back = new ItemStack(Material.ARROW);
            ItemMeta meta = back.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.of("#FF5555") + "← Back");
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.of("#808080") + "Return to main shop menu");
                meta.setLore(lore);
                back.setItemMeta(meta);
            }
            inv.setItem(53, back);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding back button", e);
        }
    }
    
    /**
     * Add close button - slot 53 (redundant with back button, keeping for compatibility)
     */
    private void addCloseButton(Inventory inv) {
        if (inv == null) return;
        
        try {
            ItemStack close = new ItemStack(Material.ARROW);
            ItemMeta meta = close.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.of("#55FF55") + "← Back");
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.of("#808080") + "Return to main shop menu");
                meta.setLore(lore);
                close.setItemMeta(meta);
            }
            inv.setItem(53, close);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding close button", e);
        }
    }
    
    /**
     * Check if a title matches this menu
     */
    public boolean isMenuTitle(String title) {
        if (title == null) return false;
        // Exact match for this specific section menu
        return title.equals(menuTitle);
    }
    
    /**
     * Handle click events with comprehensive validation
     */
    public void handleClick(InventoryClickEvent event) {
        if (event == null) return;
        
        try {
            event.setCancelled(true);
            
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }
            
            Player player = (Player) event.getWhoClicked();
            if (player == null || !player.isOnline()) {
                return;
            }
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }
            
            // Ignore border clicks
            if (clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) {
                return;
            }
            
            int slot = event.getSlot();
            UUID playerId = player.getUniqueId();
            Integer currentPage = playerPages.getOrDefault(playerId, 0);
            
            // Navigation buttons
            if (slot == 48 && currentPage > 0) {
                playSound(player, Sound.UI_BUTTON_CLICK, 0.5f, 0.9f);
                open(player, currentPage - 1, true); // Update existing inventory
                return;
            }
            
            List<ShopItem> allItems = section.getItems();
            if (allItems == null || allItems.isEmpty()) {
                player.sendMessage(ChatColor.of("#FF5555") + "✖ No items available!");
                return;
            }
            
            int maxPage = (allItems.size() - 1) / ITEMS_PER_PAGE;
            
            if (slot == 50 && currentPage < maxPage) {
                playSound(player, Sound.UI_BUTTON_CLICK, 0.5f, 1.1f);
                open(player, currentPage + 1, true); // Update existing inventory
                return;
            }
            
            // Back button (slot 53 now uses ARROW)
            if (slot == 53 && clicked.getType() == Material.ARROW) {
                playSound(player, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                new ShopMainMenu(plugin, economy).open(player);
                return;
            }
            
            // Item click (slots 0-44)
            handleItemClick(player, slot, currentPage, allItems, event.getClick());
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error handling section menu click", e);
        }
    }
    
    /**
     * Handle item click with validation
     */
    private void handleItemClick(Player player, int slot, int currentPage, List<ShopItem> allItems, ClickType clickType) {
        if (player == null || allItems == null || clickType == null) return;
        
        try {
            if (slot < 0 || slot >= ITEMS_PER_PAGE) return;
            
            int itemIndex = currentPage * ITEMS_PER_PAGE + slot;
            if (itemIndex < 0 || itemIndex >= allItems.size()) {
                // Valid slot but no item there (end of list)
                return;
            }
            
            ShopItem shopItem = allItems.get(itemIndex);
            if (shopItem == null) {
                player.sendMessage(ChatColor.of("#FF5555") + "✖ Invalid item!");
                plugin.getLogger().warning("Null shop item at index " + itemIndex);
                return;
            }
            
            boolean isBuying = clickType.isLeftClick();
            boolean isSelling = clickType.isRightClick();
            
            if (isBuying && shopItem.canBuy()) {
                playSound(player, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                try {
                    new TransactionMenu(plugin, economy, shopItem, true, section).open(player);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Error opening buy transaction", e);
                    player.sendMessage(ChatColor.of("#FF5555") + "✖ Error opening transaction menu!");
                }
            } else if (isSelling && shopItem.canSell()) {
                playSound(player, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                try {
                    new TransactionMenu(plugin, economy, shopItem, false, section).open(player);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Error opening sell transaction", e);
                    player.sendMessage(ChatColor.of("#FF5555") + "✖ Error opening transaction menu!");
                }
            } else if (isBuying && !shopItem.canBuy()) {
                player.sendMessage(ChatColor.of("#FF5555") + "✖ This item cannot be purchased!");
            } else if (isSelling && !shopItem.canSell()) {
                player.sendMessage(ChatColor.of("#FF5555") + "✖ This item cannot be sold!");
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error handling item click", e);
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
     * Handle close events
     */
    public void handleClose(InventoryCloseEvent event) {
        if (event == null || !(event.getPlayer() instanceof Player)) return;
        
        try {
            Player player = (Player) event.getPlayer();
            playerPages.remove(player.getUniqueId());
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error handling section menu close", e);
        }
    }
}
