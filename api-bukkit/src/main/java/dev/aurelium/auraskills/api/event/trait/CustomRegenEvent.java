package dev.aurelium.auraskills.api.event.trait;

import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CustomRegenEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final SkillsUser user;
    private double amount;
    private final Reason reason;
    private boolean cancelled = false;

    public CustomRegenEvent(Player player, SkillsUser user, double amount, Reason reason) {
        this.player = player;
        this.user = user;
        this.amount = amount;
        this.reason = reason;
    }

    public Player getPlayer() {
        return player;
    }

    public SkillsUser getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Reason getReason() {
        return reason;
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

    public enum Reason {

        SATURATION,
        HUNGER

    }

}
