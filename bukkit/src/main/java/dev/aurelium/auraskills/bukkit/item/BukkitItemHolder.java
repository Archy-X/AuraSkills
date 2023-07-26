package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.item.ItemHolder;
import org.bukkit.inventory.ItemStack;

public class BukkitItemHolder implements ItemHolder {

    private final ItemStack item;

    public BukkitItemHolder(ItemStack item) {
        this.item = item;
    }

    @Override
    public <T> T get(Class<T> itemClass) {
        if (itemClass.isAssignableFrom(ItemStack.class)) {
            return itemClass.cast(item);
        } else {
            throw new RuntimeException("Platform ItemHolder implementation is not of type " + itemClass.getName());
        }
    }
}
