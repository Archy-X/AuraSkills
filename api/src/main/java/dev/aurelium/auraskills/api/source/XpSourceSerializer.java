package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public abstract class XpSourceSerializer<T> extends BaseSerializer implements TypeSerializer<T> {

    protected final AuraSkillsApi auraSkills;
    protected final SourceType sourceType;
    private final String sourceName;

    public XpSourceSerializer(AuraSkillsApi auraSkills, SourceType sourceType, String sourceName) {
        super(auraSkills);
        this.auraSkills = auraSkills;
        this.sourceType = sourceType;
        this.sourceName = sourceName;
    }

    protected SourceValues parseValues(ConfigurationNode node) {
        return new SourceValues(sourceType, getId(), getXp(node), getDisplayName(node));
    }

    private NamespacedId getId() {
        return NamespacedId.of(NamespacedId.AURASKILLS, sourceName);
    }

    private double getXp(ConfigurationNode source) {
        return source.node("xp").getDouble(0.0);
    }

    private String getDisplayName(ConfigurationNode source) {
        return source.node("display_name").getString();
    }

    @Override
    public void serialize(Type type, @Nullable T obj, ConfigurationNode node) {
        // Source files are read only so we don't need to serialize
    }

}
