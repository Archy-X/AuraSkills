package dev.aurelium.auraskills.bukkit.menus.common;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class BackItem extends AbstractItem implements SingleItemProvider {

    public BackItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data) {
        Locale locale = plugin.getUser(player).getLocale();
        switch (placeholder) {
            case "back":
                return plugin.getMsg(MenuMessage.BACK, locale);
            case "back_click":
                String previousMenu = (String) activeMenu.getProperty("previous_menu");
                String formattedPreviousMenu = TextUtil.capitalizeWord(TextUtil.replace(previousMenu, "_", " "));
                return TextUtil.replace(plugin.getMsg(MenuMessage.BACK_CLICK, locale),
                        "{menu_name}", formattedPreviousMenu);
        }
        return placeholder;
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
