package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;

import java.util.HashMap;
import java.util.Map;

public class SourceTypeRegistry {

    private final AuraSkillsPlugin plugin;
    private final Map<NamespacedId, SourceType> sourceTypes;

    public SourceTypeRegistry(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.sourceTypes = new HashMap<>();
    }

    public SourceType get(NamespacedId id) throws IllegalArgumentException {
        SourceType sourceType = sourceTypes.get(id);
        if (sourceType != null) {
            return sourceType;
        } else {
            throw new IllegalArgumentException("Source type with id " + id + " does not exist");
        }
    }

    public void register(NamespacedId id, SourceType sourceType) {
        this.sourceTypes.put(id, sourceType);
    }

    public void unregister(NamespacedId id) {
        this.sourceTypes.remove(id);
    }

    public void registerDefaults() {
        for (SourceTypes defaultType : SourceTypes.values()) {
            sourceTypes.put(NamespacedId.of(NamespacedId.AURASKILLS, defaultType.getName()), defaultType);
        }
    }

}
