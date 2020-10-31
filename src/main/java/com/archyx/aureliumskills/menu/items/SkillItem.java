package com.archyx.aureliumskills.menu.items;

import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menu.MenuLoader;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.MAbility;
import com.archyx.aureliumskills.skills.levelers.Leveler;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.ItemUtils;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.RomanNumber;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Supplier;

public class SkillItem implements ConfigurableItem {

    private final ItemType TYPE = ItemType.SKILL;

    private SlotPos pos;
    private final Map<Skill, ItemStack> baseItems = new HashMap<>();
    private String displayName;
    private List<String> lore;
    private Map<Integer, Set<String>> lorePlaceholders;
    private final String[] definedPlaceholders = new String[] {"skill_desc", "primary_stat", "secondary_stat", "ability_levels", "mana_ability", "level", "progress_to_level", "max_level"};
    private final NumberFormat nf = new DecimalFormat("#.#");
    private final NumberFormat nf2 = new DecimalFormat("#.##");

    @Override
    public ItemType getType() {
        return TYPE;
    }

    @Override
    public void load(ConfigurationSection config) {
        try {
            pos = SlotPos.of(config.getInt("row"), config.getInt("column"));
            // Load base items
            for (String materialInput : config.getStringList("material")) {
                String[] splitInput = materialInput.split(" ", 2);
                Skill skill = Skill.valueOf(splitInput[0]);
                baseItems.put(skill, MenuLoader.parseItem(splitInput[1]));
            }
            displayName = LoreUtil.replace(Objects.requireNonNull(config.getString("display_name")),"&", "§");
            // Load lore
            List<String> lore = new ArrayList<>();
            Map<Integer, Set<String>> lorePlaceholders = new HashMap<>();
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
            this.lore = lore;
            this.lorePlaceholders = lorePlaceholders;
        }
        catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("[AureliumSkills] Error parsing item " + TYPE.toString() + ", check error above for details!");
        }
    }

    public ItemStack getItem(Skill skill, PlayerSkill playerSkill, Locale locale) {
        ItemStack item = baseItems.get(skill);
        ItemMeta meta = item.getItemMeta();
        int skillLevel = playerSkill.getSkillLevel(skill);
        if (meta != null) {
            meta.setDisplayName(LoreUtil.replace(displayName,"{skill}", skill.getDisplayName(locale),"{level}", RomanNumber.toRoman(skillLevel)));
            List<String> builtLore = new ArrayList<>();
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
                            if (skill.getAbilities().size() == 5) {
                                line = LoreUtil.setPlaceholders("ability_levels", MenuMessage.ABILITY_LEVELS, locale, line);
                                int num = 1;
                                for (Supplier<Ability> abilitySupplier : skill.getAbilities()) {
                                    Ability ability = abilitySupplier.get();
                                    if (playerSkill.getAbilityLevel(ability) > 0) {
                                        int abilityLevel = playerSkill.getAbilityLevel(ability);
                                        line = LoreUtil.replace(line,"{ability_" + num + "}", LoreUtil.replace(Lang.getMessage(MenuMessage.ABILITY_LEVEL_ENTRY, locale)
                                                ,"{ability}", ability.getDisplayName(locale)
                                                ,"{level}", RomanNumber.toRoman(playerSkill.getAbilityLevel(ability))
                                                ,"{info}", LoreUtil.replace(ability.getInfo(locale)
                                                        ,"{value}", nf.format(ability.getValue(abilityLevel))
                                                        ,"{value_2}", nf.format(ability.getValue2(abilityLevel)))));
                                    }
                                    else {
                                        line = LoreUtil.replace(line,"{ability_" + num + "}", LoreUtil.replace(Lang.getMessage(MenuMessage.ABILITY_LEVEL_ENTRY_LOCKED, locale)
                                                ,"{ability}", ability.getDisplayName(locale)));
                                    }
                                    num++;
                                }
                            }
                            else {
                                line = LoreUtil.replace(line,"{ability_levels}", "");
                            }
                            break;
                        case "mana_ability":
                            MAbility mAbility = skill.getManaAbility();
                            int level = playerSkill.getManaAbilityLevel(mAbility);
                            if (mAbility != MAbility.ABSORPTION && level > 0) {
                                line = LoreUtil.replace(line,"{mana_ability}", LoreUtil.replace(Lang.getMessage(MenuMessage.MANA_ABILITY, locale)
                                        ,"{mana_ability}", mAbility.getDisplayName(locale)
                                        ,"{level}", RomanNumber.toRoman(level)
                                        ,"{duration}", nf.format(mAbility.getValue(level))
                                        ,"{mana_cost}", String.valueOf(mAbility.getManaCost(level))
                                        ,"{cooldown}", nf.format(mAbility.getCooldown(level))));
                            }
                            else {
                                line = LoreUtil.replace(line,"{mana_ability}", "");
                            }
                            break;
                        case "level":
                            line = LoreUtil.replace(line,"{level}", LoreUtil.replace(Lang.getMessage(MenuMessage.LEVEL, locale),"{level}", RomanNumber.toRoman(skillLevel)));
                            break;
                        case "progress_to_level":
                            if (skillLevel < OptionL.getMaxLevel(skill)) {
                                double currentXp = playerSkill.getXp(skill);
                                double xpToNext = Leveler.levelReqs.get(skillLevel - 1);
                                line = LoreUtil.replace(line,"{progress_to_level}", LoreUtil.replace(Lang.getMessage(MenuMessage.PROGRESS_TO_LEVEL, locale)
                                        ,"{level}", RomanNumber.toRoman(skillLevel + 1)
                                        ,"{percent}", nf2.format(currentXp / xpToNext * 100)
                                        ,"{current_xp}", nf2.format(currentXp)
                                        ,"{level_xp}", String.valueOf((int) xpToNext)));
                            }
                            else {
                                line = LoreUtil.replace(line,"{progress_to_level}", "");
                            }
                            break;
                        case "max_level":
                            if (skillLevel >= OptionL.getMaxLevel(skill)) {
                                line = LoreUtil.replace(line,"{max_level}", Lang.getMessage(MenuMessage.MAX_LEVEL, locale));
                            }
                            else {
                                line = LoreUtil.replace(line,"{max_level}", "");
                            }
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

    @Override
    public SlotPos getPos() {
        return pos;
    }
}
