package dev.aurelium.auraskills.bukkit.skillcoins.shop;

import dev.aurelium.auraskills.common.skillcoins.CurrencyType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single item in the shop
 */
public class ShopItem {
    
    public enum ItemType {
        REGULAR,        // Regular item shop
        SKILL_LEVEL,    // Purchase skill levels with tokens
        TOKEN_EXCHANGE  // Exchange coins for tokens
    }
    
    private final Material material;
    private final double buyPrice;
    private final double sellPrice;
    private final Map<Enchantment, Integer> enchantments;
    private final ItemType type;
    private final String skillName;  // For SKILL_LEVEL type
    private final int tokenAmount;   // For TOKEN_EXCHANGE type
    private final CurrencyType currency; // Which currency to use for purchase
    
    public ShopItem(Material material, double buyPrice, double sellPrice) {
        this(material, buyPrice, sellPrice, new HashMap<>(), ItemType.REGULAR, null, 0, CurrencyType.COINS);
    }
    
    public ShopItem(Material material, double buyPrice, double sellPrice, Map<Enchantment, Integer> enchantments) {
        this(material, buyPrice, sellPrice, enchantments, ItemType.REGULAR, null, 0, CurrencyType.COINS);
    }
    
    public ShopItem(Material material, double buyPrice, double sellPrice, Map<Enchantment, Integer> enchantments,
                    ItemType type, String skillName, int tokenAmount, CurrencyType currency) {
        this.material = material;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.enchantments = new HashMap<>(enchantments);
        this.type = type;
        this.skillName = skillName;
        this.tokenAmount = tokenAmount;
        this.currency = currency;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public double getBuyPrice() {
        return buyPrice;
    }
    
    public double getSellPrice() {
        return sellPrice;
    }
    
    public Map<Enchantment, Integer> getEnchantments() {
        return new HashMap<>(enchantments);
    }
    
    public boolean hasEnchantments() {
        return !enchantments.isEmpty();
    }
    
    public ItemType getType() {
        return type;
    }
    
    public String getSkillName() {
        return skillName;
    }
    
    public int getTokenAmount() {
        return tokenAmount;
    }
    
    public CurrencyType getCurrency() {
        return currency;
    }
    
    /**
     * Check if buying is enabled (-1 means disabled)
     */
    public boolean canBuy() {
        return buyPrice >= 0;
    }
    
    /**
     * Check if selling is enabled (-1 means disabled)
     */
    public boolean canSell() {
        return sellPrice >= 0 && type == ItemType.REGULAR;
    }
    
    /**
     * Create an ItemStack from this shop item
     */
    public ItemStack createItemStack(int amount) {
        ItemStack item = new ItemStack(material, amount);
        
        if (!enchantments.isEmpty()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    meta.addEnchant(entry.getKey(), entry.getValue(), true);
                }
                item.setItemMeta(meta);
            }
        }
        
        return item;
    }
    
    /**
     * Check if an ItemStack matches this shop item
     */
    public boolean matches(ItemStack item) {
        if (item == null || item.getType() != material) {
            return false;
        }
        
        // Check enchantments if this shop item has them
        if (hasEnchantments()) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null || !meta.hasEnchants()) {
                return false;
            }
            
            Map<Enchantment, Integer> itemEnchants = meta.getEnchants();
            if (itemEnchants.size() != enchantments.size()) {
                return false;
            }
            
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Integer itemLevel = itemEnchants.get(entry.getKey());
                if (itemLevel == null || !itemLevel.equals(entry.getValue())) {
                    return false;
                }
            }
        }
        
        return true;
    }
}
