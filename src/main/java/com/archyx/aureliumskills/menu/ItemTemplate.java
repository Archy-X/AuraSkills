package com.archyx.aureliumskills.menu;

import fr.minuskube.inv.content.SlotPos;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ItemTemplate {

    private final String name;
    private final Map<Object, ItemStack> baseItems;
    private final String displayName;
    private final List<String> lore;
    private final Map<Object, SlotPos> positions;

    public ItemTemplate(String name, Map<Object, ItemStack> baseItems, String displayName, List<String> lore, Map<Object, SlotPos> positions) {
        this.name = name;
        this.baseItems = baseItems;
        this.displayName = displayName;
        this.lore = lore;
        this.positions = positions;
    }

    public String getName() {
        return name;
    }

    public ItemStack getBaseItem(Object key) {
        return baseItems.get(key);
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public SlotPos getPosition(Object key) {
        return positions.get(key);
    }

}
