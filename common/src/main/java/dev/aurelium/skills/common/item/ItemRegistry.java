package dev.aurelium.skills.common.item;

public interface ItemRegistry {

    boolean containsItem(String key);

    /**
     * Gives the player an item from the item registry. Adds
     * the item to the player's inventory if possible, otherwise
     * adds it to the player's unclaimed items.
     *
     * @param key The item key
     * @param amount The amount of the item, -1 to use the amount of the registered item
     */
    void giveItem(String key, int amount);

}
