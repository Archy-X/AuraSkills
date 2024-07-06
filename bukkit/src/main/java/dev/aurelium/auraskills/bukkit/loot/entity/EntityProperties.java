package dev.aurelium.auraskills.bukkit.loot.entity;

import org.spongepowered.configurate.ConfigurationNode;

public record EntityProperties(String entityId,
                               String name,
                               Integer level,
                               Double health,
                               Double damage,
                               Float horizontalVelocity,
                               Float verticalVelocity) {

    public static EntityProperties fromConfig(ConfigurationNode config) {
        String[] id = config.node("entity").getString("").split(":");

        return new EntityProperties(
                id.length > 1 ? id[1] : id[0],
                config.node("name").getString(),
                config.node("level").empty() ? null : config.node("level").getInt(),
                config.node("health").empty() ? null : config.node("health").getDouble(),
                config.node("damage").empty() ? null : config.node("damage").getDouble(),
                config.node("velocity", "horizontal").empty() ? null : config.node("velocity", "horizontal").getFloat(),
                config.node("velocity", "vertical").empty() ? null : config.node("velocity", "vertical").getFloat()
        );
    }
}
