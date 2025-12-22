package dev.aurelium.auraskills.bukkit.skillcoins.menu;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skillcoins.shop.ShopSection;
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
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
    private static final String MENU_TITLE = ChatColor.AQUA + "❖ " + ChatColor.WHITE + "SkillCoins Shop";
    private static final String SKILL_SELECT_TITLE = ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Select Skill to Buy";
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    
    // Track players in skill selection mode
    private final Set<UUID> skillSelectionPlayers = new HashSet<>();
    
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
                player.sendMessage(ChatColor.RED + "✖ Error creating shop menu!");
                plugin.getLogger().severe("Failed to create inventory for player: " + player.getName());
                return;
            }
            
            // Open safely
            player.openInventory(inv);
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error opening main shop menu for " + player.getName(), e);
            player.sendMessage(ChatColor.RED + "✖ An error occurred opening the shop!");
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
            
            // Add Level Buy button at top (slot 4) - new feature
            addLevelBuyButton(inv);
            
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
                skullMeta.setDisplayName(ChatColor.AQUA + "SkillCoins Shop " + 
                        ChatColor.GOLD + "♦ " + player.getName());
                
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Welcome to the SkillCoins shop!");
                lore.add(ChatColor.GRAY + "Browse categories below to buy");
                lore.add(ChatColor.GRAY + "and sell items using your earnings.");
                lore.add("");
                lore.add(ChatColor.AQUA + "▸ Your balance is on the right →");
                
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
                balanceMeta.setDisplayName(ChatColor.GOLD + "⬥ Your Balance");
                
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.YELLOW + "SkillCoins: " + 
                        ChatColor.WHITE + MONEY_FORMAT.format(coins));
                lore.add(ChatColor.AQUA + "SkillTokens: " + 
                        ChatColor.WHITE + MONEY_FORMAT.format(tokens));
                lore.add("");
                lore.add(ChatColor.GRAY + "Earn more by leveling skills");
                lore.add(ChatColor.GRAY + "and completing objectives!");
                
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
                    
                    // Skip slot 4 as it's reserved for Level Buy button
                    if (slot == 4) continue;
                    
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
                            meta.setDisplayName(ChatColor.AQUA + section.getId());
                        }
                        
                        List<String> lore = new ArrayList<>();
                        lore.add("");
                        lore.add(ChatColor.GRAY + "Browse and purchase items");
                        lore.add(ChatColor.GRAY + "in this category");
                        lore.add("");
                        lore.add(ChatColor.GRAY + "Items: " + 
                                ChatColor.WHITE + section.getItemCount());
                        lore.add("");
                        lore.add(ChatColor.YELLOW + "▸ Click to open!");
                        
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
     * Add Level Buy button at slot 4 - allows purchasing skill levels with tokens
     */
    private void addLevelBuyButton(Inventory inv) {
        if (inv == null) return;
        
        try {
            ItemStack levelBuy = new ItemStack(Material.EXPERIENCE_BOTTLE);
            ItemMeta meta = levelBuy.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Buy Skill Levels");
                
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Purchase skill levels using");
                lore.add(ChatColor.GRAY + "your Skill Tokens!");
                lore.add("");
                lore.add(ChatColor.AQUA + "Cost: " + ChatColor.WHITE + "10 Tokens per level");
                lore.add("");
                lore.add(ChatColor.GRAY + "Select a skill and the levels");
                lore.add(ChatColor.GRAY + "you want to purchase.");
                lore.add("");
                lore.add(ChatColor.YELLOW + "▸ Click to open!");
                
                meta.setLore(lore);
                levelBuy.setItemMeta(meta);
            }
            
            inv.setItem(4, levelBuy);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding level buy button", e);
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
                closeMeta.setDisplayName(ChatColor.RED + "✖ Close");
                List<String> closeLore = new ArrayList<>();
                closeLore.add("");
                closeLore.add(ChatColor.GRAY + "Close this menu");
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
        return title.equals(MENU_TITLE) || title.equals(SKILL_SELECT_TITLE);
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
            String title = event.getView().getTitle();
            
            // Check if we're in skill selection mode
            if (title.equals(SKILL_SELECT_TITLE)) {
                handleSkillSelectionClick(player, slot, clicked);
                return;
            }
            
            // Handle close button
            if (slot == 53 && clicked.getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }
            
            // Handle Level Buy button (slot 4)
            if (slot == 4 && clicked.getType() == Material.EXPERIENCE_BOTTLE) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                openSkillSelectionForLevelBuy(player);
                return;
            }
            
            // Handle section clicks
            handleSectionClick(player, slot);
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error handling main menu click", e);
        }
    }
    
    /**
     * Handle clicks in skill selection mode
     */
    private void handleSkillSelectionClick(Player player, int slot, ItemStack clicked) {
        // Handle back button
        if (slot == 45 && clicked.getType() == Material.ARROW) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            skillSelectionPlayers.remove(player.getUniqueId());
            open(player);
            return;
        }
        
        // Handle close button
        if (slot == 53 && clicked.getType() == Material.BARRIER) {
            skillSelectionPlayers.remove(player.getUniqueId());
            player.closeInventory();
            return;
        }
        
        // Check if it's a skill slot
        int[] skillSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
        int skillIndex = -1;
        for (int i = 0; i < skillSlots.length; i++) {
            if (skillSlots[i] == slot) {
                skillIndex = i;
                break;
            }
        }
        
        if (skillIndex == -1) return;
        
        // Find the skill at this index
        User user = plugin.getUser(player);
        if (user == null) return;
        
        int currentIndex = 0;
        for (Skill skill : Skills.values()) {
            if (!skill.isEnabled()) continue;
            
            if (currentIndex == skillIndex) {
                int currentLevel = user.getSkillLevel(skill);
                int maxLevel = skill.getMaxLevel();
                
                if (currentLevel >= maxLevel) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                    player.sendMessage(ChatColor.RED + "This skill is already at maximum level!");
                    return;
                }
                
                // Open the new LevelBuyMenu for this skill
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                skillSelectionPlayers.remove(player.getUniqueId());
                
                LevelBuyMenu levelBuyMenu = new LevelBuyMenu(plugin, economy);
                levelBuyMenu.open(player, skill);
                return;
            }
            currentIndex++;
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
                player.sendMessage(ChatColor.RED + "✖ No shop sections available!");
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
                            player.sendMessage(ChatColor.RED + "✖ Error opening token exchange!");
                        }
                    } else if (sectionId.equalsIgnoreCase("SkillLevels")) {
                        try {
                            new SkillLevelPurchaseMenu(plugin, economy).open(player);
                        } catch (Exception e) {
                            plugin.getLogger().log(Level.SEVERE, "Error opening skill purchase", e);
                            player.sendMessage(ChatColor.RED + "✖ Error opening skill purchase!");
                        }
                    } else {
                        // Regular shop section
                        try {
                            new ShopSectionMenu(plugin, economy, section).open(player, 0);
                        } catch (Exception e) {
                            plugin.getLogger().log(Level.SEVERE, "Error opening section " + sectionId, e);
                            player.sendMessage(ChatColor.RED + "✖ Error opening shop section!");
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
     * Open skill selection for Level Buy menu
     */
    private void openSkillSelectionForLevelBuy(Player player) {
        if (player == null) return;
        
        try {
            String skillSelectTitle = ChatColor.GOLD + "✦ " + ChatColor.WHITE + "Select Skill to Buy";
            Inventory inv = Bukkit.createInventory(null, 54, skillSelectTitle);
            
            // Fill background
            ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta fillerMeta = filler.getItemMeta();
            if (fillerMeta != null) {
                fillerMeta.setDisplayName(" ");
                filler.setItemMeta(fillerMeta);
            }
            for (int i = 0; i < 54; i++) {
                inv.setItem(i, filler);
            }
            
            User user = plugin.getUser(player);
            if (user == null) return;
            
            // Add skills in a nice layout
            int[] skillSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
            int slotIndex = 0;
            
            for (Skill skill : Skills.values()) {
                if (!skill.isEnabled() || slotIndex >= skillSlots.length) continue;
                
                int currentLevel = user.getSkillLevel(skill);
                int maxLevel = skill.getMaxLevel();
                
                Material icon = getSkillIcon(skill);
                ItemStack skillItem = new ItemStack(icon);
                ItemMeta meta = skillItem.getItemMeta();
                
                if (meta != null) {
                    if (currentLevel >= maxLevel) {
                        meta.setDisplayName(ChatColor.GOLD + "★ " + skill.getDisplayName(Locale.ENGLISH) + ChatColor.GRAY + " [MAX]");
                        List<String> lore = new ArrayList<>();
                        lore.add("");
                        lore.add(ChatColor.GRAY + "Level: " + ChatColor.WHITE + currentLevel + "/" + maxLevel);
                        lore.add("");
                        lore.add(ChatColor.GREEN + "This skill is at maximum level!");
                        meta.setLore(lore);
                    } else {
                        meta.setDisplayName(ChatColor.AQUA + skill.getDisplayName(Locale.ENGLISH));
                        List<String> lore = new ArrayList<>();
                        lore.add("");
                        lore.add(ChatColor.GRAY + "Current Level: " + ChatColor.WHITE + currentLevel);
                        lore.add(ChatColor.GRAY + "Max Level: " + ChatColor.WHITE + maxLevel);
                        lore.add(ChatColor.GRAY + "Available: " + ChatColor.GREEN + (maxLevel - currentLevel) + " levels");
                        lore.add("");
                        lore.add(ChatColor.YELLOW + "▸ Click to purchase levels!");
                        meta.setLore(lore);
                    }
                    skillItem.setItemMeta(meta);
                }
                
                inv.setItem(skillSlots[slotIndex++], skillItem);
            }
            
            // Back button (slot 45)
            ItemStack back = new ItemStack(Material.ARROW);
            ItemMeta backMeta = back.getItemMeta();
            if (backMeta != null) {
                backMeta.setDisplayName(ChatColor.GREEN + "« Back");
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Return to shop");
                backMeta.setLore(lore);
                back.setItemMeta(backMeta);
            }
            inv.setItem(45, back);
            
            // Close button (slot 53)
            ItemStack close = new ItemStack(Material.BARRIER);
            ItemMeta closeMeta = close.getItemMeta();
            if (closeMeta != null) {
                closeMeta.setDisplayName(ChatColor.RED + "✖ Close");
                close.setItemMeta(closeMeta);
            }
            inv.setItem(53, close);
            
            // Store that we're in skill selection mode
            skillSelectionPlayers.add(player.getUniqueId());
            
            player.openInventory(inv);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error opening skill selection", e);
            player.sendMessage(ChatColor.RED + "✖ Error opening skill selection!");
        }
    }
    
    private Material getSkillIcon(Skill skill) {
        String skillName = skill.getId().getKey().toLowerCase();
        
        return switch (skillName) {
            case "farming" -> Material.IRON_HOE;
            case "foraging" -> Material.IRON_AXE;
            case "mining" -> Material.IRON_PICKAXE;
            case "fishing" -> Material.FISHING_ROD;
            case "excavation" -> Material.IRON_SHOVEL;
            case "archery" -> Material.BOW;
            case "defense" -> Material.CHAINMAIL_CHESTPLATE;
            case "fighting" -> Material.IRON_SWORD;
            case "endurance" -> Material.GOLDEN_APPLE;
            case "agility" -> Material.FEATHER;
            case "alchemy" -> Material.POTION;
            case "enchanting" -> Material.ENCHANTING_TABLE;
            case "sorcery" -> Material.BLAZE_ROD;
            case "healing" -> Material.SPLASH_POTION;
            case "forging" -> Material.ANVIL;
            default -> Material.EXPERIENCE_BOTTLE;
        };
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
