package com.archyx.aureliumskills.menu.templates;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.configuration.Option;
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
import com.archyx.aureliumskills.util.item.ItemUtils;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.math.RomanNumber;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.google.common.collect.ImmutableList;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Supplier;

public class SkillInfoItem {

    private final AureliumSkills plugin;

    public SkillInfoItem(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    private List<String> applyPlaceholders(List<String> lore, Player player) {
        if (plugin.isPlaceholderAPIEnabled() && OptionL.getBoolean(Option.MENUS_PLACEHOLDER_API)) {
            List<String> appliedList = new ArrayList<>();
            for (String entry : lore) {
                appliedList.add(PlaceholderAPI.setPlaceholders(player, entry));
            }
            return appliedList;
        }
        return lore;
    }

    private String applyPlaceholders(String input, Player player) {
        if (plugin.isPlaceholderAPIEnabled() && OptionL.getBoolean(Option.MENUS_PLACEHOLDER_API)) {
            return PlaceholderAPI.setPlaceholders(player, input);
        }
        return input;
    }

    public ItemStack getItem(ItemStack item, Skill skill, PlayerData playerData, Locale locale, String displayName, List<String> lore, Map<Integer, Set<String>> lorePlaceholders, Player player) {
        ItemMeta meta = item.getItemMeta();
        int skillLevel = playerData.getSkillLevel(skill);
        if (meta != null) {
            meta.setDisplayName(applyPlaceholders(TextUtil.replace(displayName,"{skill}", skill.getDisplayName(locale),"{level}", RomanNumber.toRoman(skillLevel)), player));
            meta.setLore(ItemUtils.formatLore(applyPlaceholders(getLore(lore, lorePlaceholders, skill, playerData, locale), player)));
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
                        line = TextUtil.replace(line, "{skill_desc}", skill.getDescription(locale));
                        break;
                    case "stats_leveled":
                        ImmutableList<Stat> statsLeveled = plugin.getRewardManager().getRewardTable(skill).getStatsLeveled();
                        StringBuilder statList = new StringBuilder();
                        for (Stat stat : statsLeveled) {
                            statList.append(stat.getColor(locale)).append(stat.getDisplayName(locale)).append(ChatColor.GRAY).append(", ");
                        }
                        if (statList.length() > 1) {
                            statList.delete(statList.length() - 2, statList.length());
                        }
                        if (statsLeveled.size() > 0) {
                            line = TextUtil.replace(line, "{stats_leveled}", TextUtil.replace(Lang.getMessage(MenuMessage.STATS_LEVELED, locale),
                                    "{stats}", statList.toString()));
                        } else {
                            line = TextUtil.replace(line, "{stats_leveled}", "");
                        }
                    case "ability_levels":
                        line = TextUtil.replace(line, "{ability_levels}", getAbilityLevelsLore(skill, playerData, locale));
                        break;
                    case "mana_ability":
                        line = TextUtil.replace(line, "{mana_ability}", getManaAbilityLore(skill, playerData, locale));
                        break;
                    case "level":
                        line = TextUtil.replace(line,"{level}", TextUtil.replace(Lang.getMessage(MenuMessage.LEVEL, locale),"{level}", RomanNumber.toRoman(skillLevel)));
                        break;
                    case "progress_to_level":
                        if (skillLevel < OptionL.getMaxLevel(skill)) {
                            double currentXp = playerData.getSkillXp(skill);
                            double xpToNext = plugin.getLeveler().getXpRequirements().getXpRequired(skill, skillLevel + 1);
                            line = TextUtil.replace(line,"{progress_to_level}", TextUtil.replace(Lang.getMessage(MenuMessage.PROGRESS_TO_LEVEL, locale)
                                    ,"{level}", RomanNumber.toRoman(skillLevel + 1)
                                    ,"{percent}", NumberUtil.format2(currentXp / xpToNext * 100)
                                    ,"{current_xp}", NumberUtil.format2(currentXp)
                                    ,"{level_xp}", String.valueOf((int) xpToNext)));
                        } else {
                            line = TextUtil.replace(line,"{progress_to_level}", "");
                        }
                        break;
                    case "max_level":
                        if (skillLevel >= OptionL.getMaxLevel(skill)) {
                            line = TextUtil.replace(line,"{max_level}", Lang.getMessage(MenuMessage.MAX_LEVEL, locale));
                        } else {
                            line = TextUtil.replace(line,"{max_level}", "");
                        }
                        break;
                    case "skill_click":
                        line = TextUtil.replace(line,"{skill_click}", Lang.getMessage(MenuMessage.SKILL_CLICK, locale));
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

    private String getManaAbilityLore(Skill skill, PlayerData playerData, Locale locale) {
        StringBuilder manaAbilityLore = new StringBuilder();
        MAbility mAbility = skill.getManaAbility();
        if (mAbility != null) {
            int level = playerData.getManaAbilityLevel(mAbility);
            if (level > 0 && plugin.getAbilityManager().isEnabled(mAbility)) {
                ManaAbilityManager manager = plugin.getManaAbilityManager();
                manaAbilityLore.append(TextUtil.replace(Lang.getMessage(getManaAbilityMessage(mAbility), locale)
                        , "{mana_ability}", mAbility.getDisplayName(locale)
                        , "{level}", RomanNumber.toRoman(level)
                        , "{duration}", NumberUtil.format1(getDuration(mAbility, level))
                        , "{value}", NumberUtil.format1(manager.getDisplayValue(mAbility, level))
                        , "{mana_cost}", NumberUtil.format1(manager.getManaCost(mAbility, level))
                        , "{cooldown}", NumberUtil.format1(manager.getCooldown(mAbility, level))));

            }
        }
        return manaAbilityLore.toString();
    }

    private double getDuration(MAbility mAbility, int level) {
        if (mAbility == MAbility.LIGHTNING_BLADE) {
            double baseDuration = plugin.getManaAbilityManager().getOptionAsDouble(MAbility.LIGHTNING_BLADE, "base_duration");
            double durationPerLevel = plugin.getManaAbilityManager().getOptionAsDouble(MAbility.LIGHTNING_BLADE, "duration_per_level");
            return baseDuration + (durationPerLevel * (level - 1));
        } else {
            return plugin.getManaAbilityManager().getValue(mAbility, level);
        }
    }

    private MessageKey getManaAbilityMessage(MAbility mAbility) {
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

}
