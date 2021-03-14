package com.archyx.aureliumskills.menu.items;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.util.ItemUtils;
import com.archyx.aureliumskills.util.LoreUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class YourSkillsItem extends ConfigurableItem {

    public YourSkillsItem(AureliumSkills plugin) {
        super(plugin, ItemType.YOUR_SKILLS, new String[] {"desc", "hover", "click"});
    }

    public ItemStack getItem(Player player, Locale locale) {
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(applyPlaceholders(LoreUtil.replace(displayName,"{your_skills}", LoreUtil.replace(Lang.getMessage(MenuMessage.YOUR_SKILLS, locale)
                    ,"{player}", player.getName())), player));
            List<String> builtLore = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                Set<String> placeholders = lorePlaceholders.get(i);
                for (String placeholder : placeholders) {
                    switch (placeholder) {
                        case "desc":
                            line = LoreUtil.setPlaceholders("desc", MenuMessage.YOUR_SKILLS_DESC, locale, line);
                            break;
                        case "hover":
                            line = LoreUtil.setPlaceholders("hover", MenuMessage.YOUR_SKILLS_HOVER, locale, line);
                            break;
                        case "click":
                            line = LoreUtil.setPlaceholders("click", MenuMessage.YOUR_SKILLS_CLICK, locale, line);
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
