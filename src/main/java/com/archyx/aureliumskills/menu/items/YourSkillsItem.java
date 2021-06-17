package com.archyx.aureliumskills.menu.items;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.util.item.ItemUtils;
import com.archyx.aureliumskills.util.text.TextUtil;
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
            meta.setDisplayName(applyPlaceholders(TextUtil.replace(displayName,"{your_skills}", TextUtil.replace(Lang.getMessage(MenuMessage.YOUR_SKILLS, locale)
                    ,"{player}", player.getName())), player));
            List<String> builtLore = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                Set<String> placeholders = lorePlaceholders.get(i);
                for (String placeholder : placeholders) {
                    switch (placeholder) {
                        case "desc":
                            line = TextUtil.replace(line, "{desc}", Lang.getMessage(MenuMessage.YOUR_SKILLS_DESC, locale));
                            break;
                        case "hover":
                            line = TextUtil.replace(line, "{hover}", Lang.getMessage(MenuMessage.YOUR_SKILLS_HOVER, locale));
                            break;
                        case "click":
                            line = TextUtil.replace(line, "{click}", Lang.getMessage(MenuMessage.YOUR_SKILLS_CLICK, locale));
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
