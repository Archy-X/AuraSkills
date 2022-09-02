package com.archyx.aureliumskills.menus.common;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CloseItem extends AbstractItem implements SingleItemProvider {

    public CloseItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, ActiveMenu activeMenu, PlaceholderType type) {
        if (placeholder.equals("close")) {
            return Lang.getMessage(MenuMessage.CLOSE, plugin.getLang().getLocale(player));
        }
        return placeholder;
    }

    @Override
    public void onClick(@NotNull Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu activeMenu) {
        player.closeInventory();
    }
}
