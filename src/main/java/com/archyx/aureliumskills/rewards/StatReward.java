package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import org.bukkit.entity.Player;

public class StatReward extends Reward {

    private final Stat stat;
    private final double value;

    public StatReward(AureliumSkills plugin, Stat stat, double value) {
        super(plugin);
        this.stat = stat;
        this.value = value;
    }

    @Override
    public void giveReward(Player player, Skill skill, int level) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null) return;

        playerData.addStatLevel(stat, value);
    }

    public Stat getStat() {
        return stat;
    }

    public double getValue() {
        return value;
    }

}
