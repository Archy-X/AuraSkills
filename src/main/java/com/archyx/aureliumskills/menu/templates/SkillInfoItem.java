package com.archyx.aureliumskills.menu.templates;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
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
import com.archyx.aureliumskills.util.ItemUtils;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.NumberUtil;
import com.archyx.aureliumskills.util.RomanNumber;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Supplier;

public abstract class SkillInfoItem {

    private final AureliumSkills plugin;

    public SkillInfoItem(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public ItemStack getItem(ItemStack item, Skill skill, PlayerData playerData, Locale locale, String displayName, List<String> lore, Map<Integer, Set<String>> lorePlaceholders) {
        ItemMeta meta = item.getItemMeta();
        int skillLevel = playerData.getSkillLevel(skill);
        if (meta != null) {
            meta.setDisplayName(LoreUtil.replace(displayName,"{skill}", skill.getDisplayName(locale),"{level}", RomanNumber.toRoman(skillLevel)));
            meta.setLore(ItemUtils.formatLore(getLore(lore, lorePlaceholders, skill, playerData, locale)));
            item.setItemMeta(meta);
        }
        return item;
    }

    private List<String> getLore(List<String> lore, Map<Integer, Set<String>> lorePlaceholders, Skill skill, PlayerData playerData, Locale locale) {
        List<String> builtLore = new ArrayList<>();
        int skillLevel = playerData.getSkillLevel(skill);
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            Set<String> placeholders = lorePlaceholders.get(i);
            for (String placeholder : placeholders) {
                switch (placeholder) {
                    case "skill_desc":
                        line = LoreUtil.setPlaceholders("skill_desc", skill.getDescription(locale), line);
                        break;
                    case "primary_stat":
                        Stat primaryStat = skill.getPrimaryStat();
                        line = LoreUtil.replace(line,"{primary_stat}", LoreUtil.replace(Lang.getMessage(MenuMessage.PRIMARY_STAT, locale)
                                ,"{color}", primaryStat.getColor(locale)
                                ,"{stat}", primaryStat.getDisplayName(locale)));
                        break;
                    case "secondary_stat":
                        Stat secondaryStat = skill.getSecondaryStat();
                        line = LoreUtil.replace(line,"{secondary_stat}", LoreUtil.replace(Lang.getMessage(MenuMessage.SECONDARY_STAT, locale)
                                ,"{color}", secondaryStat.getColor(locale)
                                ,"{stat}", secondaryStat.getDisplayName(locale)));
                        break;
                    case "ability_levels":
                        line = LoreUtil.replace(line, "{ability_levels}", getAbilityLevelsLore(skill, playerData, locale));
                        break;
                    case "mana_ability":
                        line = LoreUtil.replace(line, "{mana_ability}", getManaAbilityLore(skill, playerData, locale));
                        break;
                    case "level":
                        line = LoreUtil.replace(line,"{level}", LoreUtil.replace(Lang.getMessage(MenuMessage.LEVEL, locale),"{level}", RomanNumber.toRoman(skillLevel)));
                        break;
                    case "progress_to_level":
                        if (skillLevel < OptionL.getMaxLevel(skill)) {
                            double currentXp = playerData.getSkillXp(skill);
                            double xpToNext = plugin.getLeveler().getLevelRequirements().get(skillLevel - 1);
                            line = LoreUtil.replace(line,"{progress_to_level}", LoreUtil.replace(Lang.getMessage(MenuMessage.PROGRESS_TO_LEVEL, locale)
                                    ,"{level}", RomanNumber.toRoman(skillLevel + 1)
                                    ,"{percent}", NumberUtil.format2(currentXp / xpToNext * 100)
                                    ,"{current_xp}", NumberUtil.format2(currentXp)
                                    ,"{level_xp}", String.valueOf((int) xpToNext)));
                        } else {
                            line = LoreUtil.replace(line,"{progress_to_level}", "");
                        }
                        break;
                    case "max_level":
                        if (skillLevel >= OptionL.getMaxLevel(skill)) {
                            line = LoreUtil.replace(line,"{max_level}", Lang.getMessage(MenuMessage.MAX_LEVEL, locale));
                        } else {
                            line = LoreUtil.replace(line,"{max_level}", "");
                        }
                        break;
                    case "skill_click":
                        line = LoreUtil.replace(line,"{skill_click}", Lang.getMessage(MenuMessage.SKILL_CLICK, locale));
                }
            }
            builtLore.add(line);
        }
        return builtLore;
    }

    private String getAbilityLevelsLore(Skill skill, PlayerData playerData, Locale locale) {
        StringBuilder abilityLevelsLore = new StringBuilder();
        if (skill.getAbilities().size() == 5) {
            String levelsMessage = Lang.getMessage(MenuMessage.ABILITY_LEVELS, locale);
            int num = 1;
            List<Ability> abilities = new ArrayList<>();
            for (Supplier<Ability> abilitySupplier : skill.getAbilities()) {
                abilities.add(abilitySupplier.get());
            }
            abilities.sort(Comparator.comparingInt(a -> plugin.getAbilityManager().getUnlock(a)));
            for (Ability ability : abilities) {
                if (plugin.getAbilityManager().isEnabled(ability)) {
                    if (playerData.getAbilityLevel(ability) > 0) {
                        int abilityLevel = playerData.getAbilityLevel(ability);
                        levelsMessage = LoreUtil.replace(levelsMessage, "{ability_" + num + "}", LoreUtil.replace(Lang.getMessage(MenuMessage.ABILITY_LEVEL_ENTRY, locale)
                                , "{ability}", ability.getDisplayName(locale)
                                , "{level}", RomanNumber.toRoman(playerData.getAbilityLevel(ability))
                                , "{info}", LoreUtil.replace(ability.getInfo(locale)
                                        , "{value}", NumberUtil.format1(plugin.getAbilityManager().getValue(ability, abilityLevel))
                                        , "{value_2}", NumberUtil.format1(plugin.getAbilityManager().getValue2(ability, abilityLevel)))));
                    } else {
                        levelsMessage = LoreUtil.replace(levelsMessage, "{ability_" + num + "}", LoreUtil.replace(Lang.getMessage(MenuMessage.ABILITY_LEVEL_ENTRY_LOCKED, locale)
                                , "{ability}", ability.getDisplayName(locale)));
                    }
                } else {
                    levelsMessage = LoreUtil.replace(levelsMessage, "\\n  {ability_" + num + "}", ""
                            , "{ability_" + num + "}", "");
                }
                num++;
            }
            abilityLevelsLore.append(levelsMessage);
        }
        return abilityLevelsLore.toString();
    }

    private String getManaAbilityLore(Skill skill, PlayerData playerData, Locale locale) {
        StringBuilder manaAbilityLore = new StringBuilder();
        MAbility mAbility = skill.getManaAbility();
        if (mAbility != null) {
            int level = playerData.getManaAbilityLevel(mAbility);
            if (level > 0 && plugin.getAbilityManager().isEnabled(mAbility)) {
                ManaAbilityManager manager = plugin.getManaAbilityManager();
                manaAbilityLore.append(LoreUtil.replace(Lang.getMessage(getManaAbilityMessage(mAbility), locale)
                        , "{mana_ability}", mAbility.getDisplayName(locale)
                        , "{level}", RomanNumber.toRoman(level)
                        , "{duration}", NumberUtil.format1(manager.getValue(mAbility, level))
                        , "{value}", NumberUtil.format1(manager.getValue(mAbility, level))
                        , "{mana_cost}", NumberUtil.format1(manager.getManaCost(mAbility, level))
                        , "{cooldown}", NumberUtil.format1(manager.getCooldown(mAbility, level))));

            }
        }
        return manaAbilityLore.toString();
    }

    private MessageKey getManaAbilityMessage(MAbility mAbility) {
        switch (mAbility) {
            case SHARP_HOOK:
                return ManaAbilityMessage.SHARP_HOOK_MENU;
            case CHARGED_SHOT:
                return ManaAbilityMessage.CHARGED_SHOT_MENU;
            default:
                return MenuMessage.MANA_ABILITY;
        }
    }

}
