package com.archyx.aureliumskills.menu;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ItemOption {

    private final String name;
    private final int row;
    private final int column;
    private final ItemStack baseItem;
    private final Map<Object, ItemStack> baseItems;
    private final String displayName;
    private final List<String> lore;

    public ItemOption(String name, int row, int column, ItemStack baseItem, String displayName, List<String> lore) {
        this.name = name;
        this.row = row;
        this.column = column;
        this.baseItem = baseItem;
        this.baseItems = null;
        this.displayName = displayName;
        this.lore = lore;
    }

    public ItemOption(String name, int row, int column, Map<Object, ItemStack> baseItems, String displayName, List<String> lore) {
        this.name = name;
        this.row = row;
        this.column = column;
        this.baseItems = baseItems;
        this.baseItem = null;
        this.displayName = displayName;
        this.lore = lore;
    }

    public String getName() {
        return name;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public ItemStack getBaseItem() {
        return baseItem;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public Map<Object, ItemStack> getBaseItems() {
        return baseItems;
    }

}
