package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.common.source.parser.ParsingExtension;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceTypeRegistry {

    private final Map<NamespacedId, SourceType> sourceTypes;
    private final Map<SourceType, List<ParsingExtension>> parsingExtensions;

    public SourceTypeRegistry() {
        this.sourceTypes = new HashMap<>();
        this.parsingExtensions = new HashMap<>();
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
            sourceTypes.put(defaultType.getId(), defaultType);
        }
    }

    @NotNull
    public List<ParsingExtension> getParsingExtensions(SourceType sourceType) {
        return parsingExtensions.getOrDefault(sourceType, new ArrayList<>());
    }

    public void registerParsingExtension(SourceType sourceType, ParsingExtension extension) {
        List<ParsingExtension> extensions = parsingExtensions.getOrDefault(sourceType, new ArrayList<>());
        extensions.add(extension);
        parsingExtensions.put(sourceType, extensions);
    }

}
