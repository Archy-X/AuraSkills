package com.archyx.aureliumskills.menus.common;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class NextPageItem extends AbstractItem implements SingleItemProvider {

    public NextPageItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data) {
        Locale locale = plugin.getLang().getLocale(player);
        switch (placeholder) {
            case "next_page":
                return Lang.getMessage(MenuMessage.NEXT_PAGE, locale);
            case "next_page_click":
                return Lang.getMessage(MenuMessage.NEXT_PAGE_CLICK, locale);
        }
        return placeholder;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu activeMenu) {
        plugin.getMenuManager().openMenu(player, activeMenu.getName(), activeMenu.getProperties(), activeMenu.getCurrentPage() + 1);
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu) {
        if (activeMenu.getCurrentPage() == activeMenu.getTotalPages() - 1) {
            return null;
        }
        return baseItem;
    }
}
