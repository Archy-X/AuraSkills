package dev.aurelium.auraskills.bukkit.skillcoins.menu;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.skillcoins.CurrencyType;
import dev.aurelium.auraskills.common.skillcoins.EconomyProvider;
import dev.aurelium.auraskills.common.user.User;
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
 * Skill Level Purchase Menu - Buy skill levels with tokens
 * Clean implementation with proper event handling and no conflicts
 */
public class SkillLevelPurchaseMenu implements Listener {
    
    private static final String MENU_TITLE = "§b✪ §fSkill Level Purchase";
    private static final String SELECTION_TITLE = "§b✪ §fSelect Skill";
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    private static final int TOKENS_PER_LEVEL = 10; // Base cost per level
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    
    // Player-specific data storage
    private final Map<UUID, Skill> selectedSkills = new HashMap<>();
    private final Map<UUID, Integer> purchaseQuantities = new HashMap<>();
    
    public SkillLevelPurchaseMenu(AuraSkills plugin, EconomyProvider economy) {
        this.plugin = plugin;
        this.economy = economy;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Open the main skill level purchase menu
     */
    public void open(Player player) {
        openSkillSelection(player);
    }
    
    /**
     * Open skill selection menu
     */
    private void openSkillSelection(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, SELECTION_TITLE);
        
        UUID uuid = player.getUniqueId();
        double tokenBalance = economy.getBalance(uuid, CurrencyType.TOKENS);
        User user = plugin.getUser(player);
        
        if (user == null) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error loading your data!");
            return;
        }
        
        // Fill border with black glass
        fillBorder(inv);
        
        // Player info (slot 0)
        ItemStack playerInfo = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta playerMeta = playerInfo.getItemMeta();
        if (playerMeta != null) {
            playerMeta.setDisplayName(ChatColor.of("#00FFFF") + "✪ Skill Level Shop");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.of("#808080") + "Purchase additional skill levels");
            lore.add(ChatColor.of("#808080") + "using your Skill Tokens.");
            lore.add("");
            lore.add(ChatColor.of("#FFFF00") + "Select a skill below!");
            playerMeta.setLore(lore);
            playerInfo.setItemMeta(playerMeta);
        }
        inv.setItem(0, playerInfo);
        
        // Token balance (slot 8)
        ItemStack balanceItem = new ItemStack(Material.EMERALD);
        ItemMeta balanceMeta = balanceItem.getItemMeta();
        if (balanceMeta != null) {
            balanceMeta.setDisplayName(ChatColor.of("#00FFFF") + "Your Tokens");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.of("#00FFFF") + "Tokens: " + ChatColor.of("#FFFFFF") + 
                    MONEY_FORMAT.format(tokenBalance));
            lore.add("");
            lore.add(ChatColor.of("#808080") + "Cost: " + ChatColor.of("#00FFFF") + 
                    TOKENS_PER_LEVEL + " tokens per level");
            balanceMeta.setLore(lore);
            balanceItem.setItemMeta(balanceMeta);
        }
        inv.setItem(8, balanceItem);
        
        // Display all enabled skills (centered grid)
        int[] skillSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
        int slotIndex = 0;
        
        for (Skill skill : Skills.values()) {
            if (!skill.isEnabled() || slotIndex >= skillSlots.length) continue;
            
            int currentLevel = user.getSkillLevel(skill);
            int maxLevel = skill.getMaxLevel();
            
            // Skip if at max level
            if (currentLevel >= maxLevel) continue;
            
            Material icon = getSkillIcon(skill);
            ItemStack skillItem = new ItemStack(icon);
            ItemMeta meta = skillItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.of("#00FFFF") + skill.getDisplayName(user.getLocale()));
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.of("#808080") + "Current Level: " + ChatColor.of("#FFFFFF") + currentLevel);
                lore.add(ChatColor.of("#808080") + "Max Level: " + ChatColor.of("#FFFFFF") + maxLevel);
                lore.add(ChatColor.of("#808080") + "Available: " + ChatColor.of("#55FF55") + 
                        (maxLevel - currentLevel) + " levels");
                lore.add("");
                lore.add(ChatColor.of("#00FFFF") + "Cost: " + ChatColor.of("#FFFFFF") + 
                        TOKENS_PER_LEVEL + " tokens/level");
                lore.add("");
                lore.add(ChatColor.of("#FFFF00") + "▸ Click to purchase!");
                meta.setLore(lore);
                skillItem.setItemMeta(meta);
            }
            inv.setItem(skillSlots[slotIndex++], skillItem);
        }
        
        // Back button (slot 53)
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.of("#FF5555") + "← Back");
            List<String> backLore = new ArrayList<>();
            backLore.add("");
            backLore.add(ChatColor.of("#808080") + "Return to main shop");
            backMeta.setLore(backLore);
            back.setItemMeta(backMeta);
        }
        inv.setItem(53, back);
        
        player.openInventory(inv);
    }
    
    /**
     * Open purchase interface for specific skill
     */
    private void openPurchaseMenu(Player player, Skill skill) {
        selectedSkills.put(player.getUniqueId(), skill);
        purchaseQuantities.putIfAbsent(player.getUniqueId(), 1);
        updatePurchaseMenu(player, skill);
    }
    
    /**
     * Update the purchase menu with current quantity
     */
    private void updatePurchaseMenu(Player player, Skill skill) {
        Inventory inv = Bukkit.createInventory(null, 54, MENU_TITLE);
        
        UUID uuid = player.getUniqueId();
        int quantity = purchaseQuantities.getOrDefault(uuid, 1);
        double tokenBalance = economy.getBalance(uuid, CurrencyType.TOKENS);
        User user = plugin.getUser(player);
        
        if (user == null) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error loading your data!");
            return;
        }
        
        int currentLevel = user.getSkillLevel(skill);
        int maxLevel = skill.getMaxLevel();
        int availableLevels = maxLevel - currentLevel;
        int totalCost = TOKENS_PER_LEVEL * quantity;
        
        // Fill border
        fillBorder(inv);
        
        // Skill display (slot 13 - center top)
        Material icon = getSkillIcon(skill);
        ItemStack display = new ItemStack(icon, Math.min(quantity, 64));
        ItemMeta displayMeta = display.getItemMeta();
        if (displayMeta != null) {
            displayMeta.setDisplayName(ChatColor.of("#00FFFF") + skill.getDisplayName(user.getLocale()));
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.of("#808080") + "Current Level: " + ChatColor.of("#FFFFFF") + currentLevel);
            lore.add(ChatColor.of("#808080") + "Levels to Purchase: " + ChatColor.of("#FFFF00") + quantity);
            lore.add(ChatColor.of("#808080") + "New Level: " + ChatColor.of("#55FF55") + (currentLevel + quantity));
            lore.add("");
            lore.add(ChatColor.of("#00FFFF") + "Total Cost: " + ChatColor.of("#FFFFFF") + 
                    MONEY_FORMAT.format(totalCost) + " tokens");
            lore.add("");
            
            if (tokenBalance >= totalCost) {
                lore.add(ChatColor.of("#55FF55") + "✔ Sufficient Tokens");
                lore.add(ChatColor.of("#808080") + "  Balance after: " + ChatColor.of("#FFFFFF") + 
                        MONEY_FORMAT.format(tokenBalance - totalCost) + " tokens");
            } else {
                lore.add(ChatColor.of("#FF5555") + "✖ Insufficient Tokens");
                lore.add(ChatColor.of("#808080") + "  Need: " + ChatColor.of("#FF5555") + 
                        MONEY_FORMAT.format(totalCost - tokenBalance) + " more");
            }
            
            displayMeta.setLore(lore);
            display.setItemMeta(displayMeta);
        }
        inv.setItem(13, display);
        
        // Quantity controls (row 3, centered)
        // -10 (slot 20)
        createButton(inv, 20, Material.RED_TERRACOTTA, 
                ChatColor.of("#FF5555") + "▼▼ -10", 
                quantity > 10, 
                "Remove 10 levels");
        
        // -1 (slot 21)
        createButton(inv, 21, Material.ORANGE_TERRACOTTA, 
                ChatColor.of("#FF5555") + "▼ -1", 
                quantity > 1, 
                "Remove 1 level");
        
        // Quantity display (slot 22)
        ItemStack qtyDisplay = new ItemStack(Material.PAPER);
        ItemMeta qtyMeta = qtyDisplay.getItemMeta();
        if (qtyMeta != null) {
            qtyMeta.setDisplayName(ChatColor.of("#FFFF00") + "Amount: " + ChatColor.of("#FFFFFF") + quantity);
            List<String> qtyLore = new ArrayList<>();
            qtyLore.add("");
            qtyLore.add(ChatColor.of("#808080") + "Levels to purchase");
            qtyLore.add(ChatColor.of("#808080") + "Use +/- to adjust");
            qtyMeta.setLore(qtyLore);
            qtyDisplay.setItemMeta(qtyMeta);
        }
        inv.setItem(22, qtyDisplay);
        
        // +1 (slot 23)
        createButton(inv, 23, Material.LIME_TERRACOTTA, 
                ChatColor.of("#55FF55") + "▲ +1", 
                quantity < availableLevels, 
                "Add 1 level");
        
        // +10 (slot 24)
        createButton(inv, 24, Material.GREEN_TERRACOTTA, 
                ChatColor.of("#55FF55") + "▲▲ +10", 
                quantity + 10 <= availableLevels, 
                "Add 10 levels");
        
        // Quick select (slot 31)
        ItemStack quickSelect = new ItemStack(Material.NETHER_STAR);
        ItemMeta quickMeta = quickSelect.getItemMeta();
        if (quickMeta != null) {
            quickMeta.setDisplayName(ChatColor.of("#FFFF00") + "⚡ Quick Select");
            List<String> quickLore = new ArrayList<>();
            quickLore.add("");
            quickLore.add(ChatColor.of("#808080") + "Quickly set to:");
            quickLore.add(ChatColor.of("#FFD700") + "  • " + ChatColor.of("#FFFFFF") + "1, 5, 10, or MAX levels");
            quickLore.add("");
            quickLore.add(ChatColor.of("#FFFF00") + "▸ Click to open!");
            quickMeta.setLore(quickLore);
            quickSelect.setItemMeta(quickMeta);
        }
        inv.setItem(31, quickSelect);
        
        // Balance (slot 45)
        ItemStack balance = new ItemStack(Material.EMERALD);
        ItemMeta balanceMeta = balance.getItemMeta();
        if (balanceMeta != null) {
            balanceMeta.setDisplayName(ChatColor.of("#00FFFF") + "Your Balance");
            List<String> balanceLore = new ArrayList<>();
            balanceLore.add("");
            balanceLore.add(ChatColor.of("#00FFFF") + "Tokens: " + ChatColor.of("#FFFFFF") + 
                    MONEY_FORMAT.format(tokenBalance));
            balanceMeta.setLore(balanceLore);
            balance.setItemMeta(balanceMeta);
        }
        inv.setItem(45, balance);
        
        // Confirm button (slot 49)
        boolean canPurchase = tokenBalance >= totalCost && quantity <= availableLevels;
        ItemStack confirm = new ItemStack(canPurchase ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
        ItemMeta confirmMeta = confirm.getItemMeta();
        if (confirmMeta != null) {
            if (canPurchase) {
                confirmMeta.setDisplayName(ChatColor.of("#55FF55") + "✔ CONFIRM PURCHASE");
                List<String> confirmLore = new ArrayList<>();
                confirmLore.add("");
                confirmLore.add(ChatColor.of("#808080") + "Purchase " + ChatColor.of("#FFFF00") + quantity + 
                        ChatColor.of("#808080") + " level" + (quantity > 1 ? "s" : ""));
                confirmLore.add(ChatColor.of("#808080") + "Cost: " + ChatColor.of("#00FFFF") + 
                        MONEY_FORMAT.format(totalCost) + " tokens");
                confirmLore.add("");
                confirmLore.add(ChatColor.of("#55FF55") + "▸ Click to confirm!");
                confirmMeta.setLore(confirmLore);
            } else {
                confirmMeta.setDisplayName(ChatColor.of("#FF5555") + "✖ Cannot Purchase");
                List<String> confirmLore = new ArrayList<>();
                confirmLore.add("");
                if (tokenBalance < totalCost) {
                    confirmLore.add(ChatColor.of("#FF5555") + "Not enough tokens!");
                } else {
                    confirmLore.add(ChatColor.of("#FF5555") + "Exceeds available levels!");
                }
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
            backLore.add(ChatColor.of("#808080") + "Choose different skill");
            backMeta.setLore(backLore);
            back.setItemMeta(backMeta);
        }
        inv.setItem(53, back);
        
        player.openInventory(inv);
    }
    
    /**
     * Open quick select preset menu
     */
    private void openQuickSelect(Player player, Skill skill) {
        Inventory inv = Bukkit.createInventory(null, 27, "§b✪ §fQuick Select Levels");
        
        User user = plugin.getUser(player);
        if (user == null) return;
        
        int currentLevel = user.getSkillLevel(skill);
        int maxLevel = skill.getMaxLevel();
        int available = maxLevel - currentLevel;
        
        fillBorder(inv);
        
        // Preset buttons
        if (available >= 1) {
            createPresetButton(inv, 10, 1, "1 Level", Material.GOLD_NUGGET);
        }
        if (available >= 5) {
            createPresetButton(inv, 11, 5, "5 Levels", Material.GOLD_INGOT);
        }
        if (available >= 10) {
            createPresetButton(inv, 13, 10, "10 Levels", Material.EMERALD);
        }
        if (available >= 25) {
            createPresetButton(inv, 15, 25, "25 Levels", Material.EMERALD_BLOCK);
        }
        if (available > 0) {
            createPresetButton(inv, 16, available, "MAX (" + available + ")", Material.DIAMOND);
        }
        
        // Back button (slot 22)
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.of("#FFFF00") + "← Back");
            List<String> backLore = new ArrayList<>();
            backLore.add("");
            backLore.add(ChatColor.of("#808080") + "Return to purchase menu");
            backMeta.setLore(backLore);
            back.setItemMeta(backMeta);
        }
        inv.setItem(22, back);
        
        player.openInventory(inv);
    }
    
    /**
     * Event handler for all clicks
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        // Only handle our menus
        if (!title.equals(MENU_TITLE) && !title.equals(SELECTION_TITLE) && 
            !title.equals("§b✪ §fQuick Select Levels")) {
            return;
        }
        
        event.setCancelled(true);
        
        if (clicked == null || clicked.getType() == Material.AIR || 
            clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) {
            return;
        }
        
        int slot = event.getSlot();
        UUID uuid = player.getUniqueId();
        
        // Handle skill selection menu
        if (title.equals(SELECTION_TITLE)) {
            handleSelectionClick(player, slot, clicked);
        }
        // Handle purchase menu
        else if (title.equals(MENU_TITLE)) {
            handlePurchaseClick(player, slot);
        }
        // Handle quick select menu
        else if (title.contains("Quick Select")) {
            handleQuickSelectClick(player, slot);
        }
    }
    
    private void handleSelectionClick(Player player, int slot, ItemStack clicked) {
        // Back button
        if (slot == 53) {
            new ShopMainMenu(plugin, economy).open(player);
            return;
        }
        
        // Skill selection slots
        int[] validSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
        for (int validSlot : validSlots) {
            if (slot == validSlot) {
                // Find skill by item name
                ItemMeta meta = clicked.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    String displayName = ChatColor.stripColor(meta.getDisplayName());
                    Skill foundSkill = findSkillByDisplayName(player, displayName);
                    if (foundSkill != null) {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                        openPurchaseMenu(player, foundSkill);
                        return;
                    }
                }
            }
        }
    }
    
    private void handlePurchaseClick(Player player, int slot) {
        UUID uuid = player.getUniqueId();
        Skill skill = selectedSkills.get(uuid);
        if (skill == null) return;
        
        int quantity = purchaseQuantities.getOrDefault(uuid, 1);
        User user = plugin.getUser(player);
        if (user == null) return;
        
        int available = skill.getMaxLevel() - user.getSkillLevel(skill);
        
        // Quantity controls
        if (slot == 20 && quantity > 10) { // -10
            purchaseQuantities.put(uuid, quantity - 10);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.8f);
            updatePurchaseMenu(player, skill);
        } else if (slot == 21 && quantity > 1) { // -1
            purchaseQuantities.put(uuid, quantity - 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.9f);
            updatePurchaseMenu(player, skill);
        } else if (slot == 23 && quantity < available) { // +1
            purchaseQuantities.put(uuid, quantity + 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.1f);
            updatePurchaseMenu(player, skill);
        } else if (slot == 24 && quantity + 10 <= available) { // +10
            purchaseQuantities.put(uuid, Math.min(available, quantity + 10));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
            updatePurchaseMenu(player, skill);
        } else if (slot == 31) { // Quick select
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            openQuickSelect(player, skill);
        } else if (slot == 49) { // Confirm
            performPurchase(player, skill, quantity);
        } else if (slot == 53) { // Back
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            openSkillSelection(player);
        }
    }
    
    private void handleQuickSelectClick(Player player, int slot) {
        UUID uuid = player.getUniqueId();
        Skill skill = selectedSkills.get(uuid);
        if (skill == null) return;
        
        // Back button
        if (slot == 22) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            updatePurchaseMenu(player, skill);
            return;
        }
        
        // Preset buttons
        Map<Integer, Integer> presets = new HashMap<>();
        presets.put(10, 1);
        presets.put(11, 5);
        presets.put(13, 10);
        presets.put(15, 25);
        
        if (presets.containsKey(slot)) {
            purchaseQuantities.put(uuid, presets.get(slot));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            updatePurchaseMenu(player, skill);
        } else if (slot == 16) { // MAX button
            User user = plugin.getUser(player);
            if (user != null) {
                int available = skill.getMaxLevel() - user.getSkillLevel(skill);
                purchaseQuantities.put(uuid, available);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                updatePurchaseMenu(player, skill);
            }
        }
    }
    
    /**
     * Perform the actual purchase
     */
    private void performPurchase(Player player, Skill skill, int quantity) {
        UUID uuid = player.getUniqueId();
        User user = plugin.getUser(player);
        if (user == null) return;
        
        int totalCost = TOKENS_PER_LEVEL * quantity;
        double tokenBalance = economy.getBalance(uuid, CurrencyType.TOKENS);
        int currentLevel = user.getSkillLevel(skill);
        int maxLevel = skill.getMaxLevel();
        
        // Validation
        if (tokenBalance < totalCost) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Not enough tokens!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        if (currentLevel + quantity > maxLevel) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Would exceed max level!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Execute transaction
        economy.subtractBalance(uuid, CurrencyType.TOKENS, totalCost);
        user.setSkillLevel(skill, currentLevel + quantity);
        
        // Success messages
        player.sendMessage(ChatColor.of("#55FF55") + "✔ Purchase Successful!");
        player.sendMessage(ChatColor.of("#FFFFFF") + "Purchased " + ChatColor.of("#FFFF00") + quantity + 
                " level" + (quantity > 1 ? "s" : "") + ChatColor.of("#FFFFFF") + " in " + 
                ChatColor.of("#00FFFF") + skill.getDisplayName(user.getLocale()));
        player.sendMessage(ChatColor.of("#808080") + "New Level: " + ChatColor.of("#55FF55") + 
                (currentLevel + quantity) + ChatColor.of("#808080") + " / " + maxLevel);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
        
        // Cleanup and close
        cleanupPlayerData(uuid);
        player.closeInventory();
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(MENU_TITLE) || title.equals(SELECTION_TITLE) || title.contains("Quick Select")) {
            UUID uuid = event.getPlayer().getUniqueId();
            // Don't cleanup immediately - let navigation work
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (event.getPlayer().getOpenInventory().getTopInventory().getSize() == 0) {
                    cleanupPlayerData(uuid);
                }
            }, 2L);
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
            lore.add(ChatColor.of("#808080") + "Cost: " + ChatColor.of("#00FFFF") + 
                    MONEY_FORMAT.format(TOKENS_PER_LEVEL * amount) + " tokens");
            lore.add("");
            lore.add(ChatColor.of("#55FF55") + "▸ Click to select!");
            meta.setLore(lore);
            button.setItemMeta(meta);
        }
        inv.setItem(slot, button);
    }
    
    private Material getSkillIcon(Skill skill) {
        switch (skill.name().toUpperCase()) {
            case "FARMING": return Material.WHEAT;
            case "FORAGING": return Material.OAK_LOG;
            case "MINING": return Material.IRON_PICKAXE;
            case "FISHING": return Material.FISHING_ROD;
            case "EXCAVATION": return Material.IRON_SHOVEL;
            case "ARCHERY": return Material.BOW;
            case "FIGHTING": return Material.DIAMOND_SWORD;
            case "DEFENSE": return Material.SHIELD;
            case "AGILITY": return Material.FEATHER;
            case "ENDURANCE": return Material.LEATHER_BOOTS;
            case "ALCHEMY": return Material.BREWING_STAND;
            case "ENCHANTING": return Material.ENCHANTING_TABLE;
            case "SORCERY": return Material.BLAZE_POWDER;
            case "HEALING": return Material.GOLDEN_APPLE;
            case "FORGING": return Material.ANVIL;
            default: return Material.EXPERIENCE_BOTTLE;
        }
    }
    
    private Skill findSkillByDisplayName(Player player, String displayName) {
        User user = plugin.getUser(player);
        if (user == null) return null;
        
        for (Skill skill : Skills.values()) {
            if (skill.isEnabled() && skill.getDisplayName(user.getLocale()).equals(displayName)) {
                return skill;
            }
        }
        return null;
    }
    
    private void cleanupPlayerData(UUID uuid) {
        selectedSkills.remove(uuid);
        purchaseQuantities.remove(uuid);
    }
}
