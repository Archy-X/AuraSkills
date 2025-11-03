package dev.aurelium.auraskills.bukkit.skillcoins.shop;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a section of the shop (Combat, Resources, etc.)
 */
public class ShopSection {
    
    private final String id;
    private final String displayName;
    private final Material icon;
    private final int slot;
    private final List<ShopItem> items;
    
    public ShopSection(String id, String displayName, Material icon, int slot) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.slot = slot;
        this.items = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Material getIcon() {
        return icon;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public List<ShopItem> getItems() {
        return new ArrayList<>(items);
    }
    
    public void addItem(ShopItem item) {
        items.add(item);
    }
    
    public int getItemCount() {
        return items.size();
    }
}
