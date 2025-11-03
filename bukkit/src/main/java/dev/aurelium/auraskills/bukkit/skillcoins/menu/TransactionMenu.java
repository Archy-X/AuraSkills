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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Transaction menu for buying/selling items with quantity adjustment
 */
public class TransactionMenu implements Listener {
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    private final ShopItem shopItem;
    private final boolean isBuying;
    private final ShopSection section;
    private static final Map<UUID, Integer> quantities = new HashMap<>();
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    
    public TransactionMenu(AuraSkills plugin, EconomyProvider economy, ShopItem shopItem, 
                          boolean isBuying, ShopSection section) {
        this.plugin = plugin;
        this.economy = economy;
        this.shopItem = shopItem;
        this.isBuying = isBuying;
        this.section = section;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void open(Player player) {
        quantities.putIfAbsent(player.getUniqueId(), 1);
        updateInventory(player);
    }
    
    private void updateInventory(Player player) {
        String title = (isBuying ? ChatColor.of("#55FF55") + "Buy" : ChatColor.of("#FFD700") + "Sell") + 
                ChatColor.of("#FFFFFF") + " Item";
        Inventory inv = Bukkit.createInventory(null, 54, title);
        
        int quantity = quantities.getOrDefault(player.getUniqueId(), 1);
        double price = isBuying ? shopItem.getBuyPrice() : shopItem.getSellPrice();
        double totalPrice = price * quantity;
        double balance = economy.getBalance(player.getUniqueId(), CurrencyType.COINS);
        int playerItemCount = countItems(player, shopItem);
        
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
        
        // Item display (slot 13 - center top)
        ItemStack display = shopItem.createItemStack(quantity);
        ItemMeta displayMeta = display.getItemMeta();
        if (displayMeta != null) {
            String itemName = displayMeta.hasDisplayName() ? displayMeta.getDisplayName() : 
                    shopItem.getMaterial().name().replace("_", " ");
            displayMeta.setDisplayName(ChatColor.of("#00FFFF") + itemName);
            
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.of("#808080") + "Quantity: " + ChatColor.of("#FFFFFF") + quantity + "x");
            lore.add(ChatColor.of("#808080") + "Price Each: " + ChatColor.of("#FFFF00") + 
                    MONEY_FORMAT.format(price) + " Coins");
            lore.add("");
            lore.add(ChatColor.of("#FFD700") + "Total Cost: " + ChatColor.of("#FFFFFF") + 
                    MONEY_FORMAT.format(totalPrice) + ChatColor.of("#FFFF00") + " Coins");
            lore.add("");
            
            if (isBuying) {
                if (balance >= totalPrice) {
                    lore.add(ChatColor.of("#55FF55") + "✔ Sufficient Funds");
                    lore.add(ChatColor.of("#808080") + "  Balance after: " + ChatColor.of("#FFFFFF") + 
                            MONEY_FORMAT.format(balance - totalPrice) + ChatColor.of("#FFFF00") + " Coins");
                } else {
                    lore.add(ChatColor.of("#FF5555") + "✖ Insufficient Funds");
                    lore.add(ChatColor.of("#808080") + "  Need: " + ChatColor.of("#FF5555") + 
                            MONEY_FORMAT.format(totalPrice - balance) + ChatColor.of("#FFFF00") + " more Coins");
                }
            } else {
                if (playerItemCount >= quantity) {
                    lore.add(ChatColor.of("#55FF55") + "✔ You have " + playerItemCount + " items");
                    lore.add(ChatColor.of("#808080") + "  You'll receive: " + ChatColor.of("#FFFFFF") + 
                            MONEY_FORMAT.format(totalPrice) + ChatColor.of("#FFFF00") + " Coins");
                } else {
                    lore.add(ChatColor.of("#FF5555") + "✖ Not enough items");
                    lore.add(ChatColor.of("#808080") + "  Need: " + ChatColor.of("#FF5555") + 
                            (quantity - playerItemCount) + " more items");
                }
            }
            
            displayMeta.setLore(lore);
            display.setItemMeta(displayMeta);
        }
        inv.setItem(13, display);
        
        // Quantity controls - Row 3 (slots 19-25), beautifully centered
        // -10 button (slot 19)
        createQuantityButton(inv, 19, Material.RED_TERRACOTTA, 
                ChatColor.of("#FF5555") + "▼▼ -10", 
                quantity > 10, 
                ChatColor.of("#808080") + "Remove 10 from quantity");
        
        // -1 button (slot 20)
        createQuantityButton(inv, 20, Material.ORANGE_TERRACOTTA, 
                ChatColor.of("#FF5555") + "▼ -1", 
                quantity > 1, 
                ChatColor.of("#808080") + "Remove 1 from quantity");
        
        // Quantity display (slot 22 - center)
        ItemStack qtyDisplay = new ItemStack(Material.PAPER);
        ItemMeta qtyMeta = qtyDisplay.getItemMeta();
        if (qtyMeta != null) {
            qtyMeta.setDisplayName(ChatColor.of("#FFFF00") + "Quantity: " + ChatColor.of("#FFFFFF") + quantity);
            List<String> qtyLore = new ArrayList<>();
            qtyLore.add("");
            qtyLore.add(ChatColor.of("#808080") + "Use the buttons to adjust");
            qtyLore.add(ChatColor.of("#808080") + "the quantity to buy/sell");
            qtyMeta.setLore(qtyLore);
            qtyDisplay.setItemMeta(qtyMeta);
        }
        inv.setItem(22, qtyDisplay);
        
        // +1 button (slot 24)
        createQuantityButton(inv, 24, Material.LIME_TERRACOTTA, 
                ChatColor.of("#55FF55") + "▲ +1", 
                quantity < 64, 
                ChatColor.of("#808080") + "Add 1 to quantity");
        
        // +10 button (slot 25)
        createQuantityButton(inv, 25, Material.GREEN_TERRACOTTA, 
                ChatColor.of("#55FF55") + "▲▲ +10", 
                quantity <= 54, 
                ChatColor.of("#808080") + "Add 10 to quantity");
        
        // Action buttons - Row 5 (slots 45-53)
        // Back button (slot 53 - matching all other menus)
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.of("#FF5555") + "← Back");
            List<String> backLore = new ArrayList<>();
            backLore.add("");
            backLore.add(ChatColor.of("#808080") + "Return to shop");
            backMeta.setLore(backLore);
            back.setItemMeta(backMeta);
        }
        inv.setItem(53, back);
        
        // Balance display (slot 46-47 area, use 46)
        ItemStack balanceItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta balanceMeta = balanceItem.getItemMeta();
        if (balanceMeta != null) {
            balanceMeta.setDisplayName(ChatColor.of("#FFD700") + "Your Balance");
            List<String> balanceLore = new ArrayList<>();
            balanceLore.add("");
            balanceLore.add(ChatColor.of("#FFFF00") + "Coins: " + ChatColor.of("#FFFFFF") + 
                    MONEY_FORMAT.format(balance));
            balanceMeta.setLore(balanceLore);
            balanceItem.setItemMeta(balanceMeta);
        }
        inv.setItem(46, balanceItem);
        
        // Confirm button (slot 49 - center bottom)
        boolean canAfford = isBuying ? (balance >= totalPrice) : (playerItemCount >= quantity);
        ItemStack confirm = new ItemStack(canAfford ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
        ItemMeta confirmMeta = confirm.getItemMeta();
        if (confirmMeta != null) {
            if (canAfford) {
                confirmMeta.setDisplayName(ChatColor.of("#55FF55") + "✔ " + 
                        ChatColor.of("#FFFFFF") + "CONFIRM " + 
                        (isBuying ? "PURCHASE" : "SALE"));
                List<String> confirmLore = new ArrayList<>();
                confirmLore.add("");
                if (isBuying) {
                    confirmLore.add(ChatColor.of("#55FF55") + "Buy " + ChatColor.of("#FFFFFF") + 
                            quantity + "x " + shopItem.getMaterial().name().replace("_", " "));
                    confirmLore.add(ChatColor.of("#808080") + "for " + ChatColor.of("#FFFF00") + 
                            MONEY_FORMAT.format(totalPrice) + " Coins");
                } else {
                    confirmLore.add(ChatColor.of("#FFD700") + "Sell " + ChatColor.of("#FFFFFF") + 
                            quantity + "x " + shopItem.getMaterial().name().replace("_", " "));
                    confirmLore.add(ChatColor.of("#808080") + "for " + ChatColor.of("#FFFF00") + 
                            MONEY_FORMAT.format(totalPrice) + " Coins");
                }
                confirmLore.add("");
                confirmLore.add(ChatColor.of("#FFFF00") + "Click to confirm!");
                confirmMeta.setLore(confirmLore);
            } else {
                confirmMeta.setDisplayName(ChatColor.of("#FF5555") + "✖ Cannot Complete Transaction");
                List<String> confirmLore = new ArrayList<>();
                confirmLore.add("");
                if (isBuying) {
                    confirmLore.add(ChatColor.of("#FF5555") + "Insufficient funds!");
                } else {
                    confirmLore.add(ChatColor.of("#FF5555") + "Not enough items!");
                }
                confirmMeta.setLore(confirmLore);
            }
            confirm.setItemMeta(confirmMeta);
        }
        inv.setItem(49, confirm);
        
        player.openInventory(inv);
    }
    
    private void createQuantityButton(Inventory inv, int slot, Material material, String name, 
                                     boolean enabled, String description) {
        ItemStack button = new ItemStack(enabled ? material : Material.GRAY_TERRACOTTA);
        ItemMeta meta = button.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(enabled ? name : ChatColor.of("#555555") + name.substring(7));
            if (enabled) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(description);
                meta.setLore(lore);
            }
            button.setItemMeta(meta);
        }
        inv.setItem(slot, button);
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        if (!title.contains("Buy") && !title.contains("Sell")) return;
        if (!title.contains("Item")) return;
        
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        int quantity = quantities.getOrDefault(player.getUniqueId(), 1);
        
        // Quantity adjustments
        if (slot == 19) { // -10
            if (quantity > 10) {
                quantities.put(player.getUniqueId(), quantity - 10);
                updateInventory(player);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.8f);
            }
        } else if (slot == 20) { // -1
            if (quantity > 1) {
                quantities.put(player.getUniqueId(), quantity - 1);
                updateInventory(player);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.9f);
            }
        } else if (slot == 24) { // +1
            if (quantity < 64) {
                quantities.put(player.getUniqueId(), quantity + 1);
                updateInventory(player);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.1f);
            }
        } else if (slot == 25) { // +10
            if (quantity <= 54) {
                quantities.put(player.getUniqueId(), Math.min(64, quantity + 10));
                updateInventory(player);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
            }
        } else if (slot == 49) { // Confirm
            performTransaction(player, quantity);
        } else if (slot == 53) { // Back (slot 53 to match other menus)
            new ShopSectionMenu(plugin, economy, section).open(player, 0);
            quantities.remove(player.getUniqueId());
        }
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title.contains("Buy Item") || title.contains("Sell Item")) {
            quantities.remove(event.getPlayer().getUniqueId());
        }
    }
    
    private void performTransaction(Player player, int quantity) {
        double price = isBuying ? shopItem.getBuyPrice() : shopItem.getSellPrice();
        double totalPrice = price * quantity;
        
        if (isBuying) {
            // Handle different item types
            switch (shopItem.getType()) {
                case SKILL_LEVEL:
                    performSkillLevelPurchase(player, quantity, totalPrice);
                    break;
                case TOKEN_EXCHANGE:
                    performTokenExchange(player, quantity, totalPrice);
                    break;
                case REGULAR:
                default:
                    performRegularPurchase(player, quantity, totalPrice);
                    break;
            }
        } else {
            // Sell transaction
            int itemCount = countItems(player, shopItem);
            if (itemCount < quantity) {
                player.sendMessage(ChatColor.of("#FF5555") + "✖ " + 
                        ChatColor.of("#FFFFFF") + "You don't have enough items to sell!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            
            // Remove items from inventory
            removeItems(player, shopItem, quantity);
            
            // Add coins
            economy.addBalance(player.getUniqueId(), CurrencyType.COINS, totalPrice);
            
            player.sendMessage(ChatColor.of("#55FF55") + "✔ Sale Successful! " + 
                    ChatColor.of("#FFFFFF") + "Sold " + 
                    ChatColor.of("#FFFF00") + quantity + "x " + 
                    ChatColor.of("#00FFFF") + shopItem.getMaterial().name().replace("_", " ") + 
                    ChatColor.of("#FFFFFF") + " for " + 
                    ChatColor.of("#FFD700") + MONEY_FORMAT.format(totalPrice) + " Coins");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        }
        
        player.closeInventory();
        quantities.remove(player.getUniqueId());
    }
    
    private void performRegularPurchase(Player player, int quantity, double totalPrice) {
        // Regular item purchase
        if (!economy.hasBalance(player.getUniqueId(), shopItem.getCurrency(), totalPrice)) {
            String currencyName = shopItem.getCurrency() == CurrencyType.COINS ? "SkillCoins" : "SkillTokens";
            player.sendMessage(ChatColor.of("#FF5555") + "✖ " + 
                    ChatColor.of("#FFFFFF") + "You don't have enough " + currencyName + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Check if player has inventory space
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ " + 
                    ChatColor.of("#FFFFFF") + "Your inventory is full!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Perform transaction
        economy.subtractBalance(player.getUniqueId(), shopItem.getCurrency(), totalPrice);
        ItemStack item = shopItem.createItemStack(quantity);
        player.getInventory().addItem(item);
        
        String currencySymbol = shopItem.getCurrency() == CurrencyType.COINS ? "Coins" : "Tokens";
        player.sendMessage(ChatColor.of("#55FF55") + "✔ Purchase Successful! " + 
                ChatColor.of("#FFFFFF") + "Bought " + 
                ChatColor.of("#FFFF00") + quantity + "x " + 
                ChatColor.of("#00FFFF") + shopItem.getMaterial().name().replace("_", " ") + 
                ChatColor.of("#FFFFFF") + " for " + 
                ChatColor.of("#FFD700") + MONEY_FORMAT.format(totalPrice) + " " + currencySymbol);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        
        player.closeInventory();
        quantities.remove(player.getUniqueId());
    }
    
    private void performSkillLevelPurchase(Player player, int quantity, double totalPrice) {
        // Check if player has enough tokens
        if (!economy.hasBalance(player.getUniqueId(), CurrencyType.TOKENS, totalPrice)) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ " + 
                    ChatColor.of("#FFFFFF") + "You don't have enough Skill Tokens!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Get the skill
        String skillName = shopItem.getSkillName();
        if (skillName == null) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error: Invalid skill configuration!");
            return;
        }
        
        dev.aurelium.auraskills.api.skill.Skill skill = null;
        try {
            skill = dev.aurelium.auraskills.api.skill.Skills.valueOf(skillName.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error: Unknown skill: " + skillName);
            return;
        }
        
        if (skill == null || !skill.isEnabled()) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ This skill is not available!");
            return;
        }
        
        // Get user and add levels
        dev.aurelium.auraskills.common.user.User user = plugin.getUser(player);
        if (user == null) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error: Player data not loaded!");
            return;
        }
        
        int currentLevel = user.getSkillLevel(skill);
        int maxLevel = skill.getMaxLevel();
        
        if (currentLevel + quantity > maxLevel) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ " + 
                    ChatColor.of("#FFFFFF") + "You can't exceed the max level (" + maxLevel + ") for this skill!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Perform transaction
        economy.subtractBalance(player.getUniqueId(), CurrencyType.TOKENS, totalPrice);
        user.setSkillLevel(skill, currentLevel + quantity);
        
        player.sendMessage(ChatColor.of("#55FF55") + "✔ Skill Level Purchase Successful!");
        player.sendMessage(ChatColor.of("#FFFFFF") + "Purchased " + 
                ChatColor.of("#FFFF00") + quantity + " level" + (quantity > 1 ? "s" : "") + 
                ChatColor.of("#FFFFFF") + " in " + 
                ChatColor.of("#00FFFF") + skill.getDisplayName(user.getLocale()) + 
                ChatColor.of("#FFFFFF") + " for " + 
                ChatColor.of("#00FFFF") + MONEY_FORMAT.format(totalPrice) + " Tokens");
        player.sendMessage(ChatColor.of("#808080") + "New Level: " + ChatColor.of("#FFFFFF") + (currentLevel + quantity));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
        
        player.closeInventory();
        quantities.remove(player.getUniqueId());
    }
    
    private void performTokenExchange(Player player, int quantity, double totalPrice) {
        // Check if player has enough coins
        if (!economy.hasBalance(player.getUniqueId(), CurrencyType.COINS, totalPrice)) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ " + 
                    ChatColor.of("#FFFFFF") + "You don't have enough SkillCoins!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        int tokensToReceive = shopItem.getTokenAmount() * quantity;
        
        // Perform transaction
        economy.subtractBalance(player.getUniqueId(), CurrencyType.COINS, totalPrice);
        economy.addBalance(player.getUniqueId(), CurrencyType.TOKENS, tokensToReceive);
        
        player.sendMessage(ChatColor.of("#55FF55") + "✔ Exchange Successful!");
        player.sendMessage(ChatColor.of("#FFFFFF") + "Exchanged " + 
                ChatColor.of("#FFD700") + MONEY_FORMAT.format(totalPrice) + " Coins" + 
                ChatColor.of("#FFFFFF") + " for " + 
                ChatColor.of("#00FFFF") + tokensToReceive + " Skill Token" + (tokensToReceive > 1 ? "s" : ""));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
        
        player.closeInventory();
        quantities.remove(player.getUniqueId());
    }
    
    private int countItems(Player player, ShopItem shopItem) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (shopItem.matches(item)) {
                count += item.getAmount();
            }
        }
        return count;
    }
    
    private void removeItems(Player player, ShopItem shopItem, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (shopItem.matches(item)) {
                int itemAmount = item.getAmount();
                if (itemAmount <= remaining) {
                    player.getInventory().setItem(i, null);
                    remaining -= itemAmount;
                } else {
                    item.setAmount(itemAmount - remaining);
                    remaining = 0;
                }
                
                if (remaining <= 0) break;
            }
        }
    }
}
