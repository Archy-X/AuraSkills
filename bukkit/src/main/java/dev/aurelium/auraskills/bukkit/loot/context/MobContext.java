package dev.aurelium.auraskills.bukkit.loot.context;

import dev.aurelium.auraskills.api.loot.LootContext;
import org.bukkit.entity.EntityType;

import java.util.Locale;

public record MobContext(EntityType entityType) implements LootContext {

    @Override
    public String getName() {
        return entityType.toString().toLowerCase(Locale.ROOT);
    }
}
