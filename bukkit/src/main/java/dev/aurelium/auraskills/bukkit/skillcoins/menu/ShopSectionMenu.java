package dev.aurelium.auraskills.bukkit.skillcoins.menu;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skillcoins.shop.ShopItem;
import dev.aurelium.auraskills.bukkit.skillcoins.shop.ShopSection;
import dev.aurelium.auraskills.common.skillcoins.CurrencyType;
import dev.aurelium.auraskills.common.skillcoins.EconomyProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Section menu showing all items in a category with pagination
 */
public class ShopSectionMenu implements Listener {
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    private final ShopSection section;
    private static final int ITEMS_PER_PAGE = 45; // 5 rows of 9
    private static final Map<UUID, Integer> playerPages = new HashMap<>();
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    
    public ShopSectionMenu(AuraSkills plugin, EconomyProvider economy, ShopSection section) {
        this.plugin = plugin;
        this.economy = economy;
        this.section = section;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void open(Player player, int page) {
        playerPages.put(player.getUniqueId(), page);
        
        // Get color-coded title based on section
        String sectionName = section.getDisplayName();
        String coloredTitle;
        if (sectionName.contains("Combat")) {
            coloredTitle = ChatColor.of("#FF5555") + "⚔ Combat Shop";
        } else if (sectionName.contains("Enchantments")) {
            coloredTitle = ChatColor.of("#FF55FF") + "✦ Enchantments Shop";
        } else if (sectionName.contains("Resources")) {
            coloredTitle = ChatColor.of("#55FF55") + "❖ Resources Shop";
        } else if (sectionName.contains("Tools")) {
            coloredTitle = ChatColor.of("#5555FF") + "⚒ Tools Shop";
        } else {
            // Ensure section menus include the word 'Shop' so click handlers detect them
            if (sectionName.toLowerCase().contains("shop")) {
                coloredTitle = ChatColor.of("#00FFFF") + sectionName;
            } else {
                coloredTitle = ChatColor.of("#00FFFF") + sectionName + ChatColor.of("#FFFFFF") + " Shop";
            }
        }
        
        Inventory inv = Bukkit.createInventory(null, 54, coloredTitle);
        
        List<ShopItem> allItems = section.getItems();
        int maxPage = (allItems.size() - 1) / ITEMS_PER_PAGE;
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allItems.size());
        
        // Get player balance
        double coins = economy.getBalance(player.getUniqueId(), CurrencyType.COINS);
        
        // Add items for this page (slots 0-44)
        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            ShopItem shopItem = allItems.get(i);
            ItemStack display = shopItem.createItemStack(1);
            ItemMeta meta = display.getItemMeta();
            if (meta != null) {
                // Keep original item name but add clean lore
                List<String> lore = new ArrayList<>();
                lore.add("");
                
                if (shopItem.canBuy()) {
                    lore.add(ChatColor.of("#55FF55") + "● Buy: " + ChatColor.of("#FFFFFF") + 
                            MONEY_FORMAT.format(shopItem.getBuyPrice()) + ChatColor.of("#FFFF00") + " Coins");
                    lore.add(ChatColor.of("#808080") + "  └ " + ChatColor.of("#FFFF00") + "Left Click" + 
                            ChatColor.of("#808080") + " to purchase");
                } else {
                    lore.add(ChatColor.of("#FF5555") + "✖ Cannot buy");
                }
                
                lore.add("");
                
                if (shopItem.canSell()) {
                    lore.add(ChatColor.of("#FFD700") + "● Sell: " + ChatColor.of("#FFFFFF") + 
                            MONEY_FORMAT.format(shopItem.getSellPrice()) + ChatColor.of("#FFFF00") + " Coins");
                    lore.add(ChatColor.of("#808080") + "  └ " + ChatColor.of("#FFFF00") + "Right Click" + 
                            ChatColor.of("#808080") + " to sell");
                } else {
                    lore.add(ChatColor.of("#FF5555") + "✖ Cannot sell");
                }
                
                meta.setLore(lore);
                display.setItemMeta(meta);
            }
            inv.setItem(slot++, display);
        }
        
        // Bottom navigation bar (slots 45-53) matching skills menu style
        // Fill bottom row with black glass first
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName(" ");
            border.setItemMeta(borderMeta);
        }
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, border);
        }
        
        // Page info (slot 45 - bottom left)
        ItemStack pageInfo = new ItemStack(Material.BOOK);
        ItemMeta pageInfoMeta = pageInfo.getItemMeta();
        if (pageInfoMeta != null) {
            pageInfoMeta.setDisplayName(ChatColor.of("#00FFFF") + "Page " + (page + 1) + " of " + (maxPage + 1));
            List<String> pageLore = new ArrayList<>();
            pageLore.add("");
            pageLore.add(ChatColor.of("#808080") + "Showing items " + 
                    ChatColor.of("#FFFFFF") + (startIndex + 1) + "-" + endIndex + 
                    ChatColor.of("#808080") + " of " + ChatColor.of("#FFFFFF") + allItems.size());
            pageInfoMeta.setLore(pageLore);
            pageInfo.setItemMeta(pageInfoMeta);
        }
        inv.setItem(45, pageInfo);
        
        // Balance display (slot 46)
        ItemStack balanceItem = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta balanceMeta = balanceItem.getItemMeta();
        if (balanceMeta != null) {
            balanceMeta.setDisplayName(ChatColor.of("#FFFF00") + "Your Coins: " + 
                    ChatColor.of("#FFFFFF") + MONEY_FORMAT.format(coins));
            balanceItem.setItemMeta(balanceMeta);
        }
        inv.setItem(46, balanceItem);
        
        // Previous page (slot 48)
        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(ChatColor.of("#FFFF00") + "← Previous Page");
                List<String> prevLore = new ArrayList<>();
                prevLore.add("");
                prevLore.add(ChatColor.of("#808080") + "Go to page " + ChatColor.of("#FFFFFF") + page);
                prevMeta.setLore(prevLore);
                prevPage.setItemMeta(prevMeta);
            }
            inv.setItem(48, prevPage);
        }
        
        // Next page (slot 50)
        if (page < maxPage) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(ChatColor.of("#FFFF00") + "Next Page →");
                List<String> nextLore = new ArrayList<>();
                nextLore.add("");
                nextLore.add(ChatColor.of("#808080") + "Go to page " + ChatColor.of("#FFFFFF") + (page + 2));
                nextMeta.setLore(nextLore);
                nextPage.setItemMeta(nextMeta);
            }
            inv.setItem(50, nextPage);
        }
        
        // Back button (slot 53 - bottom right, matching skills menu close position)
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.of("#FF5555") + "← Back");
            List<String> backLore = new ArrayList<>();
            backLore.add("");
            backLore.add(ChatColor.of("#808080") + "Return to main shop menu");
            backMeta.setLore(backLore);
            back.setItemMeta(backMeta);
        }
        inv.setItem(53, back);
        
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        if (!title.contains("Shop")) return;
        if (title.contains("SkillCoins Shop")) return; // Main menu
        
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        int slot = event.getSlot();
        Integer currentPage = playerPages.getOrDefault(player.getUniqueId(), 0);
        
        // Navigation buttons
        if (slot == 48 && currentPage > 0) {
            open(player, currentPage - 1);
            return;
        }
        
        List<ShopItem> allItems = section.getItems();
        int maxPage = (allItems.size() - 1) / ITEMS_PER_PAGE;
        
        if (slot == 50 && currentPage < maxPage) {
            open(player, currentPage + 1);
            return;
        }
        
        if (slot == 53 && clicked.getType() == Material.BARRIER) {
            new ShopMainMenu(plugin, economy).open(player);
            return;
        }
        
        // Item click (slots 0-44)
        if (slot < ITEMS_PER_PAGE) {
            int itemIndex = currentPage * ITEMS_PER_PAGE + slot;
            if (itemIndex < allItems.size()) {
                ShopItem shopItem = allItems.get(itemIndex);
                
                ClickType clickType = event.getClick();
                boolean isBuying = clickType.isLeftClick();
                boolean isSelling = clickType.isRightClick();
                
                if (isBuying && shopItem.canBuy()) {
                    new TransactionMenu(plugin, economy, shopItem, true, section).open(player);
                } else if (isSelling && shopItem.canSell()) {
                    new TransactionMenu(plugin, economy, shopItem, false, section).open(player);
                }
            }
        }
    }
}
