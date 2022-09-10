package com.archyx.aureliumskills.api.event;

import com.archyx.aureliumskills.ability.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbilityActivateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Ability ability;
    private int duration;
    private boolean isCancelled;

    public AbilityActivateEvent(Player player, Ability ability, int duration) {
        this.player = player;
        this.ability = ability;
        this.duration = duration;
        this.isCancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Ability getAbility() {
        return ability;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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
