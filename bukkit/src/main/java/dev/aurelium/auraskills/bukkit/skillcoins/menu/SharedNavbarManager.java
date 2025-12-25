package dev.aurelium.auraskills.bukkit.skillcoins.menu;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.skillcoins.CurrencyType;
import dev.aurelium.auraskills.common.skillcoins.EconomyProvider;
import dev.aurelium.auraskills.common.user.User;
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
        User user = plugin.getUser(player);
        if (user == null) return;
        
        double moneyBalance = economy.getBalance(player.getUniqueId(), CurrencyType.COINS);
        double tokenBalance = economy.getBalance(player.getUniqueId(), CurrencyType.TOKENS);
        String formattedMoney = String.format("%,.0f", moneyBalance);
        String formattedTokens = String.format("%,.0f", tokenBalance);
        
        ItemStack balanceItem = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = balanceItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Your Balance");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.YELLOW + "Coins: " + ChatColor.WHITE + formattedMoney);
            lore.add(ChatColor.AQUA + "Tokens: " + ChatColor.WHITE + formattedTokens);
            meta.setLore(lore);
            balanceItem.setItemMeta(meta);
        }
        inv.setItem(45, balanceItem);
    }
    
    private void addGlassPanes(Inventory inv) {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
        }
        
        // Set glass panes at slots 46, 47, 51, 52
        int[] glassSlots = {46, 47, 51, 52};
        for (int slot : glassSlots) {
            inv.setItem(slot, glass);
        }
    }
    
    private void addPreviousPageButton(Inventory inv, int page) {
        ItemStack prev = new ItemStack(Material.ARROW);
        ItemMeta meta = prev.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "← Previous Page");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GRAY + "Go to page " + page);
            meta.setLore(lore);
            prev.setItemMeta(meta);
        }
        inv.setItem(48, prev);
    }
    
    private void addPageInfo(Inventory inv, int page, int maxPage) {
        ItemStack pageItem = new ItemStack(Material.PAPER);
        ItemMeta meta = pageItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Page " + (page + 1) + " of " + (maxPage + 1));
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GRAY + "Current page information");
            meta.setLore(lore);
            pageItem.setItemMeta(meta);
        }
        inv.setItem(49, pageItem);
    }
    
    private void addNextPageButton(Inventory inv, int page, int maxPage) {
        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta meta = next.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Next Page →");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GRAY + "Go to page " + (page + 2));
            meta.setLore(lore);
            next.setItemMeta(meta);
        }
        inv.setItem(50, next);
    }
    
    private void addBackButton(Inventory inv) {
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta meta = back.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "← Back");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GRAY + "Return to main menu");
            meta.setLore(lore);
            back.setItemMeta(meta);
        }
        inv.setItem(53, back);
    }
}