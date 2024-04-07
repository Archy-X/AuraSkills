package dev.aurelium.auraskills.api.event.loot;

import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Called when AuraSkills drops extra loot from mechanics like Fishing, Luck, and custom loot tables.
 */
public class LootDropEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final SkillsUser user;
    private ItemStack item;
    private Location location;
    private final Cause cause;
    private boolean toInventory;
    private boolean cancelled = false;

    public LootDropEvent(Player player, SkillsUser user, ItemStack item, Location location, Cause cause, boolean toInventory) {
        this.player = player;
        this.user = user;
        this.item = item;
        this.location = location;
        this.cause = cause;
        this.toInventory = toInventory;
    }

    /**
     * Gets the player that caused the loot drop.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the {@link SkillsUser} of the player that cause the loot drop.
     *
     * @return the user
     */
    public SkillsUser getUser() {
        return user;
    }

    /**
     * Gets the item that will be dropped by the event.
     *
     * @return the item
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Sets the item dropped by the event.
     *
     * @param item the item to be dropped
     * @return the event
     */
    public LootDropEvent setItem(ItemStack item) {
        this.item = item;
        return this;
    }

    /**
     * Gets the location that the item will be dropped at in the world.
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the location in the world that the item should be dropped at.
     *
     * @param location the location to drop
     * @return the event
     */
    public LootDropEvent setLocation(Location location) {
        this.location = location;
        return this;
    }

    /**
     * Gets the {@link Cause} that initiated the loot drop.
     *
     * @return the cause
     */
    public Cause getCause() {
        return cause;
    }

    /**
     * Gets whether the item will go directly into the player's inventory instead of being
     * dropped in the world.
     *
     * @return whether the drop goes directly to the player's inventory
     */
    public boolean isToInventory() {
        return toInventory;
    }

    /**
     * Sets whether the drop should go directly to the player's inventory instead of
     * being dropped in the world. If the player's inventory cannot fit the entire ItemStack,
     * the rest will be dropped in the world.
     *
     * @param toInventory true to go to inventory, false if not
     */
    public void setToInventory(boolean toInventory) {
        this.toInventory = toInventory;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum Cause {

        /**
         *
         */
        TREASURE_HUNTER,
        EPIC_CATCH,
        METAL_DETECTOR,
        LUCKY_SPADES,
        LUCK_DOUBLE_DROP,
        FISHING_OTHER_LOOT,
        EXCAVATION_OTHER_LOOT,
        MINING_OTHER_LOOT,
        FORAGING_OTHER_LOOT,
        UNKNOWN,
        MOB_LOOT_TABLE,
        FARMING_LUCK,
        FORAGING_LUCK,
        MINING_LUCK,
        FISHING_LUCK,
        EXCAVATION_LUCK

    }

}
