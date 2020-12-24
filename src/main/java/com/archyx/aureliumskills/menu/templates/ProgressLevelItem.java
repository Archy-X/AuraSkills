package com.archyx.aureliumskills.menu.templates;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.NumberUtil;
import com.archyx.aureliumskills.util.RomanNumber;

import java.util.Locale;

public abstract class ProgressLevelItem {

    public String getRewardsLore(Skill skill, int level, Locale locale) {
        Stat primaryStat = skill.getPrimaryStat();
        StringBuilder rewardsLore = new StringBuilder();
        rewardsLore.append(LoreUtil.replace(Lang.getMessage(MenuMessage.REWARDS_ENTRY, locale)
                ,"{color}", primaryStat.getColor(locale)
                ,"{num}", String.valueOf(1)
                ,"{symbol}", primaryStat.getSymbol(locale)
                ,"{stat}", primaryStat.getDisplayName(locale)));
        if (level % 2 == 0) {
            Stat secondaryStat = skill.getSecondaryStat();
            rewardsLore.append(LoreUtil.replace(Lang.getMessage(MenuMessage.REWARDS_ENTRY, locale)
                    ,"{color}", secondaryStat.getColor(locale)
                    ,"{num}", String.valueOf(1)
                    ,"{symbol}", secondaryStat.getSymbol(locale)
                    ,"{stat}", secondaryStat.getDisplayName(locale)));
        }
        return LoreUtil.replace(Lang.getMessage(MenuMessage.REWARDS, locale),"{rewards}", rewardsLore.toString());
    }

    public String getAbilityLore(Skill skill, int level, Locale locale) {
        StringBuilder abilityLore = new StringBuilder();
        if (skill.getAbilities().size() == 5) {
            for (Ability ability : Ability.getAbilities(skill, level)) {
                if (AureliumSkills.abilityManager.isEnabled(ability)) {
                    if (level == ability.getUnlock()) {
                        abilityLore.append(LoreUtil.replace(Lang.getMessage(MenuMessage.ABILITY_UNLOCK, locale)
                                , "{ability}", ability.getDisplayName(locale)
                                , "{desc}", LoreUtil.replace(ability.getDescription(locale)
                                        , "{value_2}", NumberUtil.format1(ability.getValue2(1))
                                        , "{value}", NumberUtil.format1(ability.getValue(1)))));
                    } else {
                        int abilityLevel = ((level - ability.getUnlock()) / ability.getLevelUp()) + 1;
                        if (abilityLevel <= ability.getMaxLevel() || ability.getMaxLevel() == 0) { // Check max level
                            abilityLore.append(LoreUtil.replace(Lang.getMessage(MenuMessage.ABILITY_LEVEL, locale)
                                    , "{ability}", ability.getDisplayName(locale)
                                    , "{level}", RomanNumber.toRoman(abilityLevel)
                                    , "{desc}", LoreUtil.replace(ability.getDescription(locale)
                                            , "{value_2}", NumberUtil.format1(ability.getValue2(abilityLevel))
                                            , "{value}", NumberUtil.format1(ability.getValue(abilityLevel)))));
                        }
                    }
                }
            }
        }
        return abilityLore.toString();
    }

    public String getManaAbilityLore(Skill skill, int level, Locale locale) {
        MAbility mAbility = MAbility.getManaAbility(skill, level);
        StringBuilder manaAbilityLore = new StringBuilder();
        if (mAbility != null) {
            if (AureliumSkills.abilityManager.isEnabled(mAbility)) {
                if (level == mAbility.getUnlock()) {
                    manaAbilityLore.append(LoreUtil.replace(Lang.getMessage(MenuMessage.MANA_ABILITY_UNLOCK, locale)
                            , "{mana_ability}", mAbility.getDisplayName(locale)
                            , "{desc}", LoreUtil.replace(mAbility.getDescription(locale)
                                    , "{value}", NumberUtil.format1(mAbility.getDisplayValue(1)))));
                }
                else {
                    int manaAbilityLevel = ((level - mAbility.getUnlock()) / mAbility.getLevelUp()) + 1;
                    manaAbilityLore.append(LoreUtil.replace(Lang.getMessage(MenuMessage.MANA_ABILITY_LEVEL, locale)
                            , "{mana_ability}", mAbility.getDisplayName(locale)
                            , "{level}", RomanNumber.toRoman(manaAbilityLevel)
                            , "{desc}", LoreUtil.replace(mAbility.getDescription(locale)
                                    , "{value}", NumberUtil.format1(mAbility.getDisplayValue(manaAbilityLevel)))));
                }
            }
        }
        return manaAbilityLore.toString();
    }

}
