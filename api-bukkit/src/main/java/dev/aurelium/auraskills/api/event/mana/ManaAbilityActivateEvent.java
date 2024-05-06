package dev.aurelium.auraskills.api.event.mana;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player activates a mana ability.
 */
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

    /**
     * Gets the player that activated the mana ability.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the {@link SkillsUser} that activated the ability.
     *
     * @return the user
     */
    public SkillsUser getUser() {
        return skillsUser;
    }

    /**
     * Gets the mana ability that was activated.
     *
     * @return the mana ability
     */
    public ManaAbility getManaAbility() {
        return manaAbility;
    }

    /**
     * Gets the duration the mana ability will be active in ticks.
     *
     * @return the duration in ticks
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets the duration the mana ability will be active for in ticks.
     *
     * @param duration duration in ticks
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Gets the amount of mana consumed by the activation.
     *
     * @return the mana used
     */
    public double getManaUsed() {
        return manaUsed;
    }

    /**
     * Sets the amount of mana consumed by the activation.
     *
     * @param manaUsed the mana consumed
     */
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
