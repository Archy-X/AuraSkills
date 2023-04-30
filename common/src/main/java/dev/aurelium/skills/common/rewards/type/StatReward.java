package dev.aurelium.skills.common.rewards.type;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.Stat;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.data.PlayerData;
import dev.aurelium.skills.common.message.MessageProvider;
import dev.aurelium.skills.common.message.type.LevelerMessage;
import dev.aurelium.skills.common.message.type.MenuMessage;
import dev.aurelium.skills.common.rewards.Reward;
import dev.aurelium.skills.common.util.math.NumberUtil;
import dev.aurelium.skills.common.util.text.TextUtil;

import java.util.Locale;

public class StatReward extends Reward {

    private final Stat stat;
    private final double value;

    public StatReward(AureliumSkillsPlugin plugin, Stat stat, double value) {
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
        MessageProvider prov = plugin.getMessageProvider();
        return TextUtil.replace(plugin.getMsg(MenuMessage.REWARDS_ENTRY, locale),
                "{color}", prov.getStatColor(locale, stat),
                "{num}", NumberUtil.format1(value),
                "{symbol}", prov.getStatSymbol(locale, stat),
                "{stat}", prov.getStatDisplayName(locale, stat));
    }

    @Override
    public String getChatMessage(PlayerData player, Locale locale, Skill skill, int level) {
        MessageProvider prov = plugin.getMessageProvider();
        return TextUtil.replace(plugin.getMsg(LevelerMessage.STAT_LEVEL, locale),
                "{color}", prov.getStatColor(locale, stat),
                "{num}", NumberUtil.format1(value),
                "{symbol}", prov.getStatSymbol(locale, stat),
                "{stat}", prov.getStatDisplayName(locale, stat));
    }

}
