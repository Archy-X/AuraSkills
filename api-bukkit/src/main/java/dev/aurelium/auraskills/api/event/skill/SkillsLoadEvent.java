package dev.aurelium.auraskills.api.event.skill;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Calls when skills have finished loading from configuration files, which is usually
 * on the first tick of the server. Most methods on {@link Skill} will not work until
 * this event calls. Listen to this event if accessing methods on default {@link Skills}
 * during plugin startup instead of in onEnable.
 */
public class SkillsLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Set<Skill> skills;

    public SkillsLoadEvent(Set<Skill> skills) {
        this.skills = skills;
    }

    /**
     * Gets all the skills that the plugin has loaded. Some skills may be disabled by users
     * through the config so use {@link Skill#isEnabled()} to check whether a skill is enabled.
     *
     * @return the loaded skills
     */
    public Set<Skill> getSkills() {
        return skills;
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
