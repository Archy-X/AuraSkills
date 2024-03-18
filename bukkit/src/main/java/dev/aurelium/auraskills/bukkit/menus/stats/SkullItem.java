package dev.aurelium.auraskills.bukkit.menus.stats;

import com.archyx.slate.item.provider.ListBuilder;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Locale;

public class SkullItem extends AbstractItem implements SingleItemProvider {

    public SkullItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data) {
        Locale locale = plugin.getUser(player).getLocale();
        if (placeholder.equals("player")) {
            return player.getName();
        } else if (placeholder.equals("entries")) {
            User user = plugin.getUser(player);
            // Handle each stat entry
            ListBuilder builder = new ListBuilder(data.getListData());

            for (Stat stat : plugin.getStatManager().getEnabledStats()) {
                String entry = activeMenu.getFormat("player_stat_entry");
                entry = TextUtil.replace(entry,
                        "{color}", stat.getColor(locale),
                        "{symbol}", stat.getSymbol(locale),
                        "{stat}", stat.getDisplayName(locale),
                        "{level}", new StatDisplayHelper(plugin).getDisplayLevel(stat, user));
                builder.append(entry);
            }

            return builder.build();
        }
        return replaceMenuMessage(placeholder, player, activeMenu);
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu) {
        if (baseItem.getItemMeta() instanceof SkullMeta meta) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
            baseItem.setItemMeta(meta);
        }
        return baseItem;
    }
}
