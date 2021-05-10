package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.LevelerMessage;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.lang.MessageKey;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.stats.StatLeveler;
import com.archyx.aureliumskills.util.item.LoreUtil;
import com.archyx.aureliumskills.util.math.NumberUtil;
import org.bukkit.entity.Player;

import java.util.Locale;

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
        new StatLeveler(plugin).reloadStat(player, stat);
    }

    public Stat getStat() {
        return stat;
    }

    public double getValue() {
        return value;
    }

    @Override
    public RewardMessages getRewardMessages(Locale locale) {
        MessageKey[] keys = new MessageKey[] { MenuMessage.REWARDS_ENTRY, LevelerMessage.STAT_LEVEL };
        String[] messages = new String[2];
        for (int i = 0; i < 2; i++) {
            messages[i] = LoreUtil.replace(Lang.getMessage(keys[i], locale),
                    "{color}", stat.getColor(locale),
                    "{num}", NumberUtil.format1(value),
                    "{symbol}", stat.getSymbol(locale),
                    "{stat}", stat.getDisplayName(locale));
        }
        return new RewardMessages(messages[0], messages[1]);
    }

}
