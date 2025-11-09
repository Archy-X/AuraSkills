package dev.aurelium.auraskills.bukkit.skillcoins.menu;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skillcoins.shop.ShopSection;
import dev.aurelium.auraskills.common.skillcoins.CurrencyType;
import dev.aurelium.auraskills.common.skillcoins.EconomyProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Main shop menu showing all sections - FULLY REWRITTEN with comprehensive error handling
 * 
 * Features:
 * - Bulletproof null checking at every step
 * - Graceful error recovery
 * - Validation of all user input
 * - Safe economy operations
 * - Defensive section loading
 */
public class ShopMainMenu {
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    private static final String MENU_TITLE = ChatColor.of("#00FFFF") + "❖ " + ChatColor.of("#FFFFFF") + "SkillCoins Shop";
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    
    public ShopMainMenu(AuraSkills plugin, EconomyProvider economy) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        if (economy == null) {
            throw new IllegalArgumentException("Economy provider cannot be null");
        }
        this.plugin = plugin;
        this.economy = economy;
    }
    
    /**
     * Open the main shop menu with comprehensive error handling
     */
    public void open(Player player) {
        if (player == null) {
            plugin.getLogger().warning("Attempted to open main shop menu for null player");
            return;
        }
        
        if (!player.isOnline()) {
            plugin.getLogger().warning("Attempted to open main shop menu for offline player: " + player.getName());
            return;
        }
        
        try {
            // Register this menu instance with MenuManager
            MenuManager manager = MenuManager.getInstance(plugin);
            if (manager != null) {
                manager.registerMainMenu(player, this);
            }
            
            // Create inventory
            Inventory inv = createInventory(player);
            if (inv == null) {
                player.sendMessage(ChatColor.of("#FF5555") + "✖ Error creating shop menu!");
                plugin.getLogger().severe("Failed to create inventory for player: " + player.getName());
                return;
            }
            
            // Open safely
            player.openInventory(inv);
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error opening main shop menu for " + player.getName(), e);
            player.sendMessage(ChatColor.of("#FF5555") + "✖ An error occurred opening the shop!");
        }
    }
    
    /**
     * Create the inventory with full validation
     */
    private Inventory createInventory(Player player) {
        try {
            // Create 54-slot inventory (6 rows)
            Inventory inv = Bukkit.createInventory(null, 54, MENU_TITLE);
            if (inv == null) {
                plugin.getLogger().severe("Bukkit.createInventory returned null!");
                return null;
            }
            
            UUID uuid = player.getUniqueId();
            
            // Get balances with error handling
            double coins = 0.0;
            double tokens = 0.0;
            try {
                coins = economy.getBalance(uuid, CurrencyType.COINS);
                tokens = economy.getBalance(uuid, CurrencyType.TOKENS);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error getting balance for " + player.getName(), e);
                // Continue with zero balances rather than failing completely
            }
            
            // Fill with black glass pane border
            fillBorder(inv);
            
            // Add player head (slot 0)
            addPlayerHead(inv, player);
            
            // Add balance display (slot 8)
            addBalanceDisplay(inv, coins, tokens);
            
            // Add shop sections with validation
            addShopSections(inv);
            
            // Add close button (slot 53)
            addCloseButton(inv);
            
            return inv;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error creating main shop inventory", e);
            return null;
        }
    }
    
    /**
     * Fill inventory with border safely
     */
    private void fillBorder(Inventory inv) {
        if (inv == null) return;
        
        try {
            ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta borderMeta = border.getItemMeta();
            if (borderMeta != null) {
                borderMeta.setDisplayName(" ");
                border.setItemMeta(borderMeta);
            }
            
            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i, border);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error filling border", e);
        }
    }
    
    /**
     * Add player head with error handling
     */
    private void addPlayerHead(Inventory inv, Player player) {
        if (inv == null || player == null) return;
        
        try {
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = playerHead.getItemMeta();
            
            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                skullMeta.setOwningPlayer(player);
                skullMeta.setDisplayName(ChatColor.of("#00FFFF") + "SkillCoins Shop " + 
                        ChatColor.of("#FFD700") + "♦ " + player.getName());
                
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.of("#808080") + "Welcome to the SkillCoins shop!");
                lore.add(ChatColor.of("#808080") + "Browse categories below to buy");
                lore.add(ChatColor.of("#808080") + "and sell items using your earnings.");
                lore.add("");
                lore.add(ChatColor.of("#00FFFF") + "▸ Your balance is on the right →");
                
                skullMeta.setLore(lore);
                playerHead.setItemMeta(skullMeta);
            }
            
            inv.setItem(0, playerHead);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding player head", e);
        }
    }
    
    /**
     * Add balance display with validation
     */
    private void addBalanceDisplay(Inventory inv, double coins, double tokens) {
        if (inv == null) return;
        
        try {
            ItemStack balanceItem = new ItemStack(Material.GOLD_INGOT);
            ItemMeta balanceMeta = balanceItem.getItemMeta();
            if (balanceMeta != null) {
                balanceMeta.setDisplayName(ChatColor.of("#FFD700") + "⬥ Your Balance");
                
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.of("#FFFF00") + "SkillCoins: " + 
                        ChatColor.of("#FFFFFF") + MONEY_FORMAT.format(coins));
                lore.add(ChatColor.of("#00FFFF") + "SkillTokens: " + 
                        ChatColor.of("#FFFFFF") + MONEY_FORMAT.format(tokens));
                lore.add("");
                lore.add(ChatColor.of("#808080") + "Earn more by leveling skills");
                lore.add(ChatColor.of("#808080") + "and completing objectives!");
                
                balanceMeta.setLore(lore);
                balanceItem.setItemMeta(balanceMeta);
            }
            
            inv.setItem(8, balanceItem);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding balance display", e);
        }
    }
    
    /**
     * Add shop sections with full validation
     */
    private void addShopSections(Inventory inv) {
        if (inv == null) return;
        
        try {
            List<ShopSection> sections = null;
            
            try {
                if (plugin.getShopLoader() != null) {
                    sections = plugin.getShopLoader().getSections();
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error loading shop sections", e);
            }
            
            if (sections == null || sections.isEmpty()) {
                plugin.getLogger().warning("No shop sections loaded!");
                return;
            }
            
            for (ShopSection section : sections) {
                if (section == null) continue;
                
                try {
                    int slot = section.getSlot();
                    
                    // Validate slot is within inventory bounds
                    if (slot < 0 || slot >= inv.getSize()) {
                        plugin.getLogger().warning("Invalid slot " + slot + " for section " + section.getId());
                        continue;
                    }
                    
                    Material icon = section.getIcon();
                    if (icon == null || icon == Material.AIR) {
                        plugin.getLogger().warning("Invalid icon for section " + section.getId());
                        icon = Material.CHEST; // Fallback
                    }
                    
                    ItemStack item = new ItemStack(icon);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        String displayName = section.getDisplayName();
                        if (displayName != null && !displayName.isEmpty()) {
                            displayName = ChatColor.translateAlternateColorCodes('&', displayName);
                            meta.setDisplayName(displayName);
                        } else {
                            meta.setDisplayName(ChatColor.of("#00FFFF") + section.getId());
                        }
                        
                        List<String> lore = new ArrayList<>();
                        lore.add("");
                        lore.add(ChatColor.of("#808080") + "Browse and purchase items");
                        lore.add(ChatColor.of("#808080") + "in this category");
                        lore.add("");
                        lore.add(ChatColor.of("#808080") + "Items: " + 
                                ChatColor.of("#FFFFFF") + section.getItemCount());
                        lore.add("");
                        lore.add(ChatColor.of("#FFFF00") + "▸ Click to open!");
                        
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }
                    
                    inv.setItem(slot, item);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Error adding section " + section.getId(), e);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error adding shop sections", e);
        }
    }
    
    /**
     * Add close button
     */
    private void addCloseButton(Inventory inv) {
        if (inv == null) return;
        
        try {
            ItemStack close = new ItemStack(Material.BARRIER);
            ItemMeta closeMeta = close.getItemMeta();
            if (closeMeta != null) {
                closeMeta.setDisplayName(ChatColor.of("#FF5555") + "✖ Close");
                List<String> closeLore = new ArrayList<>();
                closeLore.add("");
                closeLore.add(ChatColor.of("#808080") + "Close this menu");
                closeMeta.setLore(closeLore);
                close.setItemMeta(closeMeta);
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
        return title.equals(MENU_TITLE);
    }
    
    /**
     * Handle click events with comprehensive validation
     */
    public void handleClick(InventoryClickEvent event) {
        if (event == null) return;
        
        try {
            // Cancel event to prevent item pickup
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
            
            // Handle close button
            if (slot == 53 && clicked.getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }
            
            // Handle section clicks
            handleSectionClick(player, slot);
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error handling main menu click", e);
        }
    }
    
    /**
     * Handle section click with validation
     */
    private void handleSectionClick(Player player, int slot) {
        if (player == null) return;
        
        try {
            List<ShopSection> sections = null;
            
            try {
                if (plugin.getShopLoader() != null) {
                    sections = plugin.getShopLoader().getSections();
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error loading sections for click", e);
            }
            
            if (sections == null || sections.isEmpty()) {
                player.sendMessage(ChatColor.of("#FF5555") + "✖ No shop sections available!");
                return;
            }
            
            for (ShopSection section : sections) {
                if (section == null) continue;
                
                if (slot == section.getSlot()) {
                    String sectionId = section.getId();
                    if (sectionId == null || sectionId.isEmpty()) {
                        plugin.getLogger().warning("Section has null or empty ID at slot " + slot);
                        continue;
                    }
                    
                    // Play sound
                    try {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    } catch (Exception e) {
                        plugin.getLogger().log(Level.WARNING, "Error playing sound", e);
                    }
                    
                    // Special handling for token services
                    if (sectionId.equalsIgnoreCase("TokenExchange")) {
                        try {
                            new TokenExchangeMenu(plugin, economy).open(player);
                        } catch (Exception e) {
                            plugin.getLogger().log(Level.SEVERE, "Error opening token exchange", e);
                            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error opening token exchange!");
                        }
                    } else if (sectionId.equalsIgnoreCase("SkillLevels")) {
                        try {
                            new SkillLevelPurchaseMenu(plugin, economy).open(player);
                        } catch (Exception e) {
                            plugin.getLogger().log(Level.SEVERE, "Error opening skill purchase", e);
                            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error opening skill purchase!");
                        }
                    } else {
                        // Regular shop section
                        try {
                            new ShopSectionMenu(plugin, economy, section).open(player, 0);
                        } catch (Exception e) {
                            plugin.getLogger().log(Level.SEVERE, "Error opening section " + sectionId, e);
                            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error opening shop section!");
                        }
                    }
                    return;
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error in handleSectionClick", e);
        }
    }
    
    /**
     * Handle close events
     */
    public void handleClose(InventoryCloseEvent event) {
        // No cleanup needed for main menu - just log for debugging
        if (event != null && event.getPlayer() instanceof Player) {
            plugin.getLogger().fine("Main menu closed for " + event.getPlayer().getName());
        }
    }
}
