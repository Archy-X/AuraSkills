package dev.aurelium.auraskills.bukkit.menus.common;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.player.User;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public abstract class AbstractSkillItem extends AbstractItem implements TemplateItemProvider<Skill> {

    public AbstractSkillItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<Skill> getContext() {
        return Skill.class;
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, Skill skill) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        int skillLevel = user.getSkillLevel(skill);

        switch (placeholder) {
            case "skill":
                return skill.getDisplayName(locale);
            case "skill_desc":
                return skill.getDescription(locale);
            case "stats_leveled":
                return getStatsLeveled(skill, locale);
            case "ability_levels":
                return getAbilityLevels(skill, user);
            case "mana_ability":
                return getManaAbility(skill, user);
            case "level":
                if (data.getType() == PlaceholderType.DISPLAY_NAME) {
                    return RomanNumber.toRoman(skillLevel, plugin);
                } else {
                    return TextUtil.replace(plugin.getMsg(MenuMessage.LEVEL, locale), "{level}", RomanNumber.toRoman(skillLevel, plugin));
                }
            case "progress_to_level":
                return getProgressToLevel(skill, user);
            case "max_level":
                if (skillLevel >= skill.getMaxLevel()) {
                    return plugin.getMsg(MenuMessage.MAX_LEVEL, locale);
                } else {
                    return "";
                }
            case "skill_click":
                return plugin.getMsg(MenuMessage.SKILL_CLICK, locale);
        }
        return placeholder;
    }

    private String getStatsLeveled(Skill skill, Locale locale) {
        ImmutableList<Stat> statsLeveled = plugin.getRewardManager().getRewardTable(skill).getStatsLeveled();
        StringBuilder statList = new StringBuilder();
        for (Stat stat : statsLeveled) {
            statList.append(stat.getColor(locale)).append(stat.getDisplayName(locale)).append(ChatColor.GRAY).append(", ");
        }
        if (statList.length() > 1) {
            statList.delete(statList.length() - 2, statList.length());
        }
        if (statsLeveled.size() > 0) {
            return TextUtil.replace(plugin.getMsg(MenuMessage.STATS_LEVELED, locale),
                    "{stats}", statList.toString());
        } else {
            return "";
        }
    }

    private String getAbilityLevels(Skill skill, User user) {
        Locale locale = user.getLocale();
        StringBuilder abilityLevelsLore = new StringBuilder();
        if (skill.getAbilities().size() == 5) {
            String levelsMessage = plugin.getMsg(MenuMessage.ABILITY_LEVELS, locale);
            int num = 1;
            List<Ability> abilities = new ArrayList<>(skill.getAbilities());
            abilities.sort(Comparator.comparingInt(Ability::getUnlock));
            for (Ability ability : abilities) {
                if (ability.isEnabled()) {
                    if (user.getAbilityLevel(ability) > 0) {
                        int abilityLevel = user.getAbilityLevel(ability);
                        levelsMessage = TextUtil.replace(levelsMessage, "{ability_" + num + "}", TextUtil.replace(plugin.getMsg(MenuMessage.ABILITY_LEVEL_ENTRY, locale)
                                , "{ability}", ability.getDisplayName(locale)
                                , "{level}", RomanNumber.toRoman(user.getAbilityLevel(ability), plugin)
                                , "{info}", TextUtil.replace(ability.getInfo(locale)
                                        , "{value}", NumberUtil.format1(ability.getValue(abilityLevel))
                                        , "{value_2}", NumberUtil.format1(ability.getSecondaryValue(abilityLevel)))));
                    } else {
                        levelsMessage = TextUtil.replace(levelsMessage, "{ability_" + num + "}", TextUtil.replace(plugin.getMsg(MenuMessage.ABILITY_LEVEL_ENTRY_LOCKED, locale)
                                , "{ability}", ability.getDisplayName(locale)));
                    }
                } else {
                    levelsMessage = TextUtil.replace(levelsMessage, "\\n  {ability_" + num + "}", ""
                            , "\n  {ability_" + num + "}", ""
                            , "{ability_" + num + "}", "");
                }
                num++;
            }
            abilityLevelsLore.append(levelsMessage);
        }
        return abilityLevelsLore.toString();
    }

    private String getManaAbility(Skill skill, User user) {
        Locale locale = user.getLocale();
        StringBuilder manaAbilityLore = new StringBuilder();
        ManaAbility manaAbility = skill.getManaAbility();
        if (manaAbility != null) {
            int level = user.getManaAbilityLevel(manaAbility);
            if (level > 0 && manaAbility.isEnabled()) {
                manaAbilityLore.append(TextUtil.replace(plugin.getMsg(getManaAbilityMessage(manaAbility), locale)
                        , "{mana_ability}", manaAbility.getDisplayName(locale)
                        , "{level}", RomanNumber.toRoman(level, plugin)
                        , "{duration}", NumberUtil.format1(getDuration(manaAbility, level))
                        , "{value}", NumberUtil.format1(manaAbility.getValue(level))
                        , "{mana_cost}", NumberUtil.format1(manaAbility.getManaCost(level))
                        , "{cooldown}", NumberUtil.format1(manaAbility.getCooldown(level))));

            }
        }
        return manaAbilityLore.toString();
    }

    private MessageKey getManaAbilityMessage(ManaAbility manaAbility) {
        if (manaAbility instanceof ManaAbilities manaAbilities) {
            switch (manaAbilities) {
                case SHARP_HOOK:
                    return ManaAbilityMessage.SHARP_HOOK_MENU;
                case CHARGED_SHOT:
                    return ManaAbilityMessage.CHARGED_SHOT_MENU;
                case LIGHTNING_BLADE:
                    return ManaAbilityMessage.LIGHTNING_BLADE_MENU;
                default:
                    return MenuMessage.MANA_ABILITY;
            }
        }
        return MenuMessage.MANA_ABILITY;
    }

    private double getDuration(ManaAbility manaAbility, int level) {
        if (manaAbility == ManaAbilities.LIGHTNING_BLADE) {
            double baseDuration = ManaAbilities.LIGHTNING_BLADE.optionDouble("base_duration");
            double durationPerLevel = ManaAbilities.LIGHTNING_BLADE.optionDouble("duration_per_level");
            return baseDuration + (durationPerLevel * (level - 1));
        } else {
            return manaAbility.getValue(level);
        }
    }

    private String getProgressToLevel(Skill skill, User user) {
        int skillLevel = user.getSkillLevel(skill);
        Locale locale = user.getLocale();
        if (skillLevel < skill.getMaxLevel()) {
            double currentXp = user.getSkillXp(skill);
            double xpToNext = plugin.getXpRequirements().getXpRequired(skill, skillLevel + 1);
            return TextUtil.replace(plugin.getMsg(MenuMessage.PROGRESS_TO_LEVEL, locale)
                    ,"{level}", RomanNumber.toRoman(skillLevel + 1, plugin)
                    ,"{percent}", NumberUtil.format2(currentXp / xpToNext * 100)
                    ,"{current_xp}", NumberUtil.format2(currentXp)
                    ,"{level_xp}", String.valueOf((int) xpToNext));
        } else {
            return "";
        }
    }

}
