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
 * Skill Level Purchase Menu - Buy skill levels with tokens
 * 
 * Clean implementation with proper menu flow:
 * 1. Skill Selection -> Choose which skill to level up
 * 2. Purchase Menu -> Adjust quantity and confirm
 * 3. Quick Select -> Preset level amounts (1, 5, 10, 25, MAX)
 */
public class SkillLevelPurchaseMenu {
    
    private static final String MENU_TITLE = "§b✪ §fSkill Level Purchase";
    private static final String SELECTION_TITLE = "§b✪ §fSelect Skill";
    private static final String QUICK_SELECT_TITLE = "§b✪ §fQuick Select Levels";
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    private static final int TOKENS_PER_LEVEL = 10;
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    
    // Thread-safe player session data
    private final Map<UUID, Skill> selectedSkills = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> purchaseQuantities = new ConcurrentHashMap<>();
    
    public SkillLevelPurchaseMenu(AuraSkills plugin, EconomyProvider economy) {
        this.plugin = plugin;
        this.economy = economy;
    }
    
    /**
     * Open the skill selection menu
     */
    public void open(Player player) {
        MenuManager.getInstance(plugin).registerSkillMenu(player, this);
        openSkillSelection(player);
    }
    
    /**
     * Check if a title matches this menu
     */
    public boolean isMenuTitle(String title) {
        return title.equals(MENU_TITLE) || 
               title.equals(SELECTION_TITLE) || 
               title.equals(QUICK_SELECT_TITLE);
    }
    
    // ==================== MENU BUILDERS ====================
    
    /**
     * Open skill selection menu (first screen)
     */
    private void openSkillSelection(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, SELECTION_TITLE);
        User user = plugin.getUser(player);
        
        if (user == null) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error loading your data!");
            return;
        }
        
        UUID uuid = player.getUniqueId();
        double tokenBalance = economy.getBalance(uuid, CurrencyType.TOKENS);
        
        fillBorder(inv);
        
        // Info display (slot 0)
        ItemStack info = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.of("#00FFFF") + "✪ Skill Level Shop");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.of("#808080") + "Purchase skill levels");
            lore.add(ChatColor.of("#808080") + "using Skill Tokens");
            lore.add("");
            lore.add(ChatColor.of("#FFFF00") + "Select a skill below!");
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inv.setItem(0, info);
        
        // Token balance (slot 8)
        ItemStack balance = new ItemStack(Material.EMERALD);
        ItemMeta balanceMeta = balance.getItemMeta();
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
            balance.setItemMeta(balanceMeta);
        }
        inv.setItem(8, balance);
        
        // Display enabled skills (centered grid)
        int[] skillSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
        int slotIndex = 0;
        
        for (Skill skill : Skills.values()) {
            if (!skill.isEnabled() || slotIndex >= skillSlots.length) continue;
            
            int currentLevel = user.getSkillLevel(skill);
            int maxLevel = skill.getMaxLevel();
            
            // Skip skills already at max level
            if (currentLevel >= maxLevel) continue;
            
            Material icon = getSkillIcon(skill);
            ItemStack skillItem = new ItemStack(icon);
            ItemMeta meta = skillItem.getItemMeta();
            
            if (meta != null) {
                meta.setDisplayName(ChatColor.of("#00FFFF") + skill.getDisplayName(user.getLocale()));
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.of("#808080") + "Current: " + ChatColor.of("#FFFFFF") + 
                        currentLevel + ChatColor.of("#808080") + " / " + maxLevel);
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
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.of("#808080") + "Return to main shop");
            backMeta.setLore(lore);
            back.setItemMeta(backMeta);
        }
        inv.setItem(53, back);
        
        player.openInventory(inv);
    }
    
    /**
     * Open purchase menu for a specific skill
     */
    private void openPurchaseMenu(Player player, Skill skill) {
        plugin.getLogger().info("[SkillMenu] openPurchaseMenu called for skill: " + skill.name());
        
        selectedSkills.put(player.getUniqueId(), skill);
        purchaseQuantities.putIfAbsent(player.getUniqueId(), 1);
        
        plugin.getLogger().info("[SkillMenu] Creating inventory...");
        Inventory inv = Bukkit.createInventory(null, 54, MENU_TITLE);
        
        plugin.getLogger().info("[SkillMenu] Updating menu content...");
        updatePurchaseMenuContent(inv, player, skill);
        
        plugin.getLogger().info("[SkillMenu] Opening inventory for player...");
        player.openInventory(inv);
        
        plugin.getLogger().info("[SkillMenu] Purchase menu opened successfully");
    }
    
    /**
     * Update purchase menu content (used for quantity changes)
     */
    private void updatePurchaseMenuContent(Inventory inv, Player player, Skill skill) {
        plugin.getLogger().info("[SkillMenu] updatePurchaseMenuContent started");
        
        User user = plugin.getUser(player);
        if (user == null) {
            plugin.getLogger().warning("[SkillMenu] User is null! Cannot update menu content");
            return;
        }
        
        plugin.getLogger().info("[SkillMenu] User found, getting data...");
        
        UUID uuid = player.getUniqueId();
        int quantity = purchaseQuantities.getOrDefault(uuid, 1);
        double tokenBalance = economy.getBalance(uuid, CurrencyType.TOKENS);
        
        int currentLevel = user.getSkillLevel(skill);
        int maxLevel = skill.getMaxLevel();
        int availableLevels = maxLevel - currentLevel;
        int totalCost = TOKENS_PER_LEVEL * quantity;
        
        // Clear and rebuild
        inv.clear();
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
        createButton(inv, 20, Material.RED_TERRACOTTA, 
                ChatColor.of("#FF5555") + "▼▼ -10", 
                quantity > 10, 
                "Remove 10 levels");
        
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
        
        createButton(inv, 23, Material.LIME_TERRACOTTA, 
                ChatColor.of("#55FF55") + "▲ +1", 
                quantity < availableLevels, 
                "Add 1 level");
        
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
            quickLore.add(ChatColor.of("#FFD700") + "  • " + ChatColor.of("#FFFFFF") + "1, 5, 10, 25, or MAX levels");
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
                    confirmLore.add(ChatColor.of("#FF5555") + "Invalid quantity!");
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
    }
    
    /**
     * Open quick select preset menu
     */
    private void openQuickSelect(Player player, Skill skill) {
        User user = plugin.getUser(player);
        if (user == null) return;
        
        int currentLevel = user.getSkillLevel(skill);
        int maxLevel = skill.getMaxLevel();
        int available = maxLevel - currentLevel;
        
        Inventory inv = Bukkit.createInventory(null, 27, QUICK_SELECT_TITLE);
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
    
    // ==================== EVENT HANDLERS ====================
    
    /**
     * Handle click events (called by MenuManager)
     */
    public void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        
        // CRITICAL: Cancel ALL clicks IMMEDIATELY to prevent item pickup
        event.setCancelled(true);
        
        plugin.getLogger().info("[SkillMenu] Click event - Player: " + player.getName() + 
                ", Slot: " + event.getSlot() + 
                ", Title: " + event.getView().getTitle() +
                ", Cancelled: " + event.isCancelled());
        
        String title = event.getView().getTitle();
        ItemStack clicked = event.getCurrentItem();
        int slot = event.getSlot();
        
        // Ignore null, air, and border items
        if (clicked == null || clicked.getType() == Material.AIR || 
            clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) {
            plugin.getLogger().info("[SkillMenu] Ignoring click - null/air/border");
            return;
        }
        
        plugin.getLogger().info("[SkillMenu] Processing click - Material: " + clicked.getType());
        
        // Route to correct handler
        if (title.equals(SELECTION_TITLE)) {
            plugin.getLogger().info("[SkillMenu] Routing to selection handler");
            handleSelectionClick(player, slot, clicked);
        } else if (title.equals(MENU_TITLE)) {
            plugin.getLogger().info("[SkillMenu] Routing to purchase handler");
            handlePurchaseClick(player, slot);
        } else if (title.equals(QUICK_SELECT_TITLE)) {
            plugin.getLogger().info("[SkillMenu] Routing to quick select handler");
            handleQuickSelectClick(player, slot);
        } else {
            plugin.getLogger().warning("[SkillMenu] Unknown title: " + title);
        }
    }
    
    /**
     * Handle skill selection menu clicks
     */
    private void handleSelectionClick(Player player, int slot, ItemStack clicked) {
        plugin.getLogger().info("[SkillMenu] handleSelectionClick - Slot: " + slot);
        
        // Back button
        if (slot == 53) {
            plugin.getLogger().info("[SkillMenu] Back button clicked");
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            new ShopMainMenu(plugin, economy).open(player);
            return;
        }
        
        // Skill selection slots
        int[] validSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
        for (int validSlot : validSlots) {
            if (slot == validSlot) {
                plugin.getLogger().info("[SkillMenu] Valid slot matched: " + validSlot);
                ItemMeta meta = clicked.getItemMeta();
                plugin.getLogger().info("[SkillMenu] ItemMeta: " + (meta != null ? "exists" : "null"));
                
                if (meta != null && meta.hasDisplayName()) {
                    String displayName = ChatColor.stripColor(meta.getDisplayName());
                    plugin.getLogger().info("[SkillMenu] Display name: " + displayName);
                    
                    Skill foundSkill = findSkillByDisplayName(player, displayName);
                    plugin.getLogger().info("[SkillMenu] Found skill: " + (foundSkill != null ? foundSkill.name() : "null"));
                    
                    if (foundSkill != null) {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                        plugin.getLogger().info("[SkillMenu] Opening purchase menu for: " + foundSkill.name());
                        openPurchaseMenu(player, foundSkill);
                        return;
                    } else {
                        plugin.getLogger().warning("[SkillMenu] Could not find skill for display name: " + displayName);
                    }
                } else {
                    plugin.getLogger().warning("[SkillMenu] ItemMeta is null or has no display name");
                }
            }
        }
        plugin.getLogger().warning("[SkillMenu] No valid slot matched for slot: " + slot);
    }
    
    /**
     * Handle purchase menu clicks
     */
    private void handlePurchaseClick(Player player, int slot) {
        UUID uuid = player.getUniqueId();
        Skill skill = selectedSkills.get(uuid);
        if (skill == null) return;
        
        User user = plugin.getUser(player);
        if (user == null) return;
        
        int quantity = purchaseQuantities.getOrDefault(uuid, 1);
        int available = skill.getMaxLevel() - user.getSkillLevel(skill);
        
        // Quantity controls
        if (slot == 20 && quantity > 10) { // -10
            purchaseQuantities.put(uuid, quantity - 10);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.8f);
            refreshPurchaseMenu(player, skill);
        } else if (slot == 21 && quantity > 1) { // -1
            purchaseQuantities.put(uuid, quantity - 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.9f);
            refreshPurchaseMenu(player, skill);
        } else if (slot == 23 && quantity < available) { // +1
            purchaseQuantities.put(uuid, quantity + 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.1f);
            refreshPurchaseMenu(player, skill);
        } else if (slot == 24 && quantity + 10 <= available) { // +10
            purchaseQuantities.put(uuid, Math.min(available, quantity + 10));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
            refreshPurchaseMenu(player, skill);
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
    
    /**
     * Handle quick select menu clicks
     */
    private void handleQuickSelectClick(Player player, int slot) {
        UUID uuid = player.getUniqueId();
        Skill skill = selectedSkills.get(uuid);
        if (skill == null) return;
        
        // Back button
        if (slot == 22) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            openPurchaseMenu(player, skill);
            return;
        }
        
        User user = plugin.getUser(player);
        if (user == null) return;
        
        int available = skill.getMaxLevel() - user.getSkillLevel(skill);
        
        // Preset buttons
        Map<Integer, Integer> presets = new HashMap<>();
        presets.put(10, 1);
        presets.put(11, 5);
        presets.put(13, 10);
        presets.put(15, 25);
        presets.put(16, available); // MAX
        
        if (presets.containsKey(slot)) {
            purchaseQuantities.put(uuid, presets.get(slot));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            openPurchaseMenu(player, skill);
        }
    }
    
    /**
     * Refresh purchase menu (update existing inventory)
     */
    private void refreshPurchaseMenu(Player player, Skill skill) {
        Inventory current = player.getOpenInventory().getTopInventory();
        if (current != null && player.getOpenInventory().getTitle().equals(MENU_TITLE)) {
            updatePurchaseMenuContent(current, player, skill);
            player.updateInventory();
        }
    }
    
    /**
     * Perform the actual purchase
     */
    private void performPurchase(Player player, Skill skill, int quantity) {
        UUID uuid = player.getUniqueId();
        User user = plugin.getUser(player);
        
        if (user == null) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error loading your data!");
            return;
        }
        
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
        try {
            economy.subtractBalance(uuid, CurrencyType.TOKENS, totalCost);
            user.setSkillLevel(skill, currentLevel + quantity);
            
            player.sendMessage(ChatColor.of("#55FF55") + "✔ Purchase Successful!");
            player.sendMessage(ChatColor.of("#FFFFFF") + "Purchased " + ChatColor.of("#FFFF00") + quantity + 
                    " level" + (quantity > 1 ? "s" : "") + ChatColor.of("#FFFFFF") + " in " + 
                    ChatColor.of("#00FFFF") + skill.getDisplayName(user.getLocale()));
            player.sendMessage(ChatColor.of("#808080") + "New Level: " + ChatColor.of("#55FF55") + 
                    (currentLevel + quantity) + ChatColor.of("#808080") + " / " + maxLevel);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
            
            cleanupPlayerData(uuid);
            player.closeInventory();
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Purchase failed for " + player.getName(), e);
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Purchase failed!");
        }
    }
    
    /**
     * Handle close events (called by MenuManager)
     */
    public void handleClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        UUID uuid = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (event.getPlayer().getOpenInventory().getTopInventory().getSize() == 0) {
                cleanupPlayerData(uuid);
            }
        }, 2L);
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Fill inventory border with glass panes
     */
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
    
    /**
     * Create a button with enable/disable state
     */
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
    
    /**
     * Create a preset button for quick select
     */
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
    
    /**
     * Get icon material for a skill
     */
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
    
    /**
     * Find skill by display name
     */
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
    
    /**
     * Cleanup player session data
     */
    private void cleanupPlayerData(UUID uuid) {
        selectedSkills.remove(uuid);
        purchaseQuantities.remove(uuid);
    }
}
