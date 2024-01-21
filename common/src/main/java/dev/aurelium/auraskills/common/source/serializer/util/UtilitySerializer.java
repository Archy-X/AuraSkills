package dev.aurelium.auraskills.common.source.serializer.util;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.source.BaseSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public abstract class UtilitySerializer<T> extends BaseSerializer implements TypeSerializer<T> {

    public UtilitySerializer(AuraSkillsApi auraSkills) {
        super(auraSkills);
    }

    @Override
    public void serialize(Type type, @Nullable T obj, ConfigurationNode node) throws SerializationException {

    }
}
