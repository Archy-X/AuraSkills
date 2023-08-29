package dev.aurelium.auraskills.bukkit.menus.common;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class NextPageItem extends AbstractItem implements SingleItemProvider {

    public NextPageItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data) {
        return replaceMenuMessage(placeholder, player, activeMenu);
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
