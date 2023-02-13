package com.archyx.aureliumskills.api.event.source;

import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event that calls when XP is gained before any multipliers are applied.
 * Subclass events expose additional information about a specific type of XP gain.
 * May not be called for every skill, use subclass events instead for guaranteed calling.
 */
public abstract class SourceXpGainEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Skill skill;
    private double amount;
    private boolean isCancelled;

    public SourceXpGainEvent(Player player, Skill skill, double amount) {
        this.player = player;
        this.skill = skill;
        this.amount = amount;
        this.isCancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Skill getSkill() {
        return skill;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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
