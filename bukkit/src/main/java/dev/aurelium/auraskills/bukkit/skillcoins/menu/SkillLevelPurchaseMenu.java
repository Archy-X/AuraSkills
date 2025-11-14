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
 * Follows EXACT same structure as TransactionMenu for consistency
 */
public class SkillLevelPurchaseMenu {
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    
    private static final String SELECTION_TITLE = ChatColor.of("#00FFFF") + "✪ " + ChatColor.of("#FFFFFF") + "Select Skill";
    private static final String QUICK_SELECT_TITLE = ChatColor.of("#FFFF00") + "⚡ " + ChatColor.of("#FFFFFF") + "Quick Select";
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    private static final int TOKENS_PER_LEVEL = 10;
    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 64;
    
    // Thread-safe player session data
    private final Map<UUID, Skill> selectedSkills = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> quantities = new ConcurrentHashMap<>();
    private final Map<UUID, String> menuTitles = new ConcurrentHashMap<>();
    
    public SkillLevelPurchaseMenu(AuraSkills plugin, EconomyProvider economy) {
        if (plugin == null) throw new IllegalArgumentException("Plugin cannot be null");
        if (economy == null) throw new IllegalArgumentException("Economy cannot be null");
        this.plugin = plugin;
        this.economy = economy;
    }
    
    public void open(Player player) {
        if (player == null) {
            plugin.getLogger().warning("Attempted to open skill menu for null player");
            return;
        }
        
        if (!player.isOnline()) {
            plugin.getLogger().warning("Attempted to open skill menu for offline player: " + player.getName());
            return;
        }
        
        try {
            MenuManager manager = MenuManager.getInstance(plugin);
            if (manager != null) {
                manager.registerSkillMenu(player, this);
            }
            
            openSkillSelection(player);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error opening skill menu for " + player.getName(), e);
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error opening skill menu!");
        }
    }
    
    public boolean isMenuTitle(String title) {
        if (title == null) return false;
        if (title.equals(SELECTION_TITLE)) return true;
        if (title.equals(QUICK_SELECT_TITLE)) return true;
        return title.startsWith(ChatColor.of("#55FF55") + "Buy: ");
    }
    
    private void openSkillSelection(Player player) {
        if (player == null) return;
        
        try {
            User user = plugin.getUser(player);
            if (user == null) {
                player.sendMessage(ChatColor.of("#FF5555") + "✖ Error loading your data!");
                return;
            }
            
            Inventory inv = Bukkit.createInventory(null, 54, SELECTION_TITLE);
            if (inv == null) return;
            
            UUID uuid = player.getUniqueId();
            double tokenBalance = economy.getBalance(uuid, CurrencyType.TOKENS);
            
            fillBorder(inv);
            
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
            
            int[] skillSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
            int slotIndex = 0;
            
            for (Skill skill : Skills.values()) {
                if (!skill.isEnabled() || slotIndex >= skillSlots.length) continue;
                
                int currentLevel = user.getSkillLevel(skill);
                int maxLevel = skill.getMaxLevel();
                
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
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error opening skill selection", e);
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error opening menu!");
        }
    }
    
    private void openPurchaseMenu(Player player, Skill skill) {
        if (player == null || skill == null) return;
        
        try {
            UUID uuid = player.getUniqueId();
            selectedSkills.put(uuid, skill);
            quantities.putIfAbsent(uuid, MIN_QUANTITY);
            
            User user = plugin.getUser(player);
            if (user == null) return;
            
            String skillName = skill.getDisplayName(user.getLocale());
            if (skillName.length() > 15) {
                skillName = skillName.substring(0, 12) + "...";
            }
            
            String menuTitle = ChatColor.of("#55FF55") + "Buy: " + ChatColor.of("#FFFFFF") + skillName;
            menuTitles.put(uuid, menuTitle);
            
            updatePurchaseInventory(player);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error opening purchase menu", e);
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Error opening purchase menu!");
        }
    }
    
    private void updatePurchaseInventory(Player player) {
        if (player == null || !player.isOnline()) return;
        
        try {
            UUID uuid = player.getUniqueId();
            Skill skill = selectedSkills.get(uuid);
            if (skill == null) return;
            
            String menuTitle = menuTitles.get(uuid);
            if (menuTitle == null) return;
            
            User user = plugin.getUser(player);
            if (user == null) return;
            
            Inventory inv = null;
            boolean isNewInventory = false;
            
            try {
                Inventory currentInv = player.getOpenInventory().getTopInventory();
                if (currentInv != null && currentInv.getSize() == 54 && 
                    player.getOpenInventory().getTitle().equals(menuTitle)) {
                    inv = currentInv;
                }
            } catch (Exception e) {
                // Will create new inventory
            }
            
            if (inv == null) {
                inv = Bukkit.createInventory(null, 54, menuTitle);
                isNewInventory = true;
                if (inv == null) return;
            }
            
            int quantity = quantities.getOrDefault(uuid, MIN_QUANTITY);
            int currentLevel = user.getSkillLevel(skill);
            int maxLevel = skill.getMaxLevel();
            int availableLevels = maxLevel - currentLevel;
            
            quantity = Math.max(MIN_QUANTITY, Math.min(availableLevels, Math.min(MAX_QUANTITY, quantity)));
            quantities.put(uuid, quantity);
            
            double tokenBalance = economy.getBalance(uuid, CurrencyType.TOKENS);
            int totalCost = TOKENS_PER_LEVEL * quantity;
            
            inv.clear();
            fillBorder(inv);
            
            addSkillDisplay(inv, skill, quantity, currentLevel, maxLevel, tokenBalance, totalCost, user);
            addQuantityControls(inv, quantity, availableLevels);
            addBalanceDisplay(inv, tokenBalance);
            addConfirmButton(inv, quantity, totalCost, tokenBalance, availableLevels);
            addBackButton(inv);
            
            if (isNewInventory) {
                player.openInventory(inv);
            } else {
                player.updateInventory();
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error updating purchase inventory", e);
        }
    }
    
    private void addSkillDisplay(Inventory inv, Skill skill, int quantity, int currentLevel, 
                                 int maxLevel, double tokenBalance, int totalCost, User user) {
        if (inv == null) return;
        
        try {
            Material icon = getSkillIcon(skill);
            ItemStack display = new ItemStack(icon, Math.min(quantity, 64));
            ItemMeta meta = display.getItemMeta();
            
            if (meta != null) {
                meta.setDisplayName(ChatColor.of("#00FFFF") + skill.getDisplayName(user.getLocale()));
                
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.of("#808080") + "Current Level: " + ChatColor.of("#FFFFFF") + currentLevel);
                lore.add(ChatColor.of("#808080") + "Purchasing: " + ChatColor.of("#FFFF00") + quantity + 
                        ChatColor.of("#808080") + " level" + (quantity > 1 ? "s" : ""));
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
                
                meta.setLore(lore);
                display.setItemMeta(meta);
            }
            
            inv.setItem(13, display);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding skill display", e);
        }
    }
    
    private void addQuantityControls(Inventory inv, int quantity, int availableLevels) {
        if (inv == null) return;
        
        try {
            createQuantityButton(inv, 19, Material.RED_TERRACOTTA, 
                    ChatColor.of("#FF5555") + "▼▼ -10", 
                    quantity > 10, 
                    ChatColor.of("#808080") + "Remove 10 from quantity");
            
            createQuantityButton(inv, 20, Material.ORANGE_TERRACOTTA, 
                    ChatColor.of("#FF5555") + "▼ -1", 
                    quantity > MIN_QUANTITY, 
                    ChatColor.of("#808080") + "Remove 1 from quantity");
            
            ItemStack qtyDisplay = new ItemStack(Material.PAPER, Math.min(quantity, 64));
            ItemMeta qtyMeta = qtyDisplay.getItemMeta();
            if (qtyMeta != null) {
                qtyMeta.setDisplayName(ChatColor.of("#FFFF00") + "Quantity: " + ChatColor.of("#FFFFFF") + quantity);
                List<String> qtyLore = new ArrayList<>();
                qtyLore.add("");
                qtyLore.add(ChatColor.of("#808080") + "Levels to purchase");
                qtyLore.add(ChatColor.of("#808080") + "Use the buttons to adjust");
                qtyMeta.setLore(qtyLore);
                qtyDisplay.setItemMeta(qtyMeta);
            }
            inv.setItem(22, qtyDisplay);
            
            createQuantityButton(inv, 24, Material.LIME_TERRACOTTA, 
                    ChatColor.of("#55FF55") + "▲ +1", 
                    quantity < availableLevels && quantity < MAX_QUANTITY, 
                    ChatColor.of("#808080") + "Add 1 to quantity");
            
            createQuantityButton(inv, 25, Material.GREEN_TERRACOTTA, 
                    ChatColor.of("#55FF55") + "▲▲ +10", 
                    quantity + 10 <= availableLevels && quantity + 10 <= MAX_QUANTITY, 
                    ChatColor.of("#808080") + "Add 10 to quantity");
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding quantity controls", e);
        }
    }
    
    private void createQuantityButton(Inventory inv, int slot, Material material, String name, 
                                     boolean enabled, String description) {
        if (inv == null) return;
        
        try {
            ItemStack button = new ItemStack(enabled ? material : Material.GRAY_TERRACOTTA);
            ItemMeta meta = button.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(enabled ? name : ChatColor.of("#808080") + "Cannot adjust");
                if (enabled) {
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add(description);
                    meta.setLore(lore);
                }
                button.setItemMeta(meta);
            }
            inv.setItem(slot, button);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error creating quantity button", e);
        }
    }
    
    private void addBalanceDisplay(Inventory inv, double tokenBalance) {
        if (inv == null) return;
        
        try {
            ItemStack balance = new ItemStack(Material.EMERALD);
            ItemMeta meta = balance.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.of("#00FFFF") + "Your Balance");
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.of("#00FFFF") + "Tokens: " + ChatColor.of("#FFFFFF") + 
                        MONEY_FORMAT.format(tokenBalance));
                meta.setLore(lore);
                balance.setItemMeta(meta);
            }
            inv.setItem(45, balance);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding balance display", e);
        }
    }
    
    private void addConfirmButton(Inventory inv, int quantity, int totalCost, 
                                  double tokenBalance, int availableLevels) {
        if (inv == null) return;
        
        try {
            boolean canPurchase = tokenBalance >= totalCost && quantity <= availableLevels;
            ItemStack confirm = new ItemStack(canPurchase ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
            ItemMeta meta = confirm.getItemMeta();
            
            if (meta != null) {
                if (canPurchase) {
                    meta.setDisplayName(ChatColor.of("#55FF55") + "✔ CONFIRM PURCHASE");
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add(ChatColor.of("#808080") + "Purchase " + ChatColor.of("#FFFF00") + quantity + 
                            ChatColor.of("#808080") + " level" + (quantity > 1 ? "s" : ""));
                    lore.add(ChatColor.of("#808080") + "Cost: " + ChatColor.of("#00FFFF") + 
                            MONEY_FORMAT.format(totalCost) + " tokens");
                    lore.add("");
                    lore.add(ChatColor.of("#55FF55") + "▸ Click to confirm!");
                    meta.setLore(lore);
                } else {
                    meta.setDisplayName(ChatColor.of("#FF5555") + "✖ Cannot Purchase");
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    if (tokenBalance < totalCost) {
                        lore.add(ChatColor.of("#FF5555") + "Not enough tokens!");
                    } else {
                        lore.add(ChatColor.of("#FF5555") + "Invalid quantity!");
                    }
                    meta.setLore(lore);
                }
                confirm.setItemMeta(meta);
            }
            
            inv.setItem(49, confirm);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding confirm button", e);
        }
    }
    
    private void addBackButton(Inventory inv) {
        if (inv == null) return;
        
        try {
            ItemStack back = new ItemStack(Material.ARROW);
            ItemMeta meta = back.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.of("#FFFF00") + "← Back / Quick Select");
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.of("#808080") + "Left Click: " + ChatColor.of("#FFFFFF") + "Return to skill list");
                lore.add(ChatColor.of("#808080") + "Right Click: " + ChatColor.of("#FFFFFF") + "Quick select amount");
                meta.setLore(lore);
                back.setItemMeta(meta);
            }
            inv.setItem(53, back);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error adding back button", e);
        }
    }
    
    private void openQuickSelect(Player player) {
        if (player == null) return;
        
        try {
            UUID uuid = player.getUniqueId();
            Skill skill = selectedSkills.get(uuid);
            if (skill == null) return;
            
            User user = plugin.getUser(player);
            if (user == null) return;
            
            int available = skill.getMaxLevel() - user.getSkillLevel(skill);
            
            Inventory inv = Bukkit.createInventory(null, 27, QUICK_SELECT_TITLE);
            fillBorder(inv);
            
            if (available >= 1) createPresetButton(inv, 10, 1, "1 Level", Material.GOLD_NUGGET);
            if (available >= 5) createPresetButton(inv, 11, 5, "5 Levels", Material.GOLD_INGOT);
            if (available >= 10) createPresetButton(inv, 13, 10, "10 Levels", Material.EMERALD);
            if (available >= 25) createPresetButton(inv, 15, 25, "25 Levels", Material.EMERALD_BLOCK);
            if (available > 0) createPresetButton(inv, 16, available, "MAX (" + available + ")", Material.DIAMOND);
            
            ItemStack back = new ItemStack(Material.ARROW);
            ItemMeta backMeta = back.getItemMeta();
            if (backMeta != null) {
                backMeta.setDisplayName(ChatColor.of("#FFFF00") + "← Back");
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.of("#808080") + "Return to purchase menu");
                backMeta.setLore(lore);
                back.setItemMeta(backMeta);
            }
            inv.setItem(22, back);
            
            player.openInventory(inv);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error opening quick select", e);
        }
    }
    
    private void createPresetButton(Inventory inv, int slot, int amount, String name, Material material) {
        if (inv == null) return;
        
        try {
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
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error creating preset button", e);
        }
    }
    
    public void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clicked = event.getCurrentItem();
        int slot = event.getSlot();
        
        if (clicked == null || clicked.getType() == Material.AIR || 
            clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) {
            return;
        }
        
        if (title.equals(SELECTION_TITLE)) {
            handleSelectionClick(player, slot, clicked);
        } else if (title.startsWith(ChatColor.of("#55FF55") + "Buy: ")) {
            handlePurchaseClick(player, slot, event.getClick().isRightClick());
        } else if (title.equals(QUICK_SELECT_TITLE)) {
            handleQuickSelectClick(player, slot);
        }
    }
    
    private void handleSelectionClick(Player player, int slot, ItemStack clicked) {
        if (slot == 53) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            new ShopMainMenu(plugin, economy).open(player);
            return;
        }
        
        int[] validSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
        for (int validSlot : validSlots) {
            if (slot == validSlot) {
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
    
    private void handlePurchaseClick(Player player, int slot, boolean isRightClick) {
        UUID uuid = player.getUniqueId();
        Skill skill = selectedSkills.get(uuid);
        if (skill == null) return;
        
        User user = plugin.getUser(player);
        if (user == null) return;
        
        int quantity = quantities.getOrDefault(uuid, MIN_QUANTITY);
        int available = skill.getMaxLevel() - user.getSkillLevel(skill);
        
        if (slot == 19 && quantity > 10) {
            quantities.put(uuid, quantity - 10);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.8f);
            updatePurchaseInventory(player);
        } else if (slot == 20 && quantity > MIN_QUANTITY) {
            quantities.put(uuid, quantity - 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.9f);
            updatePurchaseInventory(player);
        } else if (slot == 24 && quantity < available && quantity < MAX_QUANTITY) {
            quantities.put(uuid, quantity + 1);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.1f);
            updatePurchaseInventory(player);
        } else if (slot == 25 && quantity + 10 <= available && quantity + 10 <= MAX_QUANTITY) {
            quantities.put(uuid, Math.min(available, Math.min(MAX_QUANTITY, quantity + 10)));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
            updatePurchaseInventory(player);
        } else if (slot == 49) {
            performPurchase(player, skill, quantity);
        } else if (slot == 53) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            if (isRightClick) {
                openQuickSelect(player);
            } else {
                openSkillSelection(player);
            }
        }
    }
    
    private void handleQuickSelectClick(Player player, int slot) {
        UUID uuid = player.getUniqueId();
        Skill skill = selectedSkills.get(uuid);
        if (skill == null) return;
        
        if (slot == 22) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            updatePurchaseInventory(player);
            return;
        }
        
        User user = plugin.getUser(player);
        if (user == null) return;
        
        int available = skill.getMaxLevel() - user.getSkillLevel(skill);
        
        Map<Integer, Integer> presets = new HashMap<>();
        presets.put(10, 1);
        presets.put(11, 5);
        presets.put(13, 10);
        presets.put(15, 25);
        presets.put(16, available);
        
        if (presets.containsKey(slot)) {
            quantities.put(uuid, presets.get(slot));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            updatePurchaseInventory(player);
        }
    }
    
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
    
    public void handleClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        UUID uuid = event.getPlayer().getUniqueId();
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (event.getPlayer().getOpenInventory().getTopInventory().getSize() == 0) {
                cleanupPlayerData(uuid);
                MenuManager.getInstance(plugin).unregisterPlayer(uuid);
            }
        }, 2L);
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
        quantities.remove(uuid);
        menuTitles.remove(uuid);
    }
    
    private void fillBorder(Inventory inv) {
        if (inv == null) return;
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
    
    private Material getSkillIcon(Skill skill) {
        if (skill == null) return Material.EXPERIENCE_BOTTLE;
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
}
