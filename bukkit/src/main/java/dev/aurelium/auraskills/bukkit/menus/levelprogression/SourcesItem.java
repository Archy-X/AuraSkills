package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.bukkit.menus.sources.SorterItem;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SourcesItem extends AbstractItem implements SingleItemProvider {

    public SourcesItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data) {
        Locale locale = plugin.getUser(player).getLocale();
        switch (placeholder) {
            case "sources":
                return plugin.getMsg(MenuMessage.SOURCES, locale);
            case "sources_desc":
                return plugin.getMsg(MenuMessage.SOURCES_DESC, locale);
            case "sources_click":
                Skill skill = (Skill) activeMenu.getProperty("skill");
                return TextUtil.replace(plugin.getMsg(MenuMessage.SOURCES_CLICK, locale),
                        "{skill}", skill.getDisplayName(locale));
        }
        return placeholder;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu activeMenu) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("skill", activeMenu.getProperty("skill"));
        properties.put("items_per_page", 28);
        properties.put("sort_type", SorterItem.SortType.ASCENDING);
        properties.put("previous_menu", "level_progression");
        plugin.getMenuManager().openMenu(player, "sources", properties, 0);
    }
}
