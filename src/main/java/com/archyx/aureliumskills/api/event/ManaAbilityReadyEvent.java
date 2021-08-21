package com.archyx.aureliumskills.api.event;

import com.archyx.aureliumskills.mana.MAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ManaAbilityReadyEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final MAbility manaAbility;

    public ManaAbilityReadyEvent(Player player, MAbility manaAbility) {
        this.player = player;
        this.manaAbility = manaAbility;
    }


    public Player getPlayer() {
        return player;
    }

    public MAbility getAbility() {
        return manaAbility;
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
