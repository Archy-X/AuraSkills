package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.entity.Player;

public abstract class Reward {

    protected final AureliumSkills plugin;
    protected final String info;
    protected final String message;

    public Reward(AureliumSkills plugin, String info, String message) {
        this.plugin = plugin;
        this.info = info;
        this.message = message;
    }

    public String getInfo() {
        return info;
    }

    public String getMessage() {
        return message;
    }

    public abstract void giveReward(Player player, Skill skill, int level);

}
