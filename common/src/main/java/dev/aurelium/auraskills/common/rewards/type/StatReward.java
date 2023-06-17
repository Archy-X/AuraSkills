package dev.aurelium.auraskills.common.rewards.type;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.data.PlayerData;
import dev.aurelium.auraskills.common.message.type.LevelerMessage;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.rewards.Reward;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;

import java.util.Locale;

public class StatReward extends Reward {

    private final Stat stat;
    private final double value;

    public StatReward(AuraSkillsPlugin plugin, Stat stat, double value) {
        super(plugin);
        this.stat = stat;
        this.value = value;
    }

    @Override
    public void giveReward(PlayerData playerData, Skill skill, int level) {
        playerData.addStatLevel(stat, value);
        plugin.getStatManager().reloadStat(playerData, stat);
    }

    public Stat getStat() {
        return stat;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String getMenuMessage(PlayerData player, Locale locale, Skill skill, int level) {
        return TextUtil.replace(plugin.getMsg(MenuMessage.REWARDS_ENTRY, locale),
                "{color}", stat.getColor(locale),
                "{num}", NumberUtil.format1(value),
                "{symbol}", stat.getSymbol(locale),
                "{stat}", stat.getDisplayName(locale));
    }

    @Override
    public String getChatMessage(PlayerData player, Locale locale, Skill skill, int level) {
        return TextUtil.replace(plugin.getMsg(LevelerMessage.STAT_LEVEL, locale),
                "{color}", stat.getColor(locale),
                "{num}", NumberUtil.format1(value),
                "{symbol}", stat.getSymbol(locale),
                "{stat}", stat.getDisplayName(locale));
    }

}
