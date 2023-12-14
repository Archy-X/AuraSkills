package dev.aurelium.auraskills.api.event.mana;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ManaAbilityActivateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final SkillsUser skillsUser;
    private final ManaAbility manaAbility;
    private int duration;
    private boolean cancelled = false;
    private double manaUsed;

    public ManaAbilityActivateEvent(Player player, SkillsUser user, ManaAbility manaAbility, int duration, double manaUsed) {
        this.player = player;
        this.skillsUser = user;
        this.manaAbility = manaAbility;
        this.duration = duration;
        this.manaUsed = manaUsed;
    }

    public Player getPlayer() {
        return player;
    }

    public SkillsUser getUser() {
        return skillsUser;
    }

    public ManaAbility getManaAbility() {
        return manaAbility;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getManaUsed() {
        return manaUsed;
    }

    public void setManaUsed(double manaUsed) {
        this.manaUsed = manaUsed;
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
