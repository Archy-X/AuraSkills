package com.archyx.aureliumskills.menu.templates;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.item.ItemUtils;
import com.archyx.aureliumskills.util.item.LoreUtil;
import com.archyx.aureliumskills.util.math.RomanNumber;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class UnlockedTemplate extends ConfigurableTemplate {

    private final ProgressLevelItem levelItem;

    public UnlockedTemplate(AureliumSkills plugin) {
        super(plugin, TemplateType.UNLOCKED, new String[] {"level_number", "rewards", "ability", "mana_ability", "unlocked"});
        this.levelItem = new ProgressLevelItem(plugin);
    }

    public ItemStack getItem(Skill skill, int level, Player player, Locale locale) {
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(applyPlaceholders(LoreUtil.replace(displayName,"{level_unlocked}", LoreUtil.replace(Lang.getMessage(MenuMessage.LEVEL_UNLOCKED, locale),"{level}", RomanNumber.toRoman(level))), player));
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
                            line = LoreUtil.replace(line,"{rewards}", levelItem.getRewardsLore(skill, level, locale));
                            break;
                        case "ability":
                            line = LoreUtil.replace(line,"{ability}", levelItem.getAbilityLore(skill, level, locale));
                            break;
                        case "mana_ability":
                            line = LoreUtil.replace(line, "{mana_ability}", levelItem.getManaAbilityLore(skill, level, locale));
                            break;
                        case "unlocked":
                            line = LoreUtil.replace(line,"{unlocked}", Lang.getMessage(MenuMessage.UNLOCKED, locale));
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


}
