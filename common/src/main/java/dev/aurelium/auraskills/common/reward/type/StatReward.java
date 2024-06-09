package dev.aurelium.auraskills.common.reward.type;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.message.type.LevelerFormat;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;

import java.util.Locale;

public class StatReward extends SkillReward {

    private final Stat stat;
    private final double value;

    public StatReward(AuraSkillsPlugin plugin, Stat stat, double value) {
        super(plugin);
        this.stat = stat;
        this.value = value;
    }

    @Override
    public void giveReward(User user, Skill skill, int level) {
        user.addStatLevel(stat, value);
        plugin.getStatManager().reloadStat(user, stat);
    }

    public Stat getStat() {
        return stat;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String getMenuMessage(User player, Locale locale, Skill skill, int level) {
        String format = plugin.getMenuHelper().getFormat("level_progression", "stat_reward_entry");
        return TextUtil.replace(format,
                "{color}", stat.getColor(locale),
                "{num}", getDisplayValue(locale),
                "{symbol}", stat.getSymbol(locale),
                "{stat}", stat.getDisplayName(locale, false));
    }

    @Override
    public String getChatMessage(User player, Locale locale, Skill skill, int level) {
        return TextUtil.replace(plugin.getMessageProvider().getRaw(LevelerFormat.STAT_LEVEL, locale),
                "{color}", stat.getColor(locale),
                "{num}", getDisplayValue(locale),
                "{symbol}", stat.getSymbol(locale),
                "{stat}", stat.getDisplayName(locale, false));
    }

    private String getDisplayValue(Locale locale) {
        if (stat.getTraits().contains(Traits.HP) && stat.getTraitModifier(Traits.HP) == 1.0) {
            return Traits.HP.getMenuDisplay(value, locale);
        } else {
            return NumberUtil.format1(value);
        }
    }

}
