package com.archyx.aureliumskills.menu.items;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.item.ItemUtils;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SkullItem extends ConfigurableItem {

    public SkullItem(AureliumSkills plugin) {
        super(plugin, ItemType.SKULL, new String[] {"strength", "health", "regeneration", "luck", "wisdom", "toughness"});
    }

    public ItemStack getItem(Player player, PlayerData playerData, Locale locale) {
        ItemStack item = SkullCreator.itemFromUuid(player.getUniqueId());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(applyPlaceholders(TextUtil.replace(displayName,"{player}", player.getName()), player));
            List<String> builtLore = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                Set<String> placeholders = lorePlaceholders.get(i);
                for (String placeholder : placeholders) {
                    Stat stat = plugin.getStatRegistry().getStat(placeholder);
                    if (stat != null) {
                        line = TextUtil.replace(line, "{" + placeholder + "}", TextUtil.replace(Lang.getMessage(MenuMessage.PLAYER_STAT_ENTRY, locale)
                                , "{color}", stat.getColor(locale)
                                , "{symbol}", stat.getSymbol(locale)
                                , "{stat}", stat.getDisplayName(locale)
                                , "{level}", NumberUtil.format1(playerData.getStatLevel(stat))));
                    }
                }
                builtLore.add(line);
            }
            meta.setLore(ItemUtils.formatLore(applyPlaceholders(builtLore, player)));
            item.setItemMeta(meta);
        }
        return item;
    }
}
