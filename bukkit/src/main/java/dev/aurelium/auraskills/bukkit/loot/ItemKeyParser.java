package dev.aurelium.auraskills.bukkit.loot;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.loot.CustomItemParser;
import dev.aurelium.auraskills.common.ref.ItemRef;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import static dev.aurelium.auraskills.bukkit.ref.BukkitItemRef.wrap;

public class ItemKeyParser implements CustomItemParser {

    private final AuraSkills plugin;

    public ItemKeyParser(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean shouldUseParser(ConfigurationNode config) {
        return !config.node("key").virtual();
    }

    @Override
    public ItemRef parseCustomItem(ConfigurationNode config) {
        NamespacedId itemKey = NamespacedId.fromDefault(config.node("key").getString(""));
        ItemStack item = plugin.getItemRegistry().getItem(itemKey);
        if (item != null) {
            return wrap(item);
        } else {
            throw new IllegalArgumentException("Item with key " + itemKey + " not found in item registry");
        }
    }

}
