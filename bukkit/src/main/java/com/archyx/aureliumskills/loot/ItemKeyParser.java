package com.archyx.aureliumskills.loot;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.util.misc.Parser;
import com.archyx.lootmanager.loot.parser.CustomItemParser;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemKeyParser extends Parser implements CustomItemParser {

    private final AureliumSkills plugin;

    public ItemKeyParser(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean shouldUseParser(Map<?, ?> map) {
        return map.containsKey("key");
    }

    @Override
    public ItemStack parseCustomItem(Map<?, ?> map) {
        String itemKey = getString(map, "key");
        ItemStack item = plugin.getItemRegistry().getItem(itemKey);
        if (item != null) {
            return item;
        } else {
            throw new IllegalArgumentException("Item with key " + itemKey + " not found in item registry");
        }
    }
}
