package dev.aurelium.auraskills.api.event.mana;

import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ManaRegenerateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final SkillsUser skillsUser;
    private double amount;
    private boolean cancelled = false;

    public ManaRegenerateEvent(Player player, SkillsUser skillsUser, double amount) {
        this.player = player;
        this.skillsUser = skillsUser;
        this.amount = amount;
    }

    public Player getPlayer() {
        return player;
    }

    public SkillsUser getUser() {
        return skillsUser;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
}
