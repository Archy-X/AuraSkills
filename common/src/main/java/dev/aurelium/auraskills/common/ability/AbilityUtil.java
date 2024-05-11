package dev.aurelium.auraskills.common.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;

public class AbilityUtil {

    public static String getUpgradeValue(Ability ability, int level, String format) {
        String currentValue = getCurrentValue(ability, level);
        String nextValue = NumberUtil.format2(ability.getValue(level + 1));
        return TextUtil.replace(format,
                "{current}", currentValue,
                "{next}", nextValue);
    }

    public static String getUpgradeValue2(Ability ability, int level, String format) {
        String currentValue = getCurrentValue2(ability, level);
        String nextValue = NumberUtil.format2(ability.getSecondaryValue(level + 1));
        return TextUtil.replace(format,
                "{current}", currentValue,
                "{next}", nextValue);
    }

    public static String getCurrentValue(Ability ability, int level) {
        return NumberUtil.format2(ability.getValue(level));
    }

    public static String getCurrentValue2(Ability ability, int level) {
        return NumberUtil.format2(ability.getSecondaryValue(level));
    }

    public static String getUpgradeValue(ManaAbility manaAbility, int level, String format) {
        String currentValue = NumberUtil.format2(manaAbility.getDisplayValue(level));
        String nextValue = NumberUtil.format2(manaAbility.getDisplayValue(level + 1));
        return TextUtil.replace(format,
                "{current}", currentValue,
                "{next}", nextValue);
    }

    public static String getUpgradeDuration(ManaAbility manaAbility, int level, String format) {
        String currentDuration = NumberUtil.format2(getDuration(manaAbility, level));
        String nextDuration = NumberUtil.format2(getDuration(manaAbility, level + 1));
        return TextUtil.replace(format,
                "{current}", currentDuration,
                "{next}", nextDuration);
    }

    public static double getDuration(ManaAbility manaAbility, int level) {
        if (manaAbility == ManaAbilities.LIGHTNING_BLADE) {
            double baseDuration = ManaAbilities.LIGHTNING_BLADE.optionDouble("base_duration", 5.0);
            double durationPerLevel = ManaAbilities.LIGHTNING_BLADE.optionDouble("duration_per_level", 4.0);
            return baseDuration + (durationPerLevel * (level - 1));
        } else {
            return manaAbility.getValue(level);
        }
    }

}
