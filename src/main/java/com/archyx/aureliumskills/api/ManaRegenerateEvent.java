package com.archyx.aureliumskills.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ManaRegenerateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private int amount;
    private boolean isCancelled;

    public ManaRegenerateEvent(Player player, int amount) {
        this.player = player;
        this.amount = amount;
        this.isCancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
