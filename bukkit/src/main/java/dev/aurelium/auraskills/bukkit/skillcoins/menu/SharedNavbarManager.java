package dev.aurelium.auraskills.bukkit.skillcoins.menu;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.skillcoins.CurrencyType;
import dev.aurelium.auraskills.common.skillcoins.EconomyProvider;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.Replacer;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.aurelium.slate.menu.ActiveMenu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level; 

/**
 * Shared navbar manager for consistent navigation across all menus
 */
public class SharedNavbarManager {
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    private final FileConfiguration navbarConfig;
    
    // Exception menus that only show back button
    private static final List<String> SKILL_SELECTION_MENUS = Arrays.asList(
        "skills", "skill_select", "skill_abilities", "skill_sources", "skill_stats", "stat_info"
    );
    
    public SharedNavbarManager(AuraSkills plugin, EconomyProvider economy) {
        this.plugin = plugin;
        this.economy = economy;
        this.navbarConfig = loadNavbarConfig();
    }
    
    private FileConfiguration loadNavbarConfig() {
        try {
            File file = new File(plugin.getDataFolder(), "menus/shared_navbar.yml");
            if (!file.exists()) {
                plugin.saveResource("menus/shared_navbar.yml", false);
            }
            return YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load navbar config", e);
            return null;
        }
    }
    
    /**
     * Add consistent navbar to any inventory
     */
    public void addNavbar(Inventory inv, String menuName, int page, int maxPage, Player player) {
        if (navbarConfig == null) {
            addDefaultNavbar(inv, page, maxPage, player);
            return;
        }
        
        // Check if this is a skill selection menu (exceptions)
        if (isSkillSelectionMenu(menuName)) {
            addSkillSelectionNavbar(inv, menuName);
            return;
        }
        
        // Add standard navbar
        addStandardNavbar(inv, page, maxPage, player);
    }
    
    private boolean isSkillSelectionMenu(String menuName) {
        return SKILL_SELECTION_MENUS.contains(menuName);
    }
    
    private void addSkillSelectionNavbar(Inventory inv, String menuName) {
        // Only show back button for skill selection menus
        String backText = menuName.equals("skills") ? "Close" : "Back";
        
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta meta = back.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + backText);
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GRAY + "Return to main menu");
            meta.setLore(lore);
            back.setItemMeta(meta);
        }
        inv.setItem(53, back);
    }
    
    private void addStandardNavbar(Inventory inv, int page, int maxPage, Player player) {
        // Balance display (slot 45)
        addBalanceDisplay(inv, player);
        
        // Glass panes (slots 46, 47, 51, 52)
        addGlassPanes(inv);
        
        // Previous page button (slot 48)
        if (page > 0) {
            addPreviousPageButton(inv, page);
        }
        
        // Page info (slot 49)
        addPageInfo(inv, page, maxPage);
        
        // Next page button (slot 50)
        if (page < maxPage) {
            addNextPageButton(inv, page, maxPage);
        }
        
        // Back button (slot 53)
        addBackButton(inv);
    }
    
    private void addDefaultNavbar(Inventory inv, int page, int maxPage, Player player) {
        // Fallback to default navbar if config fails
        addBalanceDisplay(inv, player);
        addGlassPanes(inv);
        
        if (page > 0) {
            addPreviousPageButton(inv, page);
        }
        
        addPageInfo(inv, page, maxPage);
        
        if (page < maxPage) {
            addNextPageButton(inv, page, maxPage);
        }
        
        addBackButton(inv);
    }
    
    private void addBalanceDisplay(Inventory inv, Player player) {
        if (navbarConfig == null) return;
        
        User user = plugin.getUser(player);
        if (user == null) return;
        
        // Get balance configuration
        if (!navbarConfig.contains("navbar.balance")) {
            return;
        }
        
        double moneyBalance = economy.getBalance(player.getUniqueId(), CurrencyType.COINS);
        double tokenBalance = economy.getBalance(player.getUniqueId(), CurrencyType.TOKENS);
        String formattedMoney = String.format("%,.0f", moneyBalance);
        String formattedTokens = String.format("%,.0f", tokenBalance);
        
        // Create replacer for data placeholders
        Replacer replacer = new Replacer()
            .map("{balance}", () -> formattedMoney)
            .map("{coins}", () -> formattedMoney)
            .map("{tokens}", () -> formattedTokens);
        
        // Get navbar balance config
        String materialName = navbarConfig.getString("navbar.balance.material", "GOLD_NUGGET");
        Material material = Material.valueOf(materialName.toUpperCase());
        
        ItemStack balanceItem = new ItemStack(material);
        ItemMeta meta = balanceItem.getItemMeta();
        if (meta != null) {
            // Apply placeholders to display name
            String displayName = navbarConfig.getString("navbar.balance.display_name", "{{balance}}");
            displayName = applyMenuPlaceholders(displayName, player, "navbar", replacer);
            meta.setDisplayName(displayName);
            
            // Apply placeholders to lore
            List<String> lore = new ArrayList<>();
            List<String> configLore = navbarConfig.getStringList("navbar.balance.lore");
            for (String line : configLore) {
                line = applyMenuPlaceholders(line, player, "navbar", replacer);
                lore.add(line);
            }
            meta.setLore(lore);
            balanceItem.setItemMeta(meta);
        }
        
        // Get slot from config
        int slot = navbarConfig.getInt("navbar.balance.slot", 45);
        inv.setItem(slot, balanceItem);
    }
    
    private String applyMenuPlaceholders(String text, Player player, String menuName, Replacer replacer) {
        // First replace data placeholders
        text = TextUtil.replace(text, replacer);
        
        // Then replace menu message placeholders
        String[] placeholders = dev.aurelium.slate.util.TextUtil.substringsBetween(text, "{{", "}}");
        if (placeholders != null) {
            for (String placeholder : placeholders) {
                MessageKey messageKey = MessageKey.of("menus.navbar." + placeholder);
                MessageKey commonKey = MessageKey.of("menus.common." + placeholder);
                
                String message = plugin.getMessageProvider().getRaw(messageKey, plugin.getUser(player).getLocale());
                if (message.equals(messageKey.getPath())) { // Key not found, try common
                    message = plugin.getMessageProvider().getRaw(commonKey, plugin.getUser(player).getLocale());
                }
                
                if (!message.equals(commonKey.getPath())) { // Found a valid message
                    text = text.replace("{{" + placeholder + "}}", message);
                }
            }
        }
        
        return text;
    }
    
    private void addGlassPanes(Inventory inv) {
        if (navbarConfig == null) return;

        // New single list configuration: navbar.glass:
        // - material: BLACK_STAINED_GLASS_PANE
        //   display_name: ' '
        //   slot: 46
        if (navbarConfig.contains("navbar.glass")) {
            List<Map<?, ?>> glassList = navbarConfig.getMapList("navbar.glass");
            for (Map<?, ?> entry : glassList) {
                Object materialObj = entry.get("material");
                String materialName = materialObj != null ? String.valueOf(materialObj) : "BLACK_STAINED_GLASS_PANE";
                Material material = Material.valueOf(materialName.toUpperCase());

                ItemStack glass = new ItemStack(material);
                ItemMeta meta = glass.getItemMeta();
                if (meta != null) {
                    Object displayObj = entry.get("display_name");
                    String displayName = displayObj != null ? String.valueOf(displayObj) : " ";
                    meta.setDisplayName(displayName);
                    glass.setItemMeta(meta);
                }

                int slot;
                Object slotObj = entry.get("slot");
                if (slotObj instanceof Number) {
                    slot = ((Number) slotObj).intValue();
                } else if (slotObj != null) {
                    try {
                        slot = Integer.parseInt(String.valueOf(slotObj));
                    } catch (NumberFormatException e) {
                        slot = 46;
                    }
                } else {
                    slot = 46;
                }
                inv.setItem(slot, glass);
            }
            return;
        }

        // Backwards compatibility: check old separate glass keys
        String[] glassKeys = {"glass_left_1", "glass_left_2", "glass_right_1", "glass_right_2"};
        for (String key : glassKeys) {
            if (navbarConfig.contains("navbar." + key)) {
                String materialName = navbarConfig.getString("navbar." + key + ".material", "BLACK_STAINED_GLASS_PANE");
                Material material = Material.valueOf(materialName.toUpperCase());

                ItemStack glass = new ItemStack(material);
                ItemMeta meta = glass.getItemMeta();
                if (meta != null) {
                    String displayName = navbarConfig.getString("navbar." + key + ".display_name", " ");
                    meta.setDisplayName(displayName);
                    glass.setItemMeta(meta);
                }

                int slot = navbarConfig.getInt("navbar." + key + ".slot", 46); // Default slot
                inv.setItem(slot, glass);
            }
        }
    }
    
    private void addPreviousPageButton(Inventory inv, int page) {
        if (navbarConfig == null || !navbarConfig.contains("navbar.previous_page")) return;
        
        Replacer replacer = new Replacer()
            .map("{page}", () -> String.valueOf(page));
        
        String materialName = navbarConfig.getString("navbar.previous_page.material", "ARROW");
        Material material = Material.valueOf(materialName.toUpperCase());
        
        ItemStack prev = new ItemStack(material);
        ItemMeta meta = prev.getItemMeta();
        if (meta != null) {
            String displayName = navbarConfig.getString("navbar.previous_page.display_name", "{{previous_page}}");
            displayName = applyMenuPlaceholders(displayName, null, "navbar", replacer);
            meta.setDisplayName(displayName);
            
            List<String> lore = new ArrayList<>();
            List<String> configLore = navbarConfig.getStringList("navbar.previous_page.lore");
            for (String line : configLore) {
                line = applyMenuPlaceholders(line, null, "navbar", replacer);
                lore.add(line);
            }
            meta.setLore(lore);
            prev.setItemMeta(meta);
        }
        
        int slot = navbarConfig.getInt("navbar.previous_page.slot", 48);
        inv.setItem(slot, prev);
    }
    
    private void addPageInfo(Inventory inv, int page, int maxPage) {
        if (navbarConfig == null || !navbarConfig.contains("navbar.page_info")) return;
        
        Replacer replacer = new Replacer()
            .map("{page}", () -> String.valueOf(page + 1))
            .map("{total_pages}", () -> String.valueOf(maxPage + 1));
        
        String materialName = navbarConfig.getString("navbar.page_info.material", "PAPER");
        Material material = Material.valueOf(materialName.toUpperCase());
        
        ItemStack pageItem = new ItemStack(material);
        ItemMeta meta = pageItem.getItemMeta();
        if (meta != null) {
            String displayName = navbarConfig.getString("navbar.page_info.display_name", "{{page}}");
            displayName = applyMenuPlaceholders(displayName, null, "navbar", replacer);
            meta.setDisplayName(displayName);
            
            List<String> lore = new ArrayList<>();
            List<String> configLore = navbarConfig.getStringList("navbar.page_info.lore");
            for (String line : configLore) {
                line = applyMenuPlaceholders(line, null, "navbar", replacer);
                lore.add(line);
            }
            meta.setLore(lore);
            pageItem.setItemMeta(meta);
        }
        
        int slot = navbarConfig.getInt("navbar.page_info.slot", 49);
        inv.setItem(slot, pageItem);
    }
    
    private void addNextPageButton(Inventory inv, int page, int maxPage) {
        if (navbarConfig == null || !navbarConfig.contains("navbar.next_page")) return;
        
        Replacer replacer = new Replacer()
            .map("{page}", () -> String.valueOf(page + 2));
        
        String materialName = navbarConfig.getString("navbar.next_page.material", "ARROW");
        Material material = Material.valueOf(materialName.toUpperCase());
        
        ItemStack next = new ItemStack(material);
        ItemMeta meta = next.getItemMeta();
        if (meta != null) {
            String displayName = navbarConfig.getString("navbar.next_page.display_name", "{{next_page}}");
            displayName = applyMenuPlaceholders(displayName, null, "navbar", replacer);
            meta.setDisplayName(displayName);
            
            List<String> lore = new ArrayList<>();
            List<String> configLore = navbarConfig.getStringList("navbar.next_page.lore");
            for (String line : configLore) {
                line = applyMenuPlaceholders(line, null, "navbar", replacer);
                lore.add(line);
            }
            meta.setLore(lore);
            next.setItemMeta(meta);
        }
        
        int slot = navbarConfig.getInt("navbar.next_page.slot", 50);
        inv.setItem(slot, next);
    }
    
    private void addBackButton(Inventory inv) {
        if (navbarConfig == null || !navbarConfig.contains("navbar.back_close")) return;
        
        Replacer replacer = new Replacer()
            .map("{menu_name}", () -> "Main Menu"); // Default fallback
        
        String materialName = navbarConfig.getString("navbar.back_close.material", "ARROW");
        Material material = Material.valueOf(materialName.toUpperCase());
        
        ItemStack back = new ItemStack(material);
        ItemMeta meta = back.getItemMeta();
        if (meta != null) {
            String displayName = navbarConfig.getString("navbar.back_close.display_name", "{{back}}");
            displayName = applyMenuPlaceholders(displayName, null, "navbar", replacer);
            meta.setDisplayName(displayName);
            
            List<String> lore = new ArrayList<>();
            List<String> configLore = navbarConfig.getStringList("navbar.back_close.lore");
            for (String line : configLore) {
                line = applyMenuPlaceholders(line, null, "navbar", replacer);
                lore.add(line);
            }
            meta.setLore(lore);
            back.setItemMeta(meta);
        }
        
        int slot = navbarConfig.getInt("navbar.back_close.slot", 53);
        inv.setItem(slot, back);
    }
}