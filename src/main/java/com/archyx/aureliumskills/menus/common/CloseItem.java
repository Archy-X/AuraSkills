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

public class CloseItem extends AbstractItem implements SingleItemProvider {

    public CloseItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data) {
        if (placeholder.equals("close")) {
            return Lang.getMessage(MenuMessage.CLOSE, plugin.getLang().getLocale(player));
        }
        return placeholder;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu activeMenu) {
        player.closeInventory();
    }
}
