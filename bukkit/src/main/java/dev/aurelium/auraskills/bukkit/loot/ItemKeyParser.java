package dev.aurelium.auraskills.bukkit.loot;

import com.archyx.lootmanager.loot.parser.CustomItemParser;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.util.data.DataUtil;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemKeyParser implements CustomItemParser {

    private final AuraSkills plugin;

    public ItemKeyParser(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean shouldUseParser(Map<?, ?> map) {
        return map.containsKey("key");
    }

    @Override
    public ItemStack parseCustomItem(Map<?, ?> map) {
        NamespacedId itemKey = NamespacedId.fromDefault(DataUtil.getString(map, "key"));
        ItemStack item = plugin.getItemRegistry().getItem(itemKey);
        if (item != null) {
            return item;
        } else {
            throw new IllegalArgumentException("Item with key " + itemKey + " not found in item registry");
        }
    }
}
