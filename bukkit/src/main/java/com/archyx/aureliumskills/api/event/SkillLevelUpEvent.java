package com.archyx.aureliumskills.api.event;

import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkillLevelUpEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Skill skill;
    private final int level;

    public SkillLevelUpEvent(Player player, Skill skill, int level) {
        this.player = player;
        this.skill = skill;
        this.level = level;
    }

    public Player getPlayer() {
        return player;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
