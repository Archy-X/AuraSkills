package com.archyx.aureliumskills.menu.items;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.util.item.ItemUtils;
import com.archyx.aureliumskills.util.text.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

public class CloseItem extends ConfigurableItem {

    public CloseItem(AureliumSkills plugin) {
        super(plugin, ItemType.CLOSE, new String[] {});
    }

    public ItemStack getItem(Player player, Locale locale) {
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(applyPlaceholders(TextUtil.replace(displayName,"{close}", Lang.getMessage(MenuMessage.CLOSE, locale)), player));
            meta.setLore(ItemUtils.formatLore(applyPlaceholders(lore, player)));
            item.setItemMeta(meta);
        }
        return item;
    }
}
