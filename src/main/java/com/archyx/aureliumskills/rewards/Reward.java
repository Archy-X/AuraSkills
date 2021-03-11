package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public abstract class Reward {

    protected final AureliumSkills plugin;
    protected RewardMessages rewardMessages;

    public Reward(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public abstract void giveReward(Player player, Skill skill, int level);

    @Nullable
    public RewardMessages getRewardMessages() {
        return rewardMessages;
    }

    public void setRewardMessages(RewardMessages rewardMessages) {
        this.rewardMessages = rewardMessages;
    }

}
