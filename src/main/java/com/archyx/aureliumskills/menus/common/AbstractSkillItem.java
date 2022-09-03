package com.archyx.aureliumskills.menus.common;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.lang.MessageKey;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.math.RomanNumber;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public abstract class AbstractSkillItem extends AbstractItem implements TemplateItemProvider<Skill> {

    public AbstractSkillItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public @NotNull Class<Skill> getContext() {
        return Skill.class;
    }

    @Override
    public @NotNull String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu activeMenu, @NotNull PlaceholderType type, @NotNull Skill skill) {
        @Nullable PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null)
            return placeholder;
        @Nullable Locale locale = playerData.getLocale();
        int skillLevel = playerData.getSkillLevel(skill);
        @Nullable String m = placeholder;
        switch (placeholder) {
            case "skill":
                m = skill.getDisplayName(locale);
                break;
            case "skill_desc":
                m = skill.getDescription(locale);
                break;
            case "stats_leveled":
                m = getStatsLeveled(skill, locale);
                break;
            case "ability_levels":
                m = getAbilityLevels(skill, playerData);
                break;
            case "mana_ability":
                m = getManaAbility(skill, playerData);
                break;
            case "level":
                if (type == PlaceholderType.DISPLAY_NAME) {
                    m = RomanNumber.toRoman(skillLevel);
                } else {
                    m = Lang.getMessage(MenuMessage.LEVEL, locale);
                    assert (null != m);
                    m = TextUtil.replace(m, "{level}", RomanNumber.toRoman(skillLevel));
                }
                break;
            case "progress_to_level":
                m = getProgressToLevel(skill, playerData);
                break;
            case "max_level":
                if (skillLevel >= OptionL.getMaxLevel(skill)) {
                    m = Lang.getMessage(MenuMessage.MAX_LEVEL, locale);
                } else {
                    m = "";
                }
                break;
            case "skill_click":
                m = Lang.getMessage(MenuMessage.SKILL_CLICK, locale);
                break;
        }
        assert (m != null);
        return m;
    }

    protected int getPage(@NotNull Skill skill, @NotNull PlayerData playerData) {
        int page = (playerData.getSkillLevel(skill) - 2) / 24;
        int maxLevelPage = (OptionL.getMaxLevel(skill) - 2) / 24;
        if (page > maxLevelPage) {
            page = maxLevelPage;
        }
        return page;
    }

    private @NotNull String getStatsLeveled(@NotNull Skill skill, @Nullable Locale locale) {
        ImmutableList<@NotNull Stat> statsLeveled = plugin.getRewardManager().getRewardTable(skill).getStatsLeveled();
        StringBuilder statList = new StringBuilder();
        for (Stat stat : statsLeveled) {
            statList.append(stat.getColor(locale)).append(stat.getDisplayName(locale)).append(ChatColor.GRAY).append(", ");
        }
        if (statList.length() > 1) {
            statList.delete(statList.length() - 2, statList.length());
        }
        if (statsLeveled.size() > 0) {
            @Nullable String m = Lang.getMessage(MenuMessage.STATS_LEVELED, locale);
            assert (null != m);
            m = TextUtil.replace(m, "{stats}", statList.toString());
            return m;
        }
        return "";
    }

    private @NotNull String getAbilityLevels(@NotNull Skill skill, @NotNull PlayerData playerData) {
        @Nullable Locale locale = playerData.getLocale();
        StringBuilder abilityLevelsLore = new StringBuilder();
        if (skill.getAbilities().size() == 5) {
            String levelsMessage = Lang.getMessage(MenuMessage.ABILITY_LEVELS, locale);
            int num = 1;
            List<@NotNull Ability> abilities = new ArrayList<>();
            for (Supplier<@NotNull Ability> abilitySupplier : skill.getAbilities()) {
                abilities.add(abilitySupplier.get());
            }
            abilities.sort(Comparator.comparingInt(a -> plugin.getAbilityManager().getUnlock(a)));
            for (@NotNull Ability ability : abilities) {
                if (plugin.getAbilityManager().isEnabled(ability)) {
                    if (playerData.getAbilityLevel(ability) > 0) {
                        int abilityLevel = playerData.getAbilityLevel(ability);
                        levelsMessage = TextUtil.replace(levelsMessage, "{ability_" + num + "}", TextUtil.replace(Lang.getMessage(MenuMessage.ABILITY_LEVEL_ENTRY, locale)
                                , "{ability}", ability.getDisplayName(locale)
                                , "{level}", RomanNumber.toRoman(playerData.getAbilityLevel(ability))
                                , "{info}", TextUtil.replace(ability.getInfo(locale)
                                , "{value}", NumberUtil.format1(plugin.getAbilityManager().getValue(ability, abilityLevel))
                                , "{value_2}", NumberUtil.format1(plugin.getAbilityManager().getValue2(ability, abilityLevel)))));
                    } else {
                        levelsMessage = TextUtil.replace(levelsMessage, "{ability_" + num + "}", TextUtil.replace(Lang.getMessage(MenuMessage.ABILITY_LEVEL_ENTRY_LOCKED, locale)
                                , "{ability}", ability.getDisplayName(locale)));
                    }
                } else {
                    levelsMessage = TextUtil.replace(levelsMessage, "\\n  {ability_" + num + "}", ""
                            , "{ability_" + num + "}", "");
                }
                num++;
            }
            abilityLevelsLore.append(levelsMessage);
        }
        return abilityLevelsLore.toString();
    }

    private @NotNull String getManaAbility(@NotNull Skill skill, @NotNull PlayerData playerData) {
        @Nullable Locale locale = playerData.getLocale();
        StringBuilder manaAbilityLore = new StringBuilder();
        MAbility mAbility = skill.getManaAbility();
        if (mAbility != null) {
            int level = playerData.getManaAbilityLevel(mAbility);
            if (level > 0 && plugin.getAbilityManager().isEnabled(mAbility)) {
                ManaAbilityManager manager = plugin.getManaAbilityManager();
                @Nullable String m = TextUtil.replace(Lang.getMessage(getManaAbilityMessage(mAbility), locale)
                        , "{mana_ability}", mAbility.getDisplayName(locale)
                        , "{level}", RomanNumber.toRoman(level)
                        , "{duration}", NumberUtil.format1(getDuration(mAbility, level))
                        , "{value}", NumberUtil.format1(manager.getValue(mAbility, level))
                        , "{mana_cost}", NumberUtil.format1(manager.getManaCost(mAbility, level))
                        , "{cooldown}", NumberUtil.format1(manager.getCooldown(mAbility, level)));
                assert (null != m);
                manaAbilityLore.append(m);

            }
        }
        return manaAbilityLore.toString();
    }

    private @NotNull MessageKey getManaAbilityMessage(@NotNull MAbility mAbility) {
        switch (mAbility) {
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

    private double getDuration(@NotNull MAbility mAbility, int level) {
        if (mAbility == MAbility.LIGHTNING_BLADE) {
            double baseDuration = plugin.getManaAbilityManager().getOptionAsDouble(MAbility.LIGHTNING_BLADE, "base_duration");
            double durationPerLevel = plugin.getManaAbilityManager().getOptionAsDouble(MAbility.LIGHTNING_BLADE, "duration_per_level");
            return baseDuration + (durationPerLevel * (level - 1));
        } else {
            return plugin.getManaAbilityManager().getValue(mAbility, level);
        }
    }

    private @NotNull String getProgressToLevel(@NotNull Skill skill, @NotNull PlayerData playerData) {
        int skillLevel = playerData.getSkillLevel(skill);
        @Nullable Locale locale = playerData.getLocale();
        if (skillLevel < OptionL.getMaxLevel(skill)) {
            double currentXp = playerData.getSkillXp(skill);
            double xpToNext = plugin.getLeveler().getXpRequirements().getXpRequired(skill, skillLevel + 1);
            @Nullable String m = TextUtil.replace(Lang.getMessage(MenuMessage.PROGRESS_TO_LEVEL, locale)
                    ,"{level}", RomanNumber.toRoman(skillLevel + 1)
                    ,"{percent}", NumberUtil.format2(currentXp / xpToNext * 100)
                    ,"{current_xp}", NumberUtil.format2(currentXp)
                    ,"{level_xp}", String.valueOf((int) xpToNext));
            assert (null != m);
            return m;
        }
        return "";
    }

}
