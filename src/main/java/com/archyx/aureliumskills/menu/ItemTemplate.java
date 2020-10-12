package com.archyx.aureliumskills.menu;

import fr.minuskube.inv.content.SlotPos;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemTemplate {

    private final String name;
    private final Map<Object, ItemStack> baseItems;
    private final Map<Object, SlotPos> positions;

    public ItemTemplate(String name, Map<Object, ItemStack> baseItems, Map<Object, SlotPos> positions) {
        this.name = name;
        this.baseItems = baseItems;
        this.positions = positions;
    }

    public String getName() {
        return name;
    }

    public ItemStack getBaseItem(Object key) {
        return baseItems.get(key);
    }

    public SlotPos getPosition(Object key) {
        return positions.get(key);
    }

}
