package com.archyx.aureliumskills.menu.templates;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menu.MenuLoader;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.stats.Stats;
import com.archyx.aureliumskills.util.item.ItemUtils;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class StatTemplate extends ConfigurableTemplate {

    private final Map<Stat, SlotPos> positions = new HashMap<>();
    private final Map<Stat, ItemStack> baseItems = new HashMap<>();

    public StatTemplate(AureliumSkills plugin) {
        super(plugin, TemplateType.STAT, new String[] {"stat_desc", "skills", "your_level", "descriptors"});
    }

    @Override
    public void load(ConfigurationSection config) {
        try {
            for (String posInput : config.getStringList("pos")) {
                String[] splitInput = posInput.split(" ");
                Stat stat = plugin.getStatRegistry().getStat(splitInput[0]);
                if (stat != null) {
                    int row = Integer.parseInt(splitInput[1]);
                    int column = Integer.parseInt(splitInput[2]);
                    positions.put(stat, SlotPos.of(row, column));
                }
            }
            // Load base items
            for (String materialInput : config.getStringList("material")) {
                String[] splitInput = materialInput.split(" ", 2);
                Stat stat = plugin.getStatRegistry().getStat(splitInput[0]);
                if (stat != null) {
                    baseItems.put(stat, MenuLoader.parseItem(splitInput[1]));
                }
            }
            displayName = TextUtil.replace(Objects.requireNonNull(config.getString("display_name")),"&", "§");
            // Load lore
            List<String> lore = new ArrayList<>();
            Map<Integer, Set<String>> lorePlaceholders = new HashMap<>();
            int lineNum = 0;
            for (String line : config.getStringList("lore")) {
                Set<String> linePlaceholders = new HashSet<>();
                lore.add(TextUtil.replace(line,"&", "§"));
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
            Bukkit.getLogger().warning("[AureliumSkills] Error parsing template " + templateType.toString() + ", check error above for details!");
        }
    }

    public ItemStack getItem(Stat stat, PlayerData playerData, Player player, Locale locale) {
        ItemStack item = baseItems.get(stat);
        if (item == null) {
            item = new ItemStack(Material.STONE);
        }
        item = item.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(applyPlaceholders(TextUtil.replace(displayName,"{color}", stat.getColor(locale),"{stat}", stat.getDisplayName(locale)), player));
            List<String> builtLore = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                Set<String> placeholders = lorePlaceholders.get(i);
                for (String placeholder : placeholders) {
                    switch (placeholder) {
                        case "stat_desc":
                            line = TextUtil.replace(line,"{stat_desc}", stat.getDescription(locale));
                            break;
                        case "skills":
                            List<Skill> skillsLeveledBy = plugin.getRewardManager().getSkillsLeveledBy(stat);
                            StringBuilder skillList = new StringBuilder();
                            for (Skill skill : skillsLeveledBy) {
                                skillList.append(skill.getDisplayName(locale)).append(", ");
                            }
                            if (skillList.length() > 1) {
                                skillList.delete(skillList.length() - 2, skillList.length());
                            }
                            if (skillsLeveledBy.size() > 0) {
                                line = TextUtil.replace(line, "{skills}", TextUtil.replace(Lang.getMessage(MenuMessage.SKILLS, locale),
                                        "{skills}", skillList.toString()));
                            } else {
                                line = TextUtil.replace(line, "{skills}", "");
                            }
                            break;
                        case "your_level":
                            line = TextUtil.replace(line,"{your_level}", TextUtil.replace(Lang.getMessage(MenuMessage.YOUR_LEVEL, locale)
                                    ,"{color}", stat.getColor(locale)
                                    ,"{level}", NumberUtil.format1(playerData.getStatLevel(stat))));
                            break;
                        case "descriptors":
                            switch (stat.name()) {
                                case "STRENGTH":
                                    double strengthLevel = playerData.getStatLevel(Stats.STRENGTH);
                                    double attackDamage = strengthLevel * OptionL.getDouble(Option.STRENGTH_MODIFIER);
                                    if (OptionL.getBoolean(Option.STRENGTH_DISPLAY_DAMAGE_WITH_HEALTH_SCALING) && !OptionL.getBoolean(Option.STRENGTH_USE_PERCENT)) {
                                        attackDamage *= OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
                                    }
                                    line = TextUtil.replace(line,"{descriptors}", TextUtil.replace(Lang.getMessage(MenuMessage.ATTACK_DAMAGE, locale)
                                            ,"{value}", NumberUtil.format2(attackDamage)));
                                    break;
                                case "HEALTH":
                                    double modifier = playerData.getStatLevel(Stats.HEALTH) * OptionL.getDouble(Option.HEALTH_MODIFIER);
                                    double scaledHealth = modifier * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
                                    line = TextUtil.replace(line,"{descriptors}", TextUtil.replace(Lang.getMessage(MenuMessage.HP, locale)
                                            ,"{value}", NumberUtil.format2(scaledHealth)));
                                    break;
                                case "REGENERATION":
                                    double regenLevel = playerData.getStatLevel(Stats.REGENERATION);
                                    double saturatedRegen = regenLevel * OptionL.getDouble(Option.REGENERATION_SATURATED_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
                                    double hungerFullRegen = regenLevel *  OptionL.getDouble(Option.REGENERATION_HUNGER_FULL_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
                                    double almostFullRegen = regenLevel *  OptionL.getDouble(Option.REGENERATION_HUNGER_ALMOST_FULL_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
                                    double manaRegen = playerData.getManaRegen();
                                    line = TextUtil.replace(line,"{descriptors}", TextUtil.replace(Lang.getMessage(MenuMessage.SATURATED_REGEN, locale),"{value}", NumberUtil.format2(saturatedRegen))
                                            + "\n" + TextUtil.replace(Lang.getMessage(MenuMessage.FULL_HUNGER_REGEN, locale),"{value}", NumberUtil.format2(hungerFullRegen))
                                            + "\n" + TextUtil.replace(Lang.getMessage(MenuMessage.ALMOST_FULL_HUNGER_REGEN, locale),"{value}", NumberUtil.format2(almostFullRegen))
                                            + "\n" + TextUtil.replace(Lang.getMessage(MenuMessage.MANA_REGEN, locale),"{value}", String.valueOf((int) manaRegen)));
                                    break;
                                case "LUCK":
                                    double luckLevel = playerData.getStatLevel(Stats.LUCK);
                                    double luck = luckLevel * OptionL.getDouble(Option.LUCK_MODIFIER);
                                    double doubleDropChance = luckLevel * OptionL.getDouble(Option.LUCK_DOUBLE_DROP_MODIFIER) * 100;
                                    if (doubleDropChance > OptionL.getDouble(Option.LUCK_DOUBLE_DROP_PERCENT_MAX)) {
                                        doubleDropChance = OptionL.getDouble(Option.LUCK_DOUBLE_DROP_PERCENT_MAX);
                                    }
                                    line = TextUtil.replace(line,"{descriptors}", TextUtil.replace(Lang.getMessage(MenuMessage.LUCK, locale),"{value}", NumberUtil.format2(luck))
                                            + "\n" + TextUtil.replace(Lang.getMessage(MenuMessage.DOUBLE_DROP_CHANCE, locale),"{value}", NumberUtil.format2(doubleDropChance)));
                                    break;
                                case "WISDOM":
                                    double wisdomLevel = playerData.getStatLevel(Stats.WISDOM);
                                    double xpModifier = wisdomLevel * OptionL.getDouble(Option.WISDOM_EXPERIENCE_MODIFIER) * 100;
                                    double anvilCostReduction = (-1.0 * Math.pow(1.025, -1.0 * wisdomLevel * OptionL.getDouble(Option.WISDOM_ANVIL_COST_MODIFIER)) + 1) * 100;
                                    double maxMana = playerData.getMaxMana();
                                    line = TextUtil.replace(line,"{descriptors}", TextUtil.replace(Lang.getMessage(MenuMessage.XP_GAIN, locale),"{value}", NumberUtil.format2(xpModifier))
                                            + "\n" + TextUtil.replace(Lang.getMessage(MenuMessage.ANVIL_COST_REDUCTION, locale),"{value}", NumberUtil.format1(anvilCostReduction)) + " "
                                            + "\n" + TextUtil.replace(Lang.getMessage(MenuMessage.MAX_MANA, locale), "{value}", NumberUtil.format1(maxMana)));
                                    break;
                                case "TOUGHNESS":
                                    double toughness = playerData.getStatLevel(Stats.TOUGHNESS) * OptionL.getDouble(Option.TOUGHNESS_NEW_MODIFIER);
                                    double damageReduction = (-1.0 * Math.pow(1.01, -1.0 * toughness) + 1) * 100;
                                    line = TextUtil.replace(line,"{descriptors}", TextUtil.replace(Lang.getMessage(MenuMessage.INCOMING_DAMAGE, locale),"{value}", NumberUtil.format2(damageReduction)));
                            }
                            break;
                    }
                }
                builtLore.add(line);
            }
            meta.setLore(ItemUtils.formatLore(applyPlaceholders(builtLore, player)));
            item.setItemMeta(meta);
        }
        return item;
    }

    public SlotPos getPos(Stat stat) {
        return positions.get(stat);
    }

}
