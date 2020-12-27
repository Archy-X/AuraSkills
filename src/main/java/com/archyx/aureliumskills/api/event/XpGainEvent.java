package com.archyx.aureliumskills.api.event;

import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class XpGainEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Skill skill;
    private double amount;
    private boolean isCancelled;

    public XpGainEvent(Player player, Skill skill, double amount) {
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
