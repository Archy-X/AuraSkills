package dev.aurelium.auraskills.bukkit.loot.entity;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public record EntityProperties(
        String entityId,
        String name,
        Integer level,
        Double health,
        Double damage,
        Float horizontalVelocity,
        Float verticalVelocity,
        Map<EquipmentSlot, ItemStack> equipment
) {

    public static EntityProperties fromConfig(ConfigurationNode config, AuraSkills plugin) {
        String[] id = config.node("entity").getString("").split(":");

        // Parse equipment items for each slot
        var itemParser = new ConfigurateItemParser(plugin);

        Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ConfigurationNode itemNode = config.node(slot.toString().toLowerCase(Locale.ROOT));
            if (itemNode.empty()) continue;

            ItemStack item = itemParser.parseItem(itemNode);
            equipment.put(slot, item);
        }

        return new EntityProperties(
                id.length > 1 ? id[1] : id[0],
                config.node("name").getString(),
                config.node("level").empty() ? null : config.node("level").getInt(),
                config.node("health").empty() ? null : config.node("health").getDouble(),
                config.node("damage").empty() ? null : config.node("damage").getDouble(),
                config.node("velocity", "horizontal").empty() ? null : config.node("velocity", "horizontal").getFloat(),
                config.node("velocity", "vertical").empty() ? null : config.node("velocity", "vertical").getFloat(),
                equipment
        );
    }
}
