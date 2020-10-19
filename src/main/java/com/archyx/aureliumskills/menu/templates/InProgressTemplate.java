package com.archyx.aureliumskills.menu.templates;

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
            displayName = Objects.requireNonNull(config.getString("display_name")).replace('&', '§');
            lore = new ArrayList<>();
            lorePlaceholders = new HashMap<>();
            int lineNum = 0;
            for (String line : config.getStringList("lore")) {
                Set<String> linePlaceholders = new HashSet<>();
                lore.add(line.replace('&', '§'));
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
            Bukkit.getLogger().warning("[AureliumSkills] Error parsing item " + TYPE.toString() + ", check error above for details!");
        }
    }

    public ItemStack getItem(Skill skill, PlayerSkill playerSkill, int level, Locale locale) {
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        ImmutableList<Supplier<Ability>> abilities = skill.getAbilities();
        if (meta != null) {
            meta.setDisplayName(displayName.replace("{level_in_progress}", Lang.getMessage(MenuMessage.LEVEL_IN_PROGRESS, locale).replace("{level}", RomanNumber.toRoman(level))));
            List<String> builtLore = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                Set<String> placeholders = lorePlaceholders.get(i);
                for (String placeholder : placeholders) {
                    switch (placeholder) {
                        case "level_number":
                            line = line.replace("{level_number}", Lang.getMessage(MenuMessage.LEVEL_NUMBER, locale).replace("{level}", String.valueOf(level)));
                            break;
                        case "rewards":
                            Stat primaryStat = skill.getPrimaryStat();
                            String rewards = Lang.getMessage(MenuMessage.REWARDS_ENTRY, locale)
                                    .replace("{color}", primaryStat.getColor(locale))
                                    .replace("{num}", String.valueOf(1))
                                    .replace("{symbol}", primaryStat.getSymbol(locale))
                                    .replace("{stat}", primaryStat.getDisplayName(locale));
                            // If level has secondary stat
                            if (level % 2 == 0) {
                                Stat secondaryStat = skill.getSecondaryStat();
                                rewards += Lang.getMessage(MenuMessage.REWARDS_ENTRY, locale)
                                        .replace("{color}", secondaryStat.getColor(locale))
                                        .replace("{num}", String.valueOf(1))
                                        .replace("{symbol}", secondaryStat.getSymbol(locale))
                                        .replace("{stat}", secondaryStat.getDisplayName(locale));
                            }
                            line = line.replace("{rewards}", Lang.getMessage(MenuMessage.REWARDS, locale).replace("{rewards}", rewards));
                            break;
                        case "ability":
                            if (abilities.size() == 5) {
                                Ability ability = abilities.get((level - 2) % 5).get();
                                // Unlock
                                if (level <= 6) {
                                    line = line.replace("{ability}", Lang.getMessage(MenuMessage.ABILITY_UNLOCK, locale)
                                            .replace("{ability}", ability.getDisplayName(locale))
                                            .replace("{desc}", ability.getDescription(locale)
                                                    .replace("{value_2}", nf1.format(ability.getValue2(1)))
                                                    .replace("{value}", nf1.format(ability.getValue(1)))));
                                }
                                // Level
                                else {
                                    int abilityLevel = (level + 3) / 5;
                                    line = line.replace("{ability}", Lang.getMessage(MenuMessage.ABILITY_LEVEL, locale)
                                            .replace("{ability}", ability.getDisplayName(locale))
                                            .replace("{level}", RomanNumber.toRoman(abilityLevel))
                                            .replace("{desc}", ability.getDescription(locale)
                                                    .replace("{value_2}", nf1.format(ability.getValue2(abilityLevel)))
                                                    .replace("{value}", nf1.format(ability.getValue(abilityLevel)))));
                                }
                            }
                            else {
                                line = line.replace("{ability}", "");
                            }
                            break;
                        case "mana_ability":
                            MAbility mAbility = skill.getManaAbility();
                            if (level % 7 == 0 && mAbility != MAbility.ABSORPTION) {
                                // Mana Ability Unlocked
                                if (level == 7) {
                                    line = line.replace("{mana_ability}", Lang.getMessage(MenuMessage.MANA_ABILITY_UNLOCK, locale)
                                            .replace("{mana_ability}", mAbility.getDisplayName(locale))
                                            .replace("{desc}", mAbility.getDescription(locale)
                                                    .replace("{value}", nf1.format(mAbility.getValue(1)))));
                                }
                                // Mana Ability Level
                                else {
                                    int manaAbilityLevel = level / 7;
                                    line = line.replace("{mana_ability}", Lang.getMessage(MenuMessage.MANA_ABILITY_LEVEL, locale)
                                            .replace("{mana_ability}", mAbility.getDisplayName(locale))
                                            .replace("{level}", RomanNumber.toRoman(manaAbilityLevel))
                                            .replace("{desc}", mAbility.getDescription(locale)
                                                    .replace("{value}", nf1.format(mAbility.getValue(manaAbilityLevel)))));
                                }
                            }
                            else {
                                line = line.replace("{mana_ability}", "");
                            }
                            break;
                        case "progress":
                            double currentXp = playerSkill.getXp(skill);
                            double xpToNext = Leveler.levelReqs.get(level - 2);
                            line = line.replace("{progress}", Lang.getMessage(MenuMessage.PROGRESS, locale)
                                    .replace("{percent}", nf2.format(currentXp / xpToNext * 100))
                                    .replace("{current_xp}", nf2.format(currentXp))
                                    .replace("{level_xp}", String.valueOf((int) xpToNext)));
                            break;
                        case "in_progress":
                            line = line.replace("{in_progress}", Lang.getMessage(MenuMessage.IN_PROGRESS, locale));
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
