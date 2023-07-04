package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.type.AnvilSource;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class AnvilSourceSerializer extends SourceSerializer<AnvilSource> {

    public AnvilSourceSerializer(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public AnvilSource deserialize(Type type, ConfigurationNode source) throws SerializationException {
        ItemFilter leftItem = required(source, "left_item").get(ItemFilter.class);
        ItemFilter rightItem = required(source, "right_item").get(ItemFilter.class);
        String multiplier = source.node("multiplier").getString();

        return new AnvilSource(plugin, getId(source), getXp(source), leftItem, rightItem, multiplier);
    }
}
