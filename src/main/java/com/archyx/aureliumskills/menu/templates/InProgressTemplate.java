package com.archyx.aureliumskills.menu.templates;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.menu.MenuLoader;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.levelers.Leveler;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.ItemUtils;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.RomanNumber;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Supplier;

public class InProgressTemplate implements ConfigurableTemplate {

    private final TemplateType TYPE = TemplateType.IN_PROGRESS;

    private ItemStack baseItem;
    private String displayName;
    private List<String> lore;
    private Map<Integer, Set<String>> lorePlaceholders;
    private final String[] definedPlaceholders = new String[] {"level_number", "rewards", "ability", "mana_ability", "progress", "in_progress"};
    private final NumberFormat nf1 = new DecimalFormat("#.#");
    private final NumberFormat nf2 = new DecimalFormat("#.##");

    @Override
    public TemplateType getType() {
        return TYPE;
    }

    @Override
    public void load(ConfigurationSection config) {
        try {
            baseItem = MenuLoader.parseItem(Objects.requireNonNull(config.getString("material")));
            displayName = LoreUtil.replace(Objects.requireNonNull(config.getString("display_name")),"&", "§");
            lore = new ArrayList<>();
            lorePlaceholders = new HashMap<>();
            int lineNum = 0;
            for (String line : config.getStringList("lore")) {
                Set<String> linePlaceholders = new HashSet<>();
                lore.add(LoreUtil.replace(line,"&", "§"));
                // Find lore placeholders
                for (String placeholder : definedPlaceholders) {
                    if (line.contains("{" + placeholder + "}")) {
                        linePlaceholders.add(placeholder);
                    }
                }
                lorePlaceholders.put(lineNum, linePlaceholders);
                lineNum++;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("[AureliumSkills] Error parsing template " + TYPE.toString() + ", check error above for details!");
        }
    }

    public ItemStack getItem(Skill skill, PlayerSkill playerSkill, int level, Locale locale, Map<Ability, String> abilityNames, Map<Ability, String> abilityDescriptions) {
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        ImmutableList<Supplier<Ability>> abilities = skill.getAbilities();
        if (meta != null) {
            meta.setDisplayName(LoreUtil.replace(displayName,"{level_in_progress}", LoreUtil.replace(Lang.getMessage(MenuMessage.LEVEL_IN_PROGRESS, locale),"{level}", RomanNumber.toRoman(level))));
            List<String> builtLore = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                Set<String> placeholders = lorePlaceholders.get(i);
                for (String placeholder : placeholders) {
                    switch (placeholder) {
                        case "level_number":
                            line = LoreUtil.replace(line,"{level_number}", LoreUtil.replace(Lang.getMessage(MenuMessage.LEVEL_NUMBER, locale),"{level}", String.valueOf(level)));
                            break;
                        case "rewards":
                            Stat primaryStat = skill.getPrimaryStat();
                            String rewards = LoreUtil.replace(Lang.getMessage(MenuMessage.REWARDS_ENTRY, locale)
                                    ,"{color}", primaryStat.getColor(locale)
                                    ,"{num}", String.valueOf(1)
                                    ,"{symbol}", primaryStat.getSymbol(locale)
                                    ,"{stat}", primaryStat.getDisplayName(locale));
                            // If level has secondary stat
                            if (level % 2 == 0) {
                                Stat secondaryStat = skill.getSecondaryStat();
                                rewards += LoreUtil.replace(Lang.getMessage(MenuMessage.REWARDS_ENTRY, locale)
                                        ,"{color}", secondaryStat.getColor(locale)
                                        ,"{num}", String.valueOf(1)
                                        ,"{symbol}", secondaryStat.getSymbol(locale)
                                        ,"{stat}", secondaryStat.getDisplayName(locale));
                            }
                            line = LoreUtil.replace(line,"{rewards}", LoreUtil.replace(Lang.getMessage(MenuMessage.REWARDS, locale),"{rewards}", rewards));
                            break;
                        case "ability":
                            if (abilities.size() == 5) {
                                StringBuilder abilityLore = new StringBuilder();
                                for (Ability ability : Ability.getAbilities(skill, level)) {
                                    if (AureliumSkills.abilityManager.isEnabled(ability)) {
                                        if (level == ability.getUnlock()) {
                                            abilityLore.append(LoreUtil.replace(Lang.getMessage(MenuMessage.ABILITY_UNLOCK, locale)
                                                    , "{ability}", abilityNames.get(ability)
                                                    , "{desc}", LoreUtil.replace(abilityDescriptions.get(ability)
                                                            , "{value_2}", nf1.format(ability.getValue2(1))
                                                            , "{value}", nf1.format(ability.getValue(1)))));
                                        } else {
                                            int abilityLevel = ((level - ability.getUnlock()) / ability.getLevelUp()) + 1;
                                            if (abilityLevel <= ability.getMaxLevel() || ability.getMaxLevel() == 0) { // Check max level
                                                abilityLore.append(LoreUtil.replace(Lang.getMessage(MenuMessage.ABILITY_LEVEL, locale)
                                                        , "{ability}", abilityNames.get(ability)
                                                        , "{level}", RomanNumber.toRoman(abilityLevel)
                                                        , "{desc}", LoreUtil.replace(abilityDescriptions.get(ability)
                                                                , "{value_2}", nf1.format(ability.getValue2(abilityLevel))
                                                                , "{value}", nf1.format(ability.getValue(abilityLevel)))));
                                            }
                                        }
                                    }
                                }
                                line = LoreUtil.replace(line, "{ability}", abilityLore.toString());
                            }
                            else {
                                line = LoreUtil.replace(line,"{ability}", "");
                            }
                            break;
                        case "mana_ability":
                            MAbility mAbility = skill.getManaAbility();
                            if (level % 7 == 0 && mAbility != MAbility.ABSORPTION && AureliumSkills.abilityManager.isEnabled(mAbility)) {
                                // Mana Ability Unlocked
                                if (level == 7) {
                                    line = LoreUtil.replace(line,"{mana_ability}", LoreUtil.replace(Lang.getMessage(MenuMessage.MANA_ABILITY_UNLOCK, locale)
                                            ,"{mana_ability}", mAbility.getDisplayName(locale)
                                            ,"{desc}", LoreUtil.replace(mAbility.getDescription(locale)
                                                    ,"{value}", nf1.format(mAbility.getDisplayValue(1)))));
                                }
                                // Mana Ability Level
                                else {
                                    int manaAbilityLevel = level / 7;
                                    line = LoreUtil.replace(line,"{mana_ability}", LoreUtil.replace(Lang.getMessage(MenuMessage.MANA_ABILITY_LEVEL, locale)
                                            ,"{mana_ability}", mAbility.getDisplayName(locale)
                                            ,"{level}", RomanNumber.toRoman(manaAbilityLevel)
                                            ,"{desc}", LoreUtil.replace(mAbility.getDescription(locale)
                                                    ,"{value}", nf1.format(mAbility.getDisplayValue(manaAbilityLevel)))));
                                }
                            }
                            else {
                                line = LoreUtil.replace(line,"{mana_ability}", "");
                            }
                            break;
                        case "progress":
                            double currentXp = playerSkill.getXp(skill);
                            double xpToNext = Leveler.levelReqs.get(level - 2);
                            line = LoreUtil.replace(line,"{progress}", LoreUtil.replace(Lang.getMessage(MenuMessage.PROGRESS, locale)
                                    ,"{percent}", nf2.format(currentXp / xpToNext * 100)
                                    ,"{current_xp}", nf2.format(currentXp)
                                    ,"{level_xp}", String.valueOf((int) xpToNext)));
                            break;
                        case "in_progress":
                            line = LoreUtil.replace(line,"{in_progress}", Lang.getMessage(MenuMessage.IN_PROGRESS, locale));
                            break;
                    }
                }
                builtLore.add(line);
            }
            meta.setLore(ItemUtils.formatLore(builtLore));
            item.setItemMeta(meta);
        }
        return item;
    }

}
