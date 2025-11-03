package dev.aurelium.auraskills.bukkit.skillcoins.menu;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skillcoins.shop.ShopSection;
import dev.aurelium.auraskills.common.skillcoins.CurrencyType;
import dev.aurelium.auraskills.common.skillcoins.EconomyProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main shop menu showing all sections
 */
public class ShopMainMenu implements Listener {
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    
    public ShopMainMenu(AuraSkills plugin, EconomyProvider economy) {
        this.plugin = plugin;
        this.economy = economy;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void open(Player player) {
        // Create 54-slot inventory (6 rows) for better layout
        Inventory inv = Bukkit.createInventory(null, 54, 
                ChatColor.of("#00FFFF") + "❖ " + ChatColor.of("#FFFFFF") + "SkillCoins Shop");
        
        UUID uuid = player.getUniqueId();
        double coins = economy.getBalance(uuid, CurrencyType.COINS);
        double tokens = economy.getBalance(uuid, CurrencyType.TOKENS);
        
        // Fill with black glass pane first
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName(" ");
            border.setItemMeta(borderMeta);
        }
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, border);
        }
        
        // Top bar - Player info (slot 0)
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        if (skullMeta != null) {
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
        
        // Balance display (slot 8 - top right, matching skills menu layout)
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
        
                // Shop sections - beautifully arranged from config slots
        List<ShopSection> sections = plugin.getShopLoader().getSections();
        
        // LAYOUT: Config-driven slot positioning
        // Row 2 (11-14): Tools, Combat, Resources, Enchants (4 item shops)
        // Row 3 (20-25): Food, Blocks, Farming, Potions, Redstone, Misc (6 more shops)  
        // Row 5 (38-39): Token Exchange, Skill Levels (separated token services)
        
        for (ShopSection section : sections) {
            int slot = section.getSlot(); // Get slot from config file
            
            ItemStack item = new ItemStack(section.getIcon());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                // Use display name from config but with color formatting
                String displayName = ChatColor.translateAlternateColorCodes('&', section.getDisplayName());
                meta.setDisplayName(displayName);
                
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
        }
        
        // Close button (slot 53 - bottom right, matching skills menu)
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
        
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        if (!title.contains("SkillCoins Shop")) return;
        
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) return;
        
        int slot = event.getSlot();
        
        // Close button
        if (slot == 53 && clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }
        
        // Check if clicked slot matches any section (using config slots)
        List<ShopSection> sections = plugin.getShopLoader().getSections();
        for (ShopSection section : sections) {
            if (slot == section.getSlot()) {
                // Special handling for Token Exchange - open custom menu
                if (section.getId().equalsIgnoreCase("TokenExchange")) {
                    new TokenExchangeMenu(plugin, economy).open(player);
                } else {
                    new ShopSectionMenu(plugin, economy, section).open(player, 0);
                }
                return;
            }
        }
    }
}
