package dev.aurelium.auraskills.api.event.loot;

import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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

    public Player getPlayer() {
        return player;
    }

    public SkillsUser getUser() {
        return user;
    }

    public ItemStack getItem() {
        return item;
    }

    public LootDropEvent setItem(ItemStack item) {
        this.item = item;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public LootDropEvent setLocation(Location location) {
        this.location = location;
        return this;
    }

    public Cause getCause() {
        return cause;
    }

    public boolean isToInventory() {
        return toInventory;
    }

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

        BOUNTIFUL_HARVEST,
        TRIPLE_HARVEST,
        LUMBERJACK,
        LUCKY_MINER,
        LUCKY_CATCH,
        TREASURE_HUNTER,
        EPIC_CATCH,
        METAL_DETECTOR,
        BIGGER_SCOOP,
        LUCKY_SPADES,
        LUCK_DOUBLE_DROP,
        FISHING_OTHER_LOOT,
        EXCAVATION_OTHER_LOOT,
        MINING_OTHER_LOOT,
        FORAGING_OTHER_LOOT,
        UNKNOWN,
        MOB_LOOT_TABLE

    }

}
