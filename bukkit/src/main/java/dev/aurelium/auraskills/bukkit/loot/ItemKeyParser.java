package dev.aurelium.auraskills.bukkit.loot;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.loot.parser.CustomItemParser;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

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
    public ItemStack parseCustomItem(ConfigurationNode config) {
        NamespacedId itemKey = NamespacedId.fromDefault(config.node("key").getString(""));
        ItemStack item = plugin.getItemRegistry().getItem(itemKey);
        if (item != null) {
            return item;
        } else {
            throw new IllegalArgumentException("Item with key " + itemKey + " not found in item registry");
        }
    }
}
