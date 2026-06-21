package dev.aurelium.auraskills.api.item;

import org.bukkit.inventory.ItemStack;

public final class ModifierMenuData {

    private final String displayName;
    private final String description;
    private final ItemStack item;

    public ModifierMenuData(String displayName, String description, ItemStack item) {
        this.displayName = displayName;
        this.description = description;
        this.item = item;
    }

    public String displayName() {
        return displayName;
    }

    public String description() {
        return description;
    }

    public ItemStack item() {
        return item;
    }

    @Override
    public String toString() {
        return "ModifierMenuData[" +
                "displayName=" + displayName + ", " +
                "description=" + description + ", " +
                "item=" + item + ']';
    }
}
