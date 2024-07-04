package dev.aurelium.auraskills.api.event.loot;

import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private Entity entity;
    private boolean cancelled = false;

    public LootDropEvent(Player player, SkillsUser user, ItemStack item, Location location, Cause cause, boolean toInventory) {
        this.player = player;
        this.user = user;
        this.item = item;
        this.location = location;
        this.cause = cause;
        this.toInventory = toInventory;
    }

    public LootDropEvent(Player player, SkillsUser user, Entity entity, Location location, Cause cause) {
        this.player = player;
        this.user = user;
        this.location = location;
        this.cause = cause;
        this.toInventory = false;
        this.entity = entity;
        // Let's not break things and make the item not nullable still
        this.item = new ItemStack(Material.AIR);
    }

    /**
     * Gets the spawned entity if the loot was an entity.
     *
     * @return the entity
     */
    public @Nullable Entity getEntity() {
        return entity;
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
     * If the drop is an entity, this will be AIR.
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
         * Caused by the Treasure Hunter ability, configured as the "rare" Fishing loot table.
         */
        TREASURE_HUNTER,

        /**
         * Caused by the Epic Catch ability, configured as the "epic" Fishing loot table.
         */
        EPIC_CATCH,

        /**
         * Caused by the Metal Detector ability, configured as the "rare" Excavation loot table.
         */
        METAL_DETECTOR,

        /**
         * Caused by the Lucky Spades ability, configured as the "epic" Excavation loot table/
         */
        LUCKY_SPADES,

        /**
         * Caused by the Double Drop trait, which doesn't occur by default unless the trait is
         * explicitly added to a stat.
         */
        LUCK_DOUBLE_DROP,

        /**
         * Caused by a custom Fishing loot table that is not named "rare" or "epic".
         */
        FISHING_OTHER_LOOT,

        /**
         * Caused by a custom Excavation loot table that is not named "rare" or "epic".
         */
        EXCAVATION_OTHER_LOOT,

        /**
         * Caused by a custom Mining loot table.
         */
        MINING_OTHER_LOOT,

        /**
         * Caused by a custom Foraging loot table.
         */
        FORAGING_OTHER_LOOT,

        /**
         * Caused by a custom Farming loot table.
         */
        FARMING_OTHER_LOOT,

        /**
         * Caused is unknown.
         */
        UNKNOWN,

        /**
         * Caused by a mob loot table.
         */
        MOB_LOOT_TABLE,

        /**
         * Caused by the Farming Luck trait, which includes the Bountiful Harvest ability.
         */
        FARMING_LUCK,

        /**
         * Caused by the Foraging Luck trait, which includes the Lumberjack ability.
         */
        FORAGING_LUCK,

        /**
         * Caused by the Mining Luck trait, which includes the Lucky Miner ability.
         */
        MINING_LUCK,

        /**
         * Caused by the Fishing Luck trait, which includes the Lucky Catch ability.
         */
        FISHING_LUCK,

        /**
         * Caused by the Excavation luck trait, which includes the Bigger Scoop ability.
         */
        EXCAVATION_LUCK

    }

}
