package dev.aurelium.auraskills.api.event.skill;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player gains XP in a skill.
 */
public class XpGainEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final SkillsUser user;
    private final Skill skill;
    @Nullable
    private final XpSource source;
    private double amount;
    private boolean cancelled = false;

    public XpGainEvent(Player player, SkillsUser user, Skill skill, @Nullable XpSource source, double amount) {
        this.player = player;
        this.user = user;
        this.skill = skill;
        this.source = source;
        this.amount = amount;
    }

    /**
     * Gets the player tha gained the XP.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the {@link SkillsUser} of the player who gained XP.
     *
     * @return the user
     */
    public SkillsUser getUser() {
        return user;
    }

    /**
     * Gets the skill the XP was gained in.
     *
     * @return the skill
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * Gets the XP source that triggered XP gain.
     *
     * @return the source, or null if it is not from a defined source
     */
    @Nullable
    public XpSource getSource() {
        return source;
    }

    /**
     * Gets the amount of XP about to be gained, after applying  multipliers.
     *
     * @return the amount of XP gained
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of XP to be gained.
     *
     * @param amount the amount to be gained
     */
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
