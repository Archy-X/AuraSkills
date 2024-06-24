package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import dev.aurelium.auraskills.common.source.SourceMenuItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Objects;

public class BukkitSourceMenuItems extends SourceMenuItems<ItemStack> {

    private final AuraSkills plugin;

    public BukkitSourceMenuItems(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void parseAndRegisterMenuItem(XpSource source, ConfigurationNode config) {
        if (config.isMap()) {
            // Explicit item map
            ConfigurateItemParser parser = new ConfigurateItemParser(plugin);
            try {
                ItemStack itemStack;
                try {
                    itemStack = parser.parseItem(config);
                } catch (IllegalArgumentException e) {
                    itemStack = new ItemStack(getFallbackMaterial(source));
                }

                registerMenuItem(source, itemStack);
            } catch (Exception e) {
                plugin.logger().info("Error parsing source menu item for source " + source.getId());
                e.printStackTrace();
            }
        } else {
            // Get item from external plugin or item registry
            String itemId = config.getString();
            if (itemId == null) {
                registerMenuItem(source, new ItemStack(getFallbackMaterial(source)));
                return;
            }

            String[] split = itemId.split(":", 2);

            if (split.length >= 2) {
                // Has namespace
                String namespace = split[0];
                String key = split[1];

                ExternalItemProvider provider = plugin.getItemRegistry().getExternalItemProvider(namespace);
                if (provider == null) {
                    registerMenuItem(source, new ItemStack(getFallbackMaterial(source)));
                    return;
                }

                ItemStack item = provider.getItem(key);
                registerMenuItem(source, Objects.requireNonNullElseGet(item, () -> new ItemStack(getFallbackMaterial(source))));
            } else {
                // Try get from built-in item registry
                ItemStack item = plugin.getItemRegistry().getItem(NamespacedId.fromDefault(split[0]));
                registerMenuItem(source, Objects.requireNonNullElseGet(item, () -> new ItemStack(getFallbackMaterial(source))));
            }
        }
    }

    private Material getFallbackMaterial(XpSource source) {
        if (source instanceof EntityXpSource entity) {
            return switch (entity.getEntity()) {
                case "wither" -> Material.WITHER_SKELETON_SKULL;
                case "ender_dragon" -> Material.DRAGON_HEAD;
                case "iron_golem" -> Material.IRON_BLOCK;
                case "snow_golem" -> Material.SNOW_BLOCK;
                default -> Material.STONE;
            };
        }
        return Material.STONE;
    }

}
