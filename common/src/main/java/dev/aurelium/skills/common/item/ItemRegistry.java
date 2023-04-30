package dev.aurelium.skills.common.item;

import dev.aurelium.skills.api.util.NamespacedId;
import org.jetbrains.annotations.Nullable;

public interface ItemRegistry {

    /**
     * Gets whether the item registry contains an item with the given key.
     *
     * @param key The item key
     * @return True if the item is registered, false otherwise
     */
    boolean containsItem(NamespacedId key);

    /**
     * Gives the player an item from the item registry. Adds
     * the item to the player's inventory if possible, otherwise
     * adds it to the player's unclaimed items.
     *
     * @param key The item key
     * @param amount The amount of the item, -1 to use the amount of the registered item
     */
    void giveItem(NamespacedId key, int amount);

    /**
     * Gets the amount of an item in the item registry.
     *
     * @param key The item key
     * @return The amount of the item, 0 if the item is not registered
     */
    int getItemAmount(NamespacedId key);

    /**
     * Gets the effective item name of an item in the item registry.
     * First checks for a display name, otherwise uses the localized name.
     *
     * @param key The item key
     * @return The effective item name, null if the item is not registered
     */
    @Nullable
    String getEffectiveItemName(NamespacedId key);

}
