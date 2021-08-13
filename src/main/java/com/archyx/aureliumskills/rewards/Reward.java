package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.entity.Player;

import java.util.Locale;

public abstract class Reward {

    protected final AureliumSkills plugin;

    public Reward(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public abstract void giveReward(Player player, Skill skill, int level);

    public abstract String getMenuMessage(Player player, Locale locale, Skill skill, int level);

    public abstract String getChatMessage(Player player, Locale locale, Skill skill, int level);

}
