package com.archyx.aureliumskills.menus.common;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BackItem extends AbstractItem implements SingleItemProvider {

    public BackItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderType type) {
        Locale locale = plugin.getLang().getLocale(player);
        switch (placeholder) {
            case "back":
                return Lang.getMessage(MenuMessage.BACK, locale);
            case "back_click":
                String previousMenu = (String) activeMenu.getProperty("previous_menu");
                String formattedPreviousMenu = WordUtils.capitalize(TextUtil.replace(previousMenu, "_", " "));
                return TextUtil.replace(Lang.getMessage(MenuMessage.BACK_CLICK, locale),
                        "{menu_name}", formattedPreviousMenu);
        }
        return placeholder;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu activeMenu) {
        Object object = activeMenu.getProperty("previous_menu");
        if (object != null) {
            String previousMenu = (String) object;
            if (previousMenu.equals("level_progression")) {
                Skill skill = (Skill) activeMenu.getProperty("skill");
                Map<String, Object> properties = new HashMap<>();
                properties.put("skill", skill);
                properties.put("items_per_page", 24);
                properties.put("previous_menu", "skills");
                plugin.getSlate().getMenuManager().openMenu(player, previousMenu, properties, 1);
            } else {
                plugin.getSlate().getMenuManager().openMenu(player, previousMenu);
            }
        }
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu) {
        if (activeMenu.getProperty("previous_menu") == null) {
            return null;
        }
        return baseItem;
    }
}
