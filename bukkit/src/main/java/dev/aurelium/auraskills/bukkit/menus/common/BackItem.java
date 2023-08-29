package dev.aurelium.auraskills.bukkit.menus.common;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.util.text.Replacer;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BackItem extends AbstractItem implements SingleItemProvider {

    public BackItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data) {
        return replaceMenuMessage(placeholder, player, activeMenu, new Replacer()
                .map("{menu_name}", () -> {
                    String previousMenu = (String) activeMenu.getProperty("previous_menu");
                    return TextUtil.capitalizeWord(TextUtil.replace(previousMenu, "_", " "));
                }));
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu activeMenu) {
        Object object = activeMenu.getProperty("previous_menu");
        if (object != null) {
            String previousMenu = (String) object;
            plugin.getMenuManager().openMenu(player, previousMenu);
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
