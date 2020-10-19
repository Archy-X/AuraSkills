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
                String[] splitInput = materialInput.split(" ");
                Skill skill = Skill.valueOf(splitInput[0]);
                baseItems.put(skill, MenuLoader.parseItem(splitInput[1]));
            }
            displayName = Objects.requireNonNull(config.getString("display_name")).replace('&', '§');
            // Load lore
            List<String> lore = new ArrayList<>();
            Map<Integer, Set<String>> lorePlaceholders = new HashMap<>();
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
            meta.setDisplayName(displayName.replace("{skill}", skill.getDisplayName(locale))
                    .replace("{level}", RomanNumber.toRoman(skillLevel)));
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
                            line = line.replace("{primary_stat}", Lang.getMessage(MenuMessage.PRIMARY_STAT, locale)
                                    .replace("{color}", primaryStat.getColor(locale))
                                    .replace("{stat}", primaryStat.getDisplayName(locale)));
                            break;
                        case "secondary_stat":
                            Stat secondaryStat = skill.getSecondaryStat();
                            line = line.replace("{secondary_stat}", Lang.getMessage(MenuMessage.SECONDARY_STAT, locale)
                                    .replace("{color}", secondaryStat.getColor(locale))
                                    .replace("{stat}", secondaryStat.getDisplayName(locale)));
                            break;
                        case "ability_levels":
                            if (skill.getAbilities().size() == 5) {
                                line = LoreUtil.setPlaceholders("ability_levels", MenuMessage.ABILITY_LEVELS, locale, line);
                                int num = 1;
                                for (Supplier<Ability> abilitySupplier : skill.getAbilities()) {
                                    Ability ability = abilitySupplier.get();
                                    if (playerSkill.getAbilityLevel(ability) > 0) {
                                        int abilityLevel = playerSkill.getAbilityLevel(ability);
                                        line = line.replace("{ability_" + num + "}", Lang.getMessage(MenuMessage.ABILITY_LEVEL_ENTRY, locale)
                                                .replace("{ability}", ability.getDisplayName(locale))
                                                .replace("{level}", RomanNumber.toRoman(playerSkill.getAbilityLevel(ability)))
                                                .replace("{info}", ability.getInfo(locale)
                                                        .replace("{value}", nf.format(ability.getValue(abilityLevel)))));
                                    }
                                    else {
                                        line = line.replace("{ability_" + num + "}", Lang.getMessage(MenuMessage.ABILITY_LEVEL_ENTRY_LOCKED, locale)
                                                .replace("{ability}", ability.getDisplayName(locale)));
                                    }
                                    num++;
                                }
                            }
                            else {
                                line = line.replace("{ability_levels}", "");
                            }
                            break;
                        case "mana_ability":
                            MAbility mAbility = skill.getManaAbility();
                            int level = playerSkill.getManaAbilityLevel(mAbility);
                            if (mAbility != MAbility.ABSORPTION && level > 0) {
                                line = line.replace("{mana_ability}", Lang.getMessage(MenuMessage.MANA_ABILITY, locale)
                                        .replace("{mana_ability}", mAbility.getDisplayName(locale))
                                        .replace("{level}", RomanNumber.toRoman(level))
                                        .replace("{duration}", nf.format(mAbility.getValue(level)))
                                        .replace("{mana_cost}", String.valueOf(mAbility.getManaCost(level)))
                                        .replace("{cooldown}", nf.format(mAbility.getCooldown(level))));
                            }
                            else {
                                line = line.replace("{mana_ability}", "");
                            }
                            break;
                        case "level":
                            line = line.replace("{level}", Lang.getMessage(MenuMessage.LEVEL, locale).replace("{level}", RomanNumber.toRoman(skillLevel)));
                            break;
                        case "progress_to_level":
                            if (skillLevel < OptionL.getMaxLevel(skill)) {
                                double currentXp = playerSkill.getXp(skill);
                                double xpToNext = Leveler.levelReqs.get(skillLevel - 1);
                                line = line.replace("{progress_to_level}", Lang.getMessage(MenuMessage.PROGRESS_TO_LEVEL, locale)
                                        .replace("{level}", RomanNumber.toRoman(skillLevel))
                                        .replace("{percent}", nf2.format(currentXp / xpToNext * 100))
                                        .replace("{current_xp}", nf2.format(currentXp))
                                        .replace("{level_xp}", String.valueOf((int) xpToNext)));
                            }
                            else {
                                line = line.replace("{progress_to_level}", "");
                            }
                            break;
                        case "max_level":
                            if (skillLevel >= OptionL.getMaxLevel(skill)) {
                                line = line.replace("{max_level}", Lang.getMessage(MenuMessage.MAX_LEVEL, locale));
                            }
                            else {
                                line = line.replace("{max_level}", "");
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
