package dev.aurelium.auraskills.api.event.mana;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ManaAbilityRefreshEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final SkillsUser skillsUser;
    private final ManaAbility manaAbility;

    public ManaAbilityRefreshEvent(Player player, SkillsUser skillsUser, ManaAbility manaAbility) {
        this.player = player;
        this.skillsUser = skillsUser;
        this.manaAbility = manaAbility;
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

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
