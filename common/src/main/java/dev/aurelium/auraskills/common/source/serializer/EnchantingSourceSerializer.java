package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.EnchantingSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class EnchantingSourceSerializer extends SourceSerializer<EnchantingSource> {

    public EnchantingSourceSerializer(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public EnchantingSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        ItemFilter item = required(source, "item").get(ItemFilter.class);

        return new EnchantingSource(plugin, getId(source), getXp(source), item);
    }
}
