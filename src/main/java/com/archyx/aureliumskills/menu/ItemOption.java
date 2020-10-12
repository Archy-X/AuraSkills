package com.archyx.aureliumskills.menu;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemOption {

    private final String name;
    private final int row;
    private final int column;
    private final ItemStack baseItem;
    private final Map<Object, ItemStack> baseItems;
    private String skullOwner;

    public ItemOption(String name, int row, int column, ItemStack baseItem) {
        this.name = name;
        this.row = row;
        this.column = column;
        this.baseItem = baseItem;
        this.baseItems = null;
    }

    public ItemOption(String name, int row, int column, Map<Object, ItemStack> baseItems) {
        this.name = name;
        this.row = row;
        this.column = column;
        this.baseItems = baseItems;
        this.baseItem = null;
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

    public Map<Object, ItemStack> getBaseItems() {
        return baseItems;
    }

    public String getSkullOwner() {
        return skullOwner;
    }

    public void setSkullOwner(String skullOwner) {
        this.skullOwner = skullOwner;
    }

}
