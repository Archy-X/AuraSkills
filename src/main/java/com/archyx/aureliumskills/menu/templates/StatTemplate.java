package com.archyx.aureliumskills.menu.templates;

import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menu.MenuLoader;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.ItemUtils;
import com.archyx.aureliumskills.util.LoreUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Supplier;

public class StatTemplate implements ConfigurableTemplate {

    private final TemplateType TYPE = TemplateType.STAT;

    private final Map<Stat, SlotPos> positions = new HashMap<>();
    private final Map<Stat, ItemStack> baseItems = new HashMap<>();
    private String displayName;
    private List<String> lore;
    private Map<Integer, Set<String>> lorePlaceholders;
    private final String[] definedPlaceholders = new String[] {"stat_desc", "primary_skills_two", "primary_skills_three", "secondary_skills_two", "secondary_skills_three", "your_level", "descriptors"};
    private final NumberFormat nf = new DecimalFormat("##.##");

    @Override
    public TemplateType getType() {
        return TYPE;
    }

    @Override
    public void load(ConfigurationSection config) {
        try {
            for (String posInput : config.getStringList("pos")) {
                String[] splitInput = posInput.split(" ");
                Stat stat = Stat.valueOf(splitInput[0]);
                int row = Integer.parseInt(splitInput[1]);
                int column = Integer.parseInt(splitInput[2]);
                positions.put(stat, SlotPos.of(row, column));
            }
            // Load base items
            for (String materialInput : config.getStringList("material")) {
                String[] splitInput = materialInput.split(" ", 2);
                Stat stat = Stat.valueOf(splitInput[0]);
                baseItems.put(stat, MenuLoader.parseItem(splitInput[1]));
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
            Bukkit.getLogger().warning("[AureliumSkills] Error parsing template " + TYPE.toString() + ", check error above for details!");
        }
    }

    public ItemStack getItem(Stat stat, PlayerStat playerStat, Locale locale) {
        ItemStack item = baseItems.get(stat).clone();
        ItemMeta meta = item.getItemMeta();
        Supplier<Skill>[] primarySkills = stat.getPrimarySkills();
        Supplier<Skill>[] secondarySkills = stat.getSecondarySkills();
        if (meta != null) {
            meta.setDisplayName(LoreUtil.replace(displayName,"{color}", stat.getColor(locale),"{stat}", stat.getDisplayName(locale)));
            List<String> builtLore = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                Set<String> placeholders = lorePlaceholders.get(i);
                for (String placeholder : placeholders) {
                    switch (placeholder) {
                        case "stat_desc":
                            line = LoreUtil.replace(line,"{stat_desc}", stat.getDescription(locale));
                            break;
                        case "primary_skills_two":
                            if (primarySkills.length == 2) {
                                line = LoreUtil.replace(line,"{primary_skills_two}", LoreUtil.replace(Lang.getMessage(MenuMessage.PRIMARY_SKILLS_TWO, locale)
                                        ,"{skill_1}", primarySkills[0].get().getDisplayName(locale)
                                        ,"{skill_2}", primarySkills[1].get().getDisplayName(locale)));
                            }
                            else {
                                line = LoreUtil.replace(line,"{primary_skills_two}", "");
                            }
                            break;
                        case "primary_skills_three":
                            if (primarySkills.length == 3) {
                                line = LoreUtil.replace(line,"{primary_skills_three}", LoreUtil.replace(Lang.getMessage(MenuMessage.PRIMARY_SKILLS_THREE, locale)
                                        ,"{skill_1}", primarySkills[0].get().getDisplayName(locale)
                                        ,"{skill_2}", primarySkills[1].get().getDisplayName(locale)
                                        ,"{skill_3}", primarySkills[2].get().getDisplayName(locale)));
                            }
                            else {
                                line = LoreUtil.replace(line,"{primary_skills_three}", "");
                            }
                            break;
                        case "secondary_skills_two":
                            if (secondarySkills.length == 2) {
                                line = LoreUtil.replace(line,"{secondary_skills_two}", LoreUtil.replace(Lang.getMessage(MenuMessage.SECONDARY_SKILLS_TWO, locale)
                                        ,"{skill_1}", secondarySkills[0].get().getDisplayName(locale)
                                        ,"{skill_2}", secondarySkills[1].get().getDisplayName(locale)));
                            }
                            else {
                                line = LoreUtil.replace(line,"{secondary_skills_two}", "");
                            }
                            break;
                        case "secondary_skills_three":
                            if (secondarySkills.length == 3) {
                                line = LoreUtil.replace(line,"{secondary_skills_three}", LoreUtil.replace(Lang.getMessage(MenuMessage.SECONDARY_SKILLS_THREE, locale)
                                        ,"{skill_1}", secondarySkills[0].get().getDisplayName(locale)
                                        ,"{skill_2}", secondarySkills[1].get().getDisplayName(locale)
                                        ,"{skill_3}", secondarySkills[2].get().getDisplayName(locale)));
                            }
                            else {
                                line = LoreUtil.replace(line,"{secondary_skills_three}", "");
                            }
                            break;
                        case "your_level":
                            line = LoreUtil.replace(line,"{your_level}", LoreUtil.replace(Lang.getMessage(MenuMessage.YOUR_LEVEL, locale)
                                    ,"{color}", stat.getColor(locale)
                                    ,"{level}", String.valueOf(playerStat.getStatLevel(stat))));
                            break;
                        case "descriptors":
                            switch (stat) {
                                case STRENGTH:
                                    double strengthLevel = playerStat.getStatLevel(Stat.STRENGTH);
                                    double attackDamage = strengthLevel * OptionL.getDouble(Option.STRENGTH_MODIFIER);
                                    if (OptionL.getBoolean(Option.STRENGTH_DISPLAY_DAMAGE_WITH_HEALTH_SCALING)) {
                                        attackDamage *= OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
                                    }
                                    line = LoreUtil.replace(line,"{descriptors}", LoreUtil.replace(Lang.getMessage(MenuMessage.ATTACK_DAMAGE, locale)
                                            ,"{value}", nf.format(attackDamage)));
                                    break;
                                case HEALTH:
                                    double modifier = ((double) playerStat.getStatLevel(Stat.HEALTH)) * OptionL.getDouble(Option.HEALTH_MODIFIER);
                                    double scaledHealth = modifier * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
                                    line = LoreUtil.replace(line,"{descriptors}", LoreUtil.replace(Lang.getMessage(MenuMessage.HP, locale)
                                            ,"{value}", nf.format(scaledHealth)));
                                    break;
                                case REGENERATION:
                                    int regenLevel = playerStat.getStatLevel(Stat.REGENERATION);
                                    double saturatedRegen = regenLevel * OptionL.getDouble(Option.REGENERATION_SATURATED_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
                                    double hungerFullRegen = regenLevel *  OptionL.getDouble(Option.REGENERATION_HUNGER_FULL_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
                                    double almostFullRegen = regenLevel *  OptionL.getDouble(Option.REGENERATION_HUNGER_ALMOST_FULL_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
                                    double manaRegen = regenLevel * OptionL.getDouble(Option.REGENERATION_MANA_MODIFIER);
                                    line = LoreUtil.replace(line,"{descriptors}", LoreUtil.replace(Lang.getMessage(MenuMessage.SATURATED_REGEN, locale),"{value}", nf.format(saturatedRegen))
                                            + "\n" + LoreUtil.replace(Lang.getMessage(MenuMessage.FULL_HUNGER_REGEN, locale),"{value}", nf.format(hungerFullRegen))
                                            + "\n" + LoreUtil.replace(Lang.getMessage(MenuMessage.ALMOST_FULL_HUNGER_REGEN, locale),"{value}", nf.format(almostFullRegen))
                                            + "\n" + LoreUtil.replace(Lang.getMessage(MenuMessage.MANA_REGEN, locale),"{value}", String.valueOf((int) manaRegen)));
                                    break;
                                case LUCK:
                                    int luckLevel = playerStat.getStatLevel(Stat.LUCK);
                                    double luck = luckLevel * OptionL.getDouble(Option.LUCK_MODIFIER);
                                    double doubleDropChance = (double) luckLevel * OptionL.getDouble(Option.LUCK_DOUBLE_DROP_MODIFIER) * 100;
                                    if (doubleDropChance > OptionL.getDouble(Option.LUCK_DOUBLE_DROP_PERCENT_MAX)) {
                                        doubleDropChance = OptionL.getDouble(Option.LUCK_DOUBLE_DROP_PERCENT_MAX);
                                    }
                                    line = LoreUtil.replace(line,"{descriptors}", LoreUtil.replace(Lang.getMessage(MenuMessage.LUCK, locale),"{value}", nf.format(luck))
                                            + "\n" + LoreUtil.replace(Lang.getMessage(MenuMessage.DOUBLE_DROP_CHANCE, locale),"{value}", nf.format(doubleDropChance)));
                                    break;
                                case WISDOM:
                                    int wisdomLevel = playerStat.getStatLevel(Stat.WISDOM);
                                    double xpModifier = wisdomLevel * OptionL.getDouble(Option.WISDOM_EXPERIENCE_MODIFIER) * 100;
                                    int anvilCostReduction = (int) (wisdomLevel * OptionL.getDouble(Option.WISDOM_ANVIL_COST_MODIFIER));
                                    line = LoreUtil.replace(line,"{descriptors}", LoreUtil.replace(Lang.getMessage(MenuMessage.XP_GAIN, locale),"{value}", nf.format(xpModifier))
                                            + "\n" + LoreUtil.replace(Lang.getMessage(MenuMessage.ANVIL_COST_REDUCTION, locale),"{value}", String.valueOf(anvilCostReduction)));
                                    break;
                                case TOUGHNESS:
                                    double toughness = playerStat.getStatLevel(Stat.TOUGHNESS) * OptionL.getDouble(Option.TOUGHNESS_NEW_MODIFIER);
                                    double damageReduction = (-1.0 * Math.pow(1.01, -1.0 * toughness) + 1) * 100;
                                    line = LoreUtil.replace(line,"{descriptors}", LoreUtil.replace(Lang.getMessage(MenuMessage.INCOMING_DAMAGE, locale),"{value}", nf.format(damageReduction)));
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

    public SlotPos getPos(Stat stat) {
        return positions.get(stat);
    }

}
