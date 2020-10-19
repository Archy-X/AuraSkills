package com.archyx.aureliumskills.menu.items;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menu.MenuLoader;
import com.archyx.aureliumskills.util.LoreUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;
import java.util.Objects;

public class CloseItem implements ConfigurableItem {

    private final ItemType TYPE = ItemType.CLOSE;

    private SlotPos pos;
    private ItemStack baseItem;
    private String displayName;

    @Override
    public ItemType getType() {
        return TYPE;
    }

    @Override
    public void load(ConfigurationSection config) {
        try {
            pos = SlotPos.of(config.getInt("row"), config.getInt("column"));
            baseItem = MenuLoader.parseItem(Objects.requireNonNull(config.getString("material")));
            displayName = LoreUtil.replace(Objects.requireNonNull(config.getString("display_name")),"&", "§");
        }
        catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("[AureliumSkills] Error parsing item " + TYPE.toString() + ", check error above for details!");
        }
    }

    public ItemStack getItem(Locale locale) {
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(LoreUtil.replace(displayName,"{close}", Lang.getMessage(MenuMessage.CLOSE, locale)));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public SlotPos getPos() {
        return pos;
    }
}
