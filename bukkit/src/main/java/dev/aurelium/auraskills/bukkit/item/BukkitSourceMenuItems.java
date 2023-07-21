package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import dev.aurelium.auraskills.common.source.SourceMenuItems;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

public class BukkitSourceMenuItems extends SourceMenuItems<ItemStack> {

    private final AuraSkills plugin;

    public BukkitSourceMenuItems(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void parseAndRegisterMenuItem(XpSource source, ConfigurationNode config) {
        ConfigurateItemParser parser = new ConfigurateItemParser(plugin);
        try {
            ItemStack itemStack = parser.parseItem(config);

            registerMenuItem(source, itemStack);
        } catch (Exception e) {
            plugin.logger().info("Error parsing source menu item for source " + source.getId());
            e.printStackTrace();
        }
    }
}
