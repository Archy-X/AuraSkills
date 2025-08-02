package dev.aurelium.auraskills.api.loot;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class LootOptioned {

    protected final Map<String, Object> options;
    private final LootRequirements requirements;

    public LootOptioned(Map<String, Object> options, LootRequirements requirements) {
        this.options = options;
        this.requirements = requirements;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    @Nullable
    public <T> T getOption(String key, Class<T> type) {
        Object o = options.get(key);
        if (o == null) return null;
        return type.cast(o);
    }

    public <T> T getOption(String key, Class<T> type, T def) {
        Object o = options.get(key);
        if (o == null) return def;
        return type.cast(o);
    }

    public boolean checkRequirements(UUID uuid) {
        return requirements.checkByUuid(uuid);
    }

}
