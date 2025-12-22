package dev.aurelium.auraskills.bukkit.skillcoins.menu;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skillcoins.TokenRewardListener;
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
 * Level Buy Menu - Uses the same track layout as level progression
 * Allows clicking levels to select range, with highlighting
 * +/- buttons at top with confirm button
 */
public class LevelBuyMenu {
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    
    // The track positions (same as level_progression.yml)
    private static final int[] TRACK = {9, 18, 27, 36, 37, 38, 29, 20, 11, 12, 13, 22, 31, 40, 41, 42, 33, 24, 15, 16, 17, 26, 35, 44};
    private static final int ITEMS_PER_PAGE = 24;
    private static final int TOKENS_PER_LEVEL = 10;
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0");
    
    // Title prefix to identify this menu
    private static final String TITLE_PREFIX = ChatColor.of("#FFD700") + "✦ ";
    
    // Session data per player
    private final Map<UUID, Skill> selectedSkill = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> selectedUpToLevel = new ConcurrentHashMap<>();  // Level they want to upgrade TO
    private final Map<UUID, Integer> currentPage = new ConcurrentHashMap<>();
    
    public LevelBuyMenu(AuraSkills plugin, EconomyProvider economy) {
        if (plugin == null) throw new IllegalArgumentException("Plugin cannot be null");
        if (economy == null) throw new IllegalArgumentException("Economy cannot be null");
        this.plugin = plugin;
        this.economy = economy;
    }
    
    /**
     * Open the level buy menu for a specific skill
     */
    public void open(Player player, Skill skill) {
        if (player == null || skill == null) return;
        
        try {
            MenuManager manager = MenuManager.getInstance(plugin);
            if (manager != null) {
                manager.registerLevelBuyMenu(player, this);
            }
            
            UUID uuid = player.getUniqueId();
            selectedSkill.put(uuid, skill);
            currentPage.put(uuid, 0);
            
            User user = plugin.getUser(player);
            if (user == null) return;
            
            int currentLevel = user.getSkillLevel(skill);
            // Default selection is to buy 1 level
            selectedUpToLevel.put(uuid, currentLevel + 1);
            
            updateInventory(player);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error opening level buy menu", e);
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error opening menu!");
        }
    }
    
    /**
     * Open from a specific locked level click in /skill menu
     */
    public void openFromLevelClick(Player player, Skill skill, int clickedLevel) {
        if (player == null || skill == null) return;
        
        try {
            MenuManager manager = MenuManager.getInstance(plugin);
            if (manager != null) {
                manager.registerLevelBuyMenu(player, this);
            }
            
            UUID uuid = player.getUniqueId();
            selectedSkill.put(uuid, skill);
            
            User user = plugin.getUser(player);
            if (user == null) return;
            
            int currentLevel = user.getSkillLevel(skill);
            
            // Calculate which page this level is on
            int levelIndex = clickedLevel - 1; // 0-indexed
            int page = levelIndex / ITEMS_PER_PAGE;
            currentPage.put(uuid, page);
            
            // Set selection to the clicked level (or current+1 if below current)
            selectedUpToLevel.put(uuid, Math.max(currentLevel + 1, clickedLevel));
            
            updateInventory(player);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error opening level buy menu from click", e);
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error opening menu!");
        }
    }
    
    public boolean isMenuTitle(String title) {
        if (title == null) return false;
        return title.startsWith(TITLE_PREFIX);
    }
    
    private String getMenuTitle(Skill skill, int page) {
        return TITLE_PREFIX + ChatColor.WHITE + "Buy " + skill.getDisplayName(Locale.ENGLISH) + " Levels" + 
               ChatColor.GRAY + " [" + (page + 1) + "]";
    }
    
    private void updateInventory(Player player) {
        if (player == null || !player.isOnline()) return;
        
        try {
            UUID uuid = player.getUniqueId();
            Skill skill = selectedSkill.get(uuid);
            if (skill == null) return;
            
            User user = plugin.getUser(player);
            if (user == null) return;
            
            int page = currentPage.getOrDefault(uuid, 0);
            int currentLevel = user.getSkillLevel(skill);
            int maxLevel = skill.getMaxLevel();
            int selectedLevel = selectedUpToLevel.getOrDefault(uuid, currentLevel + 1);
            
            // Clamp selection
            selectedLevel = Math.max(currentLevel + 1, Math.min(maxLevel, selectedLevel));
            selectedUpToLevel.put(uuid, selectedLevel);
            
            String title = getMenuTitle(skill, page);
            
            Inventory inv = null;
            boolean isNewInventory = false;
            
            try {
                Inventory currentInv = player.getOpenInventory().getTopInventory();
                if (currentInv != null && currentInv.getSize() == 54 &&
                    player.getOpenInventory().getTitle().startsWith(TITLE_PREFIX)) {
                    inv = currentInv;
                }
            } catch (Exception e) {
                // Will create new inventory
            }
            
            if (inv == null) {
                inv = Bukkit.createInventory(null, 54, title);
                isNewInventory = true;
            }
            
            inv.clear();
            
            // Fill with dark glass
            fillBackground(inv);
            
            // Add top controls (replacing rank/sources/abilities)
            addTopControls(inv, player, skill, currentLevel, maxLevel, selectedLevel);
            
            // Add level track items
            addLevelTrack(inv, skill, currentLevel, maxLevel, selectedLevel, page);
            
            // Add navigation
            addNavigation(inv, page, maxLevel);
            
            if (isNewInventory) {
                player.openInventory(inv);
            } else {
                player.updateInventory();
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error updating level buy inventory", e);
        }
    }
    
    private void fillBackground(Inventory inv) {
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }
        
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }
    }
    
    private void addTopControls(Inventory inv, Player player, Skill skill, int currentLevel, int maxLevel, int selectedLevel) {
        UUID uuid = player.getUniqueId();
        double tokenBalance = economy.getBalance(uuid, CurrencyType.TOKENS);
        
        int levelsToBuy = selectedLevel - currentLevel;
        int totalCost = levelsToBuy * TOKENS_PER_LEVEL;
        boolean canAfford = tokenBalance >= totalCost;
        
        // Slot 0: Skill Info
        ItemStack skillItem = new ItemStack(getSkillIcon(skill));
        ItemMeta skillMeta = skillItem.getItemMeta();
        if (skillMeta != null) {
            skillMeta.setDisplayName(ChatColor.of("#00FFFF") + "✦ " + skill.getDisplayName(Locale.ENGLISH));
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GRAY + "Current Level: " + ChatColor.WHITE + currentLevel);
            lore.add(ChatColor.GRAY + "Max Level: " + ChatColor.WHITE + maxLevel);
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click levels below to select!");
            skillMeta.setLore(lore);
            skillItem.setItemMeta(skillMeta);
        }
        inv.setItem(0, skillItem);
        
        // Slot 1: Minus button (-10)
        createControlButton(inv, 1, Material.RED_STAINED_GLASS_PANE, 
                ChatColor.of("#FF5555") + "▼▼ -10 Levels",
                selectedLevel - 10 > currentLevel,
                Arrays.asList("", ChatColor.GRAY + "Remove 10 from selection"));
        
        // Slot 2: Minus button (-1)
        createControlButton(inv, 2, Material.ORANGE_STAINED_GLASS_PANE,
                ChatColor.of("#FF8800") + "▼ -1 Level",
                selectedLevel > currentLevel + 1,
                Arrays.asList("", ChatColor.GRAY + "Remove 1 from selection"));
        
        // Slot 4: Confirm/Purchase Button
        if (levelsToBuy > 0 && canAfford && currentLevel < maxLevel) {
            ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK);
            ItemMeta confirmMeta = confirm.getItemMeta();
            if (confirmMeta != null) {
                confirmMeta.setDisplayName(ChatColor.of("#55FF55") + "✔ CONFIRM PURCHASE");
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Levels: " + ChatColor.WHITE + currentLevel + " → " + 
                        ChatColor.GREEN + selectedLevel);
                lore.add(ChatColor.GRAY + "Buying: " + ChatColor.YELLOW + levelsToBuy + " level" + (levelsToBuy > 1 ? "s" : ""));
                lore.add("");
                lore.add(ChatColor.GOLD + "Cost: " + ChatColor.WHITE + MONEY_FORMAT.format(totalCost) + " Tokens");
                lore.add(ChatColor.GRAY + "Balance after: " + ChatColor.WHITE + 
                        MONEY_FORMAT.format(tokenBalance - totalCost));
                lore.add("");
                lore.add(ChatColor.GREEN + "▸ Click to purchase!");
                confirmMeta.setLore(lore);
                confirm.setItemMeta(confirmMeta);
            }
            inv.setItem(4, confirm);
        } else if (levelsToBuy > 0 && !canAfford) {
            ItemStack cantAfford = new ItemStack(Material.REDSTONE_BLOCK);
            ItemMeta cantMeta = cantAfford.getItemMeta();
            if (cantMeta != null) {
                cantMeta.setDisplayName(ChatColor.of("#FF5555") + "✖ INSUFFICIENT TOKENS");
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Cost: " + ChatColor.RED + MONEY_FORMAT.format(totalCost) + " Tokens");
                lore.add(ChatColor.GRAY + "You have: " + ChatColor.WHITE + MONEY_FORMAT.format(tokenBalance));
                lore.add(ChatColor.GRAY + "Need: " + ChatColor.RED + MONEY_FORMAT.format(totalCost - tokenBalance) + " more");
                cantMeta.setLore(lore);
                cantAfford.setItemMeta(cantMeta);
            }
            inv.setItem(4, cantAfford);
        } else if (currentLevel >= maxLevel) {
            ItemStack maxed = new ItemStack(Material.NETHER_STAR);
            ItemMeta maxedMeta = maxed.getItemMeta();
            if (maxedMeta != null) {
                maxedMeta.setDisplayName(ChatColor.of("#FFD700") + "★ MAX LEVEL REACHED");
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "This skill is at maximum level!");
                maxedMeta.setLore(lore);
                maxed.setItemMeta(maxedMeta);
            }
            inv.setItem(4, maxed);
        }
        
        // Slot 6: Plus button (+1)
        createControlButton(inv, 6, Material.LIME_STAINED_GLASS_PANE,
                ChatColor.of("#55FF55") + "▲ +1 Level",
                selectedLevel < maxLevel,
                Arrays.asList("", ChatColor.GRAY + "Add 1 to selection"));
        
        // Slot 7: Plus button (+10)
        createControlButton(inv, 7, Material.GREEN_STAINED_GLASS_PANE,
                ChatColor.of("#00AA00") + "▲▲ +10 Levels",
                selectedLevel + 10 <= maxLevel,
                Arrays.asList("", ChatColor.GRAY + "Add 10 to selection"));
        
        // Slot 8: Balance Display
        ItemStack balance = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta balanceMeta = balance.getItemMeta();
        if (balanceMeta != null) {
            balanceMeta.setDisplayName(ChatColor.of("#FFD700") + "⬥ Your Tokens");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.AQUA + "Tokens: " + ChatColor.WHITE + MONEY_FORMAT.format(tokenBalance));
            lore.add("");
            lore.add(ChatColor.GRAY + "Cost per level: " + ChatColor.YELLOW + TOKENS_PER_LEVEL);
            balanceMeta.setLore(lore);
            balance.setItemMeta(balanceMeta);
        }
        inv.setItem(8, balance);
    }
    
    private void createControlButton(Inventory inv, int slot, Material material, String name, boolean enabled, List<String> lore) {
        ItemStack button = new ItemStack(enabled ? material : Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = button.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(enabled ? name : ChatColor.GRAY + "[Limit Reached]");
            if (enabled) {
                meta.setLore(lore);
            }
            button.setItemMeta(meta);
        }
        inv.setItem(slot, button);
    }
    
    private void addLevelTrack(Inventory inv, Skill skill, int currentLevel, int maxLevel, int selectedLevel, int page) {
        int startLevel = (page * ITEMS_PER_PAGE) + 1;
        
        for (int i = 0; i < TRACK.length; i++) {
            int level = startLevel + i;
            if (level > maxLevel) break;
            
            int slot = TRACK[i];
            Material material;
            String displayName;
            List<String> lore = new ArrayList<>();
            
            if (level <= currentLevel) {
                // Already unlocked - green glass
                material = Material.LIME_STAINED_GLASS_PANE;
                displayName = ChatColor.GREEN + "Level " + level + " ✔";
                lore.add("");
                lore.add(ChatColor.GREEN + "Already Unlocked!");
            } else if (level <= selectedLevel) {
                // Selected for purchase - cyan/light blue glass (highlighted)
                material = Material.LIGHT_BLUE_STAINED_GLASS_PANE;
                displayName = ChatColor.of("#00FFFF") + "Level " + level + " ★";
                lore.add("");
                lore.add(ChatColor.AQUA + "SELECTED FOR PURCHASE");
                lore.add("");
                addRewardLore(lore, level);
                lore.add("");
                lore.add(ChatColor.YELLOW + "▸ Click to deselect this level");
            } else {
                // Not selected, can be purchased - red/orange glass
                material = Material.RED_STAINED_GLASS_PANE;
                displayName = ChatColor.RED + "Level " + level;
                lore.add("");
                lore.add(ChatColor.GRAY + "Locked");
                lore.add("");
                addRewardLore(lore, level);
                lore.add("");
                lore.add(ChatColor.GOLD + "Cost: " + ChatColor.WHITE + TOKENS_PER_LEVEL + " Tokens");
                lore.add("");
                lore.add(ChatColor.YELLOW + "▸ Click to select up to this level");
            }
            
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(displayName);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slot, item);
        }
    }
    
    private void addRewardLore(List<String> lore, int level) {
        int coinsReward = TokenRewardListener.getCoinsRewardForLevel(level);
        int tokenReward = TokenRewardListener.getTokenRewardForLevel(level);
        
        lore.add(ChatColor.GRAY + "Rewards:");
        lore.add(ChatColor.GOLD + "  ⬥ " + coinsReward + " SkillCoins");
        lore.add(ChatColor.AQUA + "  ⬥ " + tokenReward + " Token" + (tokenReward > 1 ? "s" : ""));
    }
    
    private void addNavigation(Inventory inv, int page, int maxLevel) {
        int maxPage = (maxLevel - 1) / ITEMS_PER_PAGE;
        
        // Back button (slot 45) - Arrow style like Quest plugin
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
        
        // Previous page (slot 48)
        if (page > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prev.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(ChatColor.GOLD + "« Previous Page");
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Go to page " + page);
                prevMeta.setLore(lore);
                prev.setItemMeta(prevMeta);
            }
            inv.setItem(48, prev);
        }
        
        // Page indicator (slot 49)
        ItemStack pageItem = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageItem.getItemMeta();
        if (pageMeta != null) {
            pageMeta.setDisplayName(ChatColor.YELLOW + "Page " + (page + 1) + " / " + (maxPage + 1));
            pageItem.setItemMeta(pageMeta);
        }
        inv.setItem(49, pageItem);
        
        // Next page (slot 50)
        if (page < maxPage) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = next.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(ChatColor.GOLD + "Next Page »");
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + "Go to page " + (page + 2));
                nextMeta.setLore(lore);
                next.setItemMeta(nextMeta);
            }
            inv.setItem(50, next);
        }
        
        // Close button (slot 53) - Barrier style
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(ChatColor.RED + "✖ Close");
            close.setItemMeta(closeMeta);
        }
        inv.setItem(53, close);
    }
    
    /**
     * Handle click events in the level buy menu
     */
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        
        Skill skill = selectedSkill.get(uuid);
        if (skill == null) return;
        
        User user = plugin.getUser(player);
        if (user == null) return;
        
        int slot = event.getRawSlot();
        int currentLevel = user.getSkillLevel(skill);
        int maxLevel = skill.getMaxLevel();
        int selectedLevel = selectedUpToLevel.getOrDefault(uuid, currentLevel + 1);
        int page = currentPage.getOrDefault(uuid, 0);
        
        // Check for control buttons
        switch (slot) {
            case 1: // -10 levels
                if (selectedLevel - 10 > currentLevel) {
                    selectedUpToLevel.put(uuid, selectedLevel - 10);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    updateInventory(player);
                }
                return;
            case 2: // -1 level
                if (selectedLevel > currentLevel + 1) {
                    selectedUpToLevel.put(uuid, selectedLevel - 1);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    updateInventory(player);
                }
                return;
            case 4: // Confirm purchase
                if (currentLevel < maxLevel) {
                    processPurchase(player, skill, currentLevel, selectedLevel);
                }
                return;
            case 6: // +1 level
                if (selectedLevel < maxLevel) {
                    selectedUpToLevel.put(uuid, selectedLevel + 1);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    updateInventory(player);
                }
                return;
            case 7: // +10 levels
                if (selectedLevel + 10 <= maxLevel) {
                    selectedUpToLevel.put(uuid, selectedLevel + 10);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    updateInventory(player);
                }
                return;
            case 45: // Back
                player.closeInventory();
                // Open shop main menu
                Bukkit.getScheduler().runTask(plugin, () -> {
                    ShopMainMenu shopMenu = new ShopMainMenu(plugin, economy);
                    shopMenu.open(player);
                });
                return;
            case 48: // Previous page
                if (page > 0) {
                    currentPage.put(uuid, page - 1);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    updateInventory(player);
                }
                return;
            case 50: // Next page
                int maxPage = (maxLevel - 1) / ITEMS_PER_PAGE;
                if (page < maxPage) {
                    currentPage.put(uuid, page + 1);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    updateInventory(player);
                }
                return;
            case 53: // Close
                player.closeInventory();
                return;
        }
        
        // Check if clicked on a level in the track
        int trackIndex = -1;
        for (int i = 0; i < TRACK.length; i++) {
            if (TRACK[i] == slot) {
                trackIndex = i;
                break;
            }
        }
        
        if (trackIndex != -1) {
            int clickedLevel = (page * ITEMS_PER_PAGE) + trackIndex + 1;
            
            if (clickedLevel <= currentLevel) {
                // Can't select already unlocked levels
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                return;
            }
            
            if (clickedLevel > maxLevel) return;
            
            // Toggle selection
            if (clickedLevel <= selectedLevel) {
                // Clicking on a selected level - deselect down to the clicked level minus 1
                // But at minimum keep 1 level selected
                int newSelection = clickedLevel - 1;
                if (newSelection < currentLevel + 1) {
                    newSelection = currentLevel + 1;
                }
                selectedUpToLevel.put(uuid, newSelection);
            } else {
                // Clicking on an unselected level - select up to that level
                selectedUpToLevel.put(uuid, clickedLevel);
            }
            
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
            updateInventory(player);
        }
    }
    
    private void processPurchase(Player player, Skill skill, int currentLevel, int selectedLevel) {
        UUID uuid = player.getUniqueId();
        
        int levelsToBuy = selectedLevel - currentLevel;
        int totalCost = levelsToBuy * TOKENS_PER_LEVEL;
        
        double balance = economy.getBalance(uuid, CurrencyType.TOKENS);
        
        if (balance < totalCost) {
            player.sendMessage(ChatColor.RED + "✖ Insufficient tokens!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Deduct tokens
        economy.subtractBalance(uuid, CurrencyType.TOKENS, totalCost);
        
        // Add levels
        User user = plugin.getUser(player);
        if (user == null) return;
        
        user.setSkillLevel(skill, selectedLevel);
        
        // Success feedback
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.0f);
        
        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "  ✔ " + ChatColor.WHITE + ChatColor.BOLD + "PURCHASE COMPLETE!");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "  " + skill.getDisplayName(Locale.ENGLISH) + ": " + 
                ChatColor.WHITE + currentLevel + ChatColor.GRAY + " → " + ChatColor.GREEN + selectedLevel);
        player.sendMessage(ChatColor.GRAY + "  Spent: " + ChatColor.YELLOW + MONEY_FORMAT.format(totalCost) + " Tokens");
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Reset selection and update
        selectedUpToLevel.put(uuid, selectedLevel + 1);
        updateInventory(player);
    }
    
    public void handleClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        selectedSkill.remove(uuid);
        selectedUpToLevel.remove(uuid);
        currentPage.remove(uuid);
        
        MenuManager manager = MenuManager.getInstance(plugin);
        if (manager != null) {
            manager.unregisterLevelBuyMenu(player);
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
}
