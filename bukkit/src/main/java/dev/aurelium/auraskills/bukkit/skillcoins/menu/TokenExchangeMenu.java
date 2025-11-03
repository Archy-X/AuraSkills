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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Token Exchange menu for buying tokens with coins
 */
public class TokenExchangeMenu implements Listener {
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    private static final Map<UUID, Integer> quantities = new HashMap<>();
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    private static final int COINS_PER_TOKEN = 100; // Base rate: 100 coins = 1 token
    
    public TokenExchangeMenu(AuraSkills plugin, EconomyProvider economy) {
        this.plugin = plugin;
        this.economy = economy;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void open(Player player) {
        quantities.putIfAbsent(player.getUniqueId(), 1);
        updateInventory(player);
    }
    
    private void updateInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, 
                ChatColor.of("#00FF00") + "Token Exchange");
        
        int quantity = quantities.getOrDefault(player.getUniqueId(), 1);
        int totalCoins = COINS_PER_TOKEN * quantity;
        double balance = economy.getBalance(player.getUniqueId(), CurrencyType.COINS);
        double tokenBalance = economy.getBalance(player.getUniqueId(), CurrencyType.TOKENS);
        
        // Fill with black glass pane border
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName(" ");
            border.setItemMeta(borderMeta);
        }
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, border);
        }
        
        // Token display (slot 13 - center top)
        ItemStack display = new ItemStack(Material.EMERALD, Math.min(quantity, 64));
        ItemMeta displayMeta = display.getItemMeta();
        if (displayMeta != null) {
            displayMeta.setDisplayName(ChatColor.of("#00FFFF") + "Skill Tokens");
            
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.of("#808080") + "Tokens to Buy: " + ChatColor.of("#FFFFFF") + quantity);
            lore.add(ChatColor.of("#808080") + "Cost per Token: " + ChatColor.of("#FFD700") + COINS_PER_TOKEN + " Coins");
            lore.add("");
            lore.add(ChatColor.of("#FFD700") + "Total Cost: " + ChatColor.of("#FFFFFF") + 
                    MONEY_FORMAT.format(totalCoins) + ChatColor.of("#FFFF00") + " Coins");
            lore.add("");
            
            if (balance >= totalCoins) {
                lore.add(ChatColor.of("#55FF55") + "✔ Sufficient Funds");
                lore.add(ChatColor.of("#808080") + "  Balance after: " + ChatColor.of("#FFFFFF") + 
                        MONEY_FORMAT.format(balance - totalCoins) + ChatColor.of("#FFFF00") + " Coins");
            } else {
                lore.add(ChatColor.of("#FF5555") + "✖ Insufficient Funds");
                lore.add(ChatColor.of("#808080") + "  Need: " + ChatColor.of("#FF5555") + 
                        MONEY_FORMAT.format(totalCoins - balance) + ChatColor.of("#FFFF00") + " more Coins");
            }
            
            displayMeta.setLore(lore);
            display.setItemMeta(displayMeta);
        }
        inv.setItem(13, display);
        
        // Quantity controls - Row 3 (centered, better spacing)
        // -10 button (slot 20)
        createQuantityButton(inv, 20, Material.RED_TERRACOTTA, 
                ChatColor.of("#FF5555") + "▼▼ -10", 
                quantity > 10, 
                ChatColor.of("#808080") + "Remove 10 tokens");
        
        // -1 button (slot 21)
        createQuantityButton(inv, 21, Material.ORANGE_TERRACOTTA, 
                ChatColor.of("#FF5555") + "▼ -1", 
                quantity > 1, 
                ChatColor.of("#808080") + "Remove 1 token");
        
        // Quantity display (slot 22 - center) - Using EMERALD for consistency
        ItemStack qtyDisplay = new ItemStack(Material.EMERALD, Math.min(quantity, 64));
        ItemMeta qtyMeta = qtyDisplay.getItemMeta();
        if (qtyMeta != null) {
            qtyMeta.setDisplayName(ChatColor.of("#00FFFF") + "Amount: " + ChatColor.of("#FFFFFF") + quantity);
            List<String> qtyLore = new ArrayList<>();
            qtyLore.add("");
            qtyLore.add(ChatColor.of("#808080") + "Current quantity to purchase");
            qtyLore.add(ChatColor.of("#808080") + "Use +/- buttons to adjust");
            qtyMeta.setLore(qtyLore);
            qtyDisplay.setItemMeta(qtyMeta);
        }
        inv.setItem(22, qtyDisplay);
        
        // +1 button (slot 23)
        createQuantityButton(inv, 23, Material.LIME_TERRACOTTA, 
                ChatColor.of("#55FF55") + "▲ +1", 
                quantity < 1000, 
                ChatColor.of("#808080") + "Add 1 token");
        
        // +10 button (slot 24)
        createQuantityButton(inv, 24, Material.GREEN_TERRACOTTA, 
                ChatColor.of("#55FF55") + "▲▲ +10", 
                quantity <= 990, 
                ChatColor.of("#808080") + "Add 10 tokens");
        
        // Quick select preset amounts button (slot 31 - center of next row)
        ItemStack presetBtn = new ItemStack(Material.NETHER_STAR);
        ItemMeta presetMeta = presetBtn.getItemMeta();
        if (presetMeta != null) {
            presetMeta.setDisplayName(ChatColor.of("#FFFF00") + "⚡ Quick Select");
            List<String> presetLore = new ArrayList<>();
            presetLore.add("");
            presetLore.add(ChatColor.of("#808080") + "Choose from preset amounts:");
            presetLore.add(ChatColor.of("#FFD700") + "  ● " + ChatColor.of("#FFFFFF") + "1, 10, 50, 100, or 500 tokens");
            presetLore.add("");
            presetLore.add(ChatColor.of("#55FF55") + "▸ Click to open menu!");
            presetMeta.setLore(presetLore);
            presetBtn.setItemMeta(presetMeta);
        }
        inv.setItem(31, presetBtn);
        
        // Bottom action buttons
        // Balance display (slot 45 - bottom left)
        ItemStack balanceItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta balanceMeta = balanceItem.getItemMeta();
        if (balanceMeta != null) {
            balanceMeta.setDisplayName(ChatColor.of("#FFD700") + "Your Balance");
            List<String> balanceLore = new ArrayList<>();
            balanceLore.add("");
            balanceLore.add(ChatColor.of("#FFFF00") + "Coins: " + ChatColor.of("#FFFFFF") + 
                    MONEY_FORMAT.format(balance));
            balanceLore.add(ChatColor.of("#00FFFF") + "Tokens: " + ChatColor.of("#FFFFFF") + 
                    MONEY_FORMAT.format(tokenBalance));
            balanceMeta.setLore(balanceLore);
            balanceItem.setItemMeta(balanceMeta);
        }
        inv.setItem(45, balanceItem);
        
        // Confirm button (slot 49 - center bottom)
        boolean canAfford = balance >= totalCoins;
        ItemStack confirm = new ItemStack(canAfford ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
        ItemMeta confirmMeta = confirm.getItemMeta();
        if (confirmMeta != null) {
            if (canAfford) {
                confirmMeta.setDisplayName(ChatColor.of("#55FF55") + "✔ " + 
                        ChatColor.of("#FFFFFF") + "CONFIRM PURCHASE");
                List<String> confirmLore = new ArrayList<>();
                confirmLore.add("");
                confirmLore.add(ChatColor.of("#808080") + "Buy " + ChatColor.of("#00FFFF") + quantity + 
                        ChatColor.of("#808080") + " tokens for");
                confirmLore.add(ChatColor.of("#FFD700") + MONEY_FORMAT.format(totalCoins) + " Coins");
                confirmLore.add("");
                confirmLore.add(ChatColor.of("#55FF55") + "▸ Click to confirm!");
                confirmMeta.setLore(confirmLore);
            } else {
                confirmMeta.setDisplayName(ChatColor.of("#FF5555") + "✖ Cannot Purchase");
                List<String> confirmLore = new ArrayList<>();
                confirmLore.add("");
                confirmLore.add(ChatColor.of("#FF5555") + "Not enough coins!");
                confirmMeta.setLore(confirmLore);
                confirmMeta.setLore(confirmLore);
            }
            confirm.setItemMeta(confirmMeta);
        }
        inv.setItem(49, confirm);
        
        // Back button (slot 53 - bottom right)
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
        
        player.openInventory(inv);
    }
    
    private void createQuantityButton(Inventory inv, int slot, Material material, String name, 
                                      boolean enabled, String description) {
        ItemStack item = new ItemStack(enabled ? material : Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(enabled ? name : ChatColor.of("#808080") + "Cannot adjust");
            if (enabled) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(description);
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        inv.setItem(slot, item);
    }
    
    private void createPresetButton(Inventory inv, int slot, int amount, String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.of("#FFFF00") + name);
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.of("#808080") + "Cost: " + ChatColor.of("#FFD700") + 
                    MONEY_FORMAT.format(COINS_PER_TOKEN * amount) + " Coins");
            lore.add("");
            lore.add(ChatColor.of("#55FF55") + "▸ Click to set amount!");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        inv.setItem(slot, item);
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        if (!title.contains("Token Exchange")) return;
        
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) return;
        
        int slot = event.getSlot();
        Integer currentQty = quantities.getOrDefault(player.getUniqueId(), 1);
        
        // Quantity adjustments
        if (slot == 20 && currentQty > 10) {
            quantities.put(player.getUniqueId(), currentQty - 10);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.8f);
            updateInventory(player);
            return;
        }
        
        if (slot == 21 && currentQty > 1) {
            quantities.put(player.getUniqueId(), currentQty - 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.8f);
            updateInventory(player);
            return;
        }
        
        if (slot == 23 && currentQty < 1000) {
            quantities.put(player.getUniqueId(), currentQty + 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
            updateInventory(player);
            return;
        }
        
        if (slot == 24 && currentQty <= 990) {
            quantities.put(player.getUniqueId(), Math.min(1000, currentQty + 10));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
            updateInventory(player);
            return;
        }
        
        // Quick select preset menu
        if (slot == 31 && clicked.getType() == Material.NETHER_STAR) {
            openPresetMenu(player);
            return;
        }
        
        // Back button
        if (slot == 53 && clicked.getType() == Material.ARROW) {
            new ShopMainMenu(plugin, economy).open(player);
            return;
        }
        
        // Confirm purchase
        if (slot == 49 && clicked.getType() == Material.EMERALD_BLOCK) {
            int quantity = quantities.getOrDefault(player.getUniqueId(), 1);
            int totalCoins = COINS_PER_TOKEN * quantity;
            double balance = economy.getBalance(player.getUniqueId(), CurrencyType.COINS);
            
            if (balance >= totalCoins) {
                economy.subtractBalance(player.getUniqueId(), CurrencyType.COINS, totalCoins);
                economy.addBalance(player.getUniqueId(), CurrencyType.TOKENS, quantity);
                
                player.sendMessage(ChatColor.of("#55FF55") + "✔ " + ChatColor.of("#FFFFFF") + 
                        "Successfully purchased " + ChatColor.of("#00FFFF") + quantity + 
                        ChatColor.of("#FFFFFF") + " token" + (quantity > 1 ? "s" : "") + 
                        " for " + ChatColor.of("#FFD700") + MONEY_FORMAT.format(totalCoins) + 
                        " Coins!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                
                // Reset quantity and refresh
                quantities.put(player.getUniqueId(), 1);
                updateInventory(player);
            } else {
                player.sendMessage(ChatColor.of("#FF5555") + "✖ Not enough coins!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }
    }
    
    private void openPresetMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, 
                ChatColor.of("#FFFF00") + "Quick Select Amount");
        
        // Fill with black glass pane border
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName(" ");
            border.setItemMeta(borderMeta);
        }
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, border);
        }
        
        // Preset buttons (centered row)
        createPresetButton(inv, 10, 1, "1 Token", Material.GOLD_NUGGET);
        createPresetButton(inv, 11, 10, "10 Tokens", Material.GOLD_INGOT);
        createPresetButton(inv, 12, 50, "50 Tokens", Material.GOLD_BLOCK);
        createPresetButton(inv, 13, 100, "100 Tokens", Material.EMERALD);
        createPresetButton(inv, 14, 500, "500 Tokens", Material.EMERALD_BLOCK);
        createPresetButton(inv, 16, 1000, "1000 Tokens (Max)", Material.DIAMOND);
        
        // Back button (slot 22 - bottom right)
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.of("#FFFF00") + "← Back");
            List<String> backLore = new ArrayList<>();
            backLore.add("");
            backLore.add(ChatColor.of("#808080") + "Return to token exchange");
            backMeta.setLore(backLore);
            back.setItemMeta(backMeta);
        }
        inv.setItem(22, back);
        
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title.contains("Token Exchange") || title.contains("Quick Select Amount")) {
            quantities.remove(event.getPlayer().getUniqueId());
        }
    }
    
    @EventHandler
    public void onPresetClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        if (!title.contains("Quick Select Amount")) return;
        
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) return;
        
        int slot = event.getSlot();
        
        // Preset selections
        if (slot == 10) {
            quantities.put(player.getUniqueId(), 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            updateInventory(player);
            return;
        }
        if (slot == 11) {
            quantities.put(player.getUniqueId(), 10);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            updateInventory(player);
            return;
        }
        if (slot == 12) {
            quantities.put(player.getUniqueId(), 50);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            updateInventory(player);
            return;
        }
        if (slot == 13) {
            quantities.put(player.getUniqueId(), 100);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            updateInventory(player);
            return;
        }
        if (slot == 14) {
            quantities.put(player.getUniqueId(), 500);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            updateInventory(player);
            return;
        }
        if (slot == 16) {
            quantities.put(player.getUniqueId(), 1000);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            updateInventory(player);
            return;
        }
        
        // Back button
        if (slot == 22 && clicked.getType() == Material.ARROW) {
            updateInventory(player);
            return;
        }
    }
}
