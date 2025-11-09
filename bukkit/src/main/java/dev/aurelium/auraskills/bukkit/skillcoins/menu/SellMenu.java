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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Sell Menu - Double chest GUI where players can place items to sell
 * Items are automatically sold when menu is closed or sell button is clicked
 */
public class SellMenu {
    
    private static final String MENU_TITLE = "§e⚖ §fSell Items";
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    private static final int INVENTORY_SIZE = 54; // Double chest
    private static final int SELL_BUTTON_SLOT = 49; // Center bottom
    private static final int INFO_SLOT = 4; // Top center
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    private final Set<UUID> openMenus = new HashSet<>();
    
    public SellMenu(AuraSkills plugin, EconomyProvider economy) {
        this.plugin = plugin;
        this.economy = economy;
    }
    
    /**
     * Open the sell menu for a player
     */
    public void open(Player player) {
        // Register this menu instance with MenuManager
        MenuManager.getInstance(plugin).registerSellMenu(player, this);
        
        Inventory inv = Bukkit.createInventory(null, INVENTORY_SIZE, MENU_TITLE);
        
        // Create info item (top center)
        ItemStack info = new ItemStack(Material.GOLD_INGOT);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.of("#FFD700") + "⚖ Quick Sell");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.of("#808080") + "Place items you want to sell");
            lore.add(ChatColor.of("#808080") + "in this inventory.");
            lore.add("");
            lore.add(ChatColor.of("#55FF55") + "✔ Items will be sold automatically");
            lore.add(ChatColor.of("#808080") + "  when you close the menu or");
            lore.add(ChatColor.of("#808080") + "  click the Sell All button.");
            lore.add("");
            lore.add(ChatColor.of("#FFFF00") + "⚠ Only sellable items count!");
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inv.setItem(INFO_SLOT, info);
        
        // Create sell button (center bottom)
        ItemStack sellButton = new ItemStack(Material.EMERALD);
        ItemMeta sellMeta = sellButton.getItemMeta();
        if (sellMeta != null) {
            sellMeta.setDisplayName(ChatColor.of("#55FF55") + "✔ SELL ALL ITEMS");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.of("#808080") + "Click to sell all items");
            lore.add(ChatColor.of("#808080") + "in this inventory now");
            lore.add("");
            lore.add(ChatColor.of("#FFD700") + "▸ Click to sell!");
            sellMeta.setLore(lore);
            sellButton.setItemMeta(sellMeta);
        }
        inv.setItem(SELL_BUTTON_SLOT, sellButton);
        
        openMenus.add(player.getUniqueId());
        player.openInventory(inv);
    }
    
    /**
     * Check if a title matches this menu
     */
    public boolean isMenuTitle(String title) {
        return title.equals(MENU_TITLE);
    }
    
    /**
     * Handle click events (called by MenuManager)
     */
    public void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        
        // Prevent clicking on info and sell button
        if (slot == INFO_SLOT || slot == SELL_BUTTON_SLOT) {
            event.setCancelled(true);
            
            // Handle sell button click
            if (slot == SELL_BUTTON_SLOT) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                sellAllItems(player, event.getInventory());
            }
        }
        // Allow placing/removing items in other slots
    }
    
    /**
     * Handle close events (called by MenuManager)
     */
    public void handleClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        if (openMenus.contains(uuid)) {
            openMenus.remove(uuid);
            
            // Sell all items on close
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                sellAllItems(player, event.getInventory());
            }, 1L);
        }
    }
    
    /**
     * Sell all items in the inventory
     */
    private void sellAllItems(Player player, Inventory inventory) {
        List<SellResult> results = new ArrayList<>();
        double totalEarned = 0.0;
        int totalItemsSold = 0;
        
        // Get all shop sections to find sellable items
        List<ShopSection> sections = plugin.getShopLoader().getSections();
        Map<Material, ShopItem> sellableItems = new HashMap<>();
        
        // Build map of sellable items
        for (ShopSection section : sections) {
            for (ShopItem shopItem : section.getItems()) {
                if (shopItem.canSell()) {
                    sellableItems.put(shopItem.getMaterial(), shopItem);
                }
            }
        }
        
        // Process each slot
        for (int i = 0; i < inventory.getSize(); i++) {
            // Skip info and sell button slots
            if (i == INFO_SLOT || i == SELL_BUTTON_SLOT) continue;
            
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            
            // Find matching shop item
            ShopItem shopItem = findMatchingShopItem(item, sellableItems);
            if (shopItem == null || !shopItem.canSell()) {
                // Return unsellable items to player
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
                if (!leftover.isEmpty()) {
                    // Drop items if inventory full
                    for (ItemStack drop : leftover.values()) {
                        player.getWorld().dropItem(player.getLocation(), drop);
                    }
                }
                inventory.setItem(i, null);
                continue;
            }
            
            // Calculate earnings
            int amount = item.getAmount();
            double earnings = shopItem.getSellPrice() * amount;
            totalEarned += earnings;
            totalItemsSold += amount;
            
            // Track result
            results.add(new SellResult(shopItem.getMaterial(), amount, earnings));
            
            // Remove item from sell inventory
            inventory.setItem(i, null);
        }
        
        // Give coins to player
        if (totalEarned > 0) {
            economy.addBalance(player.getUniqueId(), CurrencyType.COINS, totalEarned);
            
            // Success message
            player.sendMessage(ChatColor.of("#55FF55") + "✔ Sale Completed!");
            player.sendMessage(ChatColor.of("#FFFFFF") + "Sold " + ChatColor.of("#FFFF00") + 
                    totalItemsSold + " item" + (totalItemsSold > 1 ? "s" : "") + 
                    ChatColor.of("#FFFFFF") + " for " + ChatColor.of("#FFD700") + 
                    MONEY_FORMAT.format(totalEarned) + " Coins");
            
            // Show detailed breakdown if 5 or fewer different items
            if (results.size() <= 5) {
                player.sendMessage("");
                player.sendMessage(ChatColor.of("#808080") + "Breakdown:");
                for (SellResult result : results) {
                    String itemName = result.material.name().replace("_", " ").toLowerCase();
                    player.sendMessage(ChatColor.of("#808080") + "  • " + ChatColor.of("#FFFFFF") + 
                            result.amount + "x " + itemName + ChatColor.of("#808080") + " = " + 
                            ChatColor.of("#FFD700") + MONEY_FORMAT.format(result.earnings) + " Coins");
                }
            }
            
            double newBalance = economy.getBalance(player.getUniqueId(), CurrencyType.COINS);
            player.sendMessage("");
            player.sendMessage(ChatColor.of("#808080") + "New balance: " + ChatColor.of("#FFFFFF") + 
                    MONEY_FORMAT.format(newBalance) + " Coins");
            
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        } else {
            player.sendMessage(ChatColor.of("#FFFF00") + "⚠ No sellable items found!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }
    
    /**
     * Find a matching shop item for the given ItemStack
     */
    private ShopItem findMatchingShopItem(ItemStack item, Map<Material, ShopItem> sellableItems) {
        if (item == null) return null;
        
        // Try direct material match first
        ShopItem shopItem = sellableItems.get(item.getType());
        if (shopItem != null && shopItem.matches(item)) {
            return shopItem;
        }
        
        // Try matching with enchantments
        for (ShopItem candidate : sellableItems.values()) {
            if (candidate.matches(item)) {
                return candidate;
            }
        }
        
        return null;
    }
    
    /**
     * Helper class to track sell results
     */
    private static class SellResult {
        final Material material;
        final int amount;
        final double earnings;
        
        SellResult(Material material, int amount, double earnings) {
            this.material = material;
            this.amount = amount;
            this.earnings = earnings;
        }
    }
}
