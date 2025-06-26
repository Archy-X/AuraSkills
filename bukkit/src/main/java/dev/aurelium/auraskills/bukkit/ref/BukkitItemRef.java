package dev.aurelium.auraskills.bukkit.ref;

import dev.aurelium.auraskills.common.ref.ItemRef;
import org.bukkit.inventory.ItemStack;

public class BukkitItemRef implements ItemRef, Cloneable {

    private final ItemStack item;

    public BukkitItemRef(ItemStack item) {
        this.item = item;
    }

    public static BukkitItemRef wrap(ItemStack item) {
        return new BukkitItemRef(item);
    }

    public static ItemStack unwrap(ItemRef ref) {
        return ((BukkitItemRef) ref).get();
    }

    @Override
    public ItemStack get() {
        return item;
    }

    @Override
    public ItemRef clone() {
        try {
            return wrap(unwrap((ItemRef) super.clone()).clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
